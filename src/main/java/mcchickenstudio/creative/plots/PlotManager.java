/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package mcchickenstudio.creative.plots;

import mcchickenstudio.creative.coding.CodeScript;
import mcchickenstudio.creative.utils.*;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.entity.Player;
import mcchickenstudio.creative.Main;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mcchickenstudio.creative.utils.ErrorUtils.sendPlayerErrorMessage;
import static mcchickenstudio.creative.utils.FileUtils.*;
import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.*;

public class PlotManager {

    private static PlotManager plotManager;

    private PlotManager() {}
    public static PlotManager getInstance() {
        if (plotManager == null) {
            plotManager = new PlotManager();
        }
        return plotManager;
    }

    private final Set<Plot> plots = new HashSet<>();
    private final Set<Plot> corruptedPlots = new HashSet<>();

    public Set<Plot> getPlots() {
        return plots;
    }

    public Set<Plot> getCorruptedPlots() {
        return corruptedPlots;
    }

    public void registerPlot(Plot plot) {
        if (plot.isCorrupted()) {
            corruptedPlots.add(plot);
        } else {
            plots.add(plot);
        }
    }

    /**
     * Creates and loads a new plot for player with specified world generation parameters.
     * @param owner Owner of plot.
     * @param id Id of plot.
     * @param generator Generator of world.
     * @param environment Environment of world.
     * @param seed Seed for generation.
     * @param generateStructures Generate or not generate structures.
     */
    public void createPlot(Player owner, int id, WorldUtils.WorldGenerator generator, World.Environment environment, long seed, boolean generateStructures) {
        owner.sendTitle(getLocaleMessage("creating-world.title"),getLocaleMessage("creating-world.subtitle"),10,300,40);
        Main.getPlugin().getLogger().info("Creating new plot " + id + " by " + owner.getName() + "...");

        createWorldSettings(id, false, owner, environment);
        Plot plot = new Plot(id);

        FileUtils.loadWorldFolder(plot.getWorldName(),true);
        if (plot.generateWorld(generator,environment,seed,generateStructures) != null) {
            plot.connectPlayer(owner);
            plot.getWorld().getSpawnLocation().getChunk().load(true);
            owner.sendTitle(getLocaleMessage("creating-world.welcome-title",owner),getLocaleMessage("creating-world.welcome-subtitle",owner),15,180,45);
            owner.playSound(owner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE,100,0.1f);
            owner.sendMessage(getLocaleMessage("creating-world.welcome",owner));
            owner.setGameMode(GameMode.CREATIVE);
            ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
            owner.getInventory().setItem(8,worldSettingsItem);
        } else {
            sendPlayerErrorMessage(owner,"Failed to create world, world is null.");
        }

    }

    public void clearPlots() {
        plots.clear();
    }

    /**
     Load plot, for example if player tries to join it. It loads world folder, world and code script.
     **/
    public void loadPlot(Plot plot) {
        FileUtils.loadWorldFolder(plot.getWorldName(),true);
        World world = new WorldCreator(plot.getWorldName()).environment(plot.getEnvironment()).keepSpawnLoaded(TriState.FALSE).createWorld();
        if (world == null) return;
        plot.setWorld(world);
        plot.getWorld().setAutoSave(true);
        plot.getWorld().setKeepSpawnInMemory(false);
        if (world.getEnvironment() == World.Environment.THE_END) {
            if (world.getEnderDragonBattle() != null) {
                world.getEnderDragonBattle().setPreviouslyKilled(true);
                world.getEnderDragonBattle().getBossBar().setVisible(false);
            }
        }
        plot.setScript(new CodeScript(plot, FileUtils.getPlotScriptFile(plot)));
        FileUtils.setPlotConfigParameter(plot,"last-activity-time",System.currentTimeMillis());
        plot.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
        plot.getWorld().getWorldBorder().setSize(getPlayerPlotSize(plot.getOwnerGroup()));
        plot.getVariables().load();
    }


    /**
    Unload plot, for example if no players playing in plot.
     **/
    public void unloadPlot(Plot plot) {
        plot.getVariables().save();
        FileUtils.setPlotConfigParameter(plot,"last-activity-time",System.currentTimeMillis());
        FileUtils.setPlotConfigParameter(plot,"mode", plot.getMode());
        FileUtils.setPlotConfigParameter(plot,"environment",plot.getEnvironment().name());
        for (Player player : plot.getPlayers()) {
            teleportToLobby(player);
        }
        plot.stopBukkitRunnables();
        if (Bukkit.unloadWorld(plot.getWorldName(),true)) {
            FileUtils.unloadWorldFolder(plot.getWorldName(),true);
            if (Bukkit.getWorld(plot.getDevPlot().worldName) != null) {
                for (Player player : plot.getDevPlot().world.getPlayers()) {
                    teleportToLobby(player);
                }
                plot.getDevPlot().setLoaded(false);
                if (Bukkit.unloadWorld(plot.getDevPlot().worldName,true)) {
                    FileUtils.unloadWorldFolder(plot.getDevPlot().worldName,true);
                }
            }
        }
    }

    /**
     Returns plots, these player owns.
     **/
    public List<Plot> getPlayerPlots(Player player) {
        List<Plot> playerPlots = new ArrayList<>();
        for (Plot plot : PlotManager.getInstance().getPlots()) {
            if (plot.getOwner().equalsIgnoreCase(player.getName())) {
                playerPlots.add(plot);
            }
        }
        return playerPlots;
    }

    /**
     Delete plot. It teleports plot players to spawn, closes plot, unloads world and deletes world folder.
     **/
    public void deletePlot(Plot plot) {
        try {
            // Телепортирует всех игроков в мире на спавн
            for (Player p : plot.getPlayers()) {
                PlayerUtils.teleportToLobby(p);
            }
            if (plot.getDevPlot().exists()) {
                FileUtils.deleteWorld(FileUtils.getDevPlotFolder(plot.getDevPlot()));
            }
            // Удаляет папку мира
            plot.setPlotSharing(Plot.Sharing.CLOSED);
            plots.remove(plot);
            FileUtils.deleteWorld(FileUtils.getPlotFolder(plot));

            // После 3 секунд удаления мир отгружается полностью
            Bukkit.getServer().getScheduler().runTaskLater(Main.getPlugin(), () -> {
                Bukkit.unloadWorld(plot.getWorldName(),false);
                if (plot.getDevPlot().isLoaded()) Bukkit.unloadWorld(plot.getDevPlot().worldName, false);
            }, 60);
        } catch (NullPointerException error) {
            ErrorUtils.sendCriticalErrorMessage("При удалении мира возникла ошибка: " + error.getMessage());
        }
    }

    /**
     Delete plot on player request. It teleports plot players to spawn, closes plot, unloads world and deletes world folder.
     **/
    public void deletePlot(Plot plot, Player player) {
        try {
            // Телепортирует всех игроков в мире на спавн
            for (Player p : plot.getPlayers()) {
                PlayerUtils.teleportToLobby(p);
            }
            // Удаляет папку мира
            plot.setPlotSharing(Plot.Sharing.CLOSED);
            plots.remove(plot);
            FileUtils.deleteWorld(FileUtils.getPlotFolder(plot));
            if (plot.getDevPlot() != null) {
                FileUtils.deleteWorld(FileUtils.getDevPlotFolder(plot.getDevPlot()));
            }
            // После 3 секунд удаления мир отгружается полностью
            Bukkit.getServer().getScheduler().runTaskLater(Main.getPlugin(), () -> {
                Bukkit.unloadWorld(plot.getWorldName(),false);
                player.sendMessage(MessageUtils.getLocaleMessage("deleting-world.message"));
            }, 60);
        } catch (NullPointerException error) {
            ErrorUtils.sendCriticalErrorMessage("При удалении мира возникла ошибка: " + error.getMessage());
        }
    }

    public List<Plot> getRecommendedPlots() {
        List<Plot> featuredPlots = new ArrayList<>();
        Set<Integer> featuredIds = Main.getSettings().getRecommendedWorldsIDs();
        for (int id : featuredIds) {
            Plot plot = getPlotByWorldName("plot"+id);
            if (plot != null) {
                featuredPlots.add(plot);
            }
        }
        return featuredPlots;
    }


    /**
     Returns plots that contains specified name.
     **/
    public Set<Plot> getPlotsByPlotName(String worldName) {
        Set<Plot> foundPlots = new HashSet<>();
        for (Plot plot : plots) {
            if (plot.getInformation().getDisplayName().toLowerCase().contains(worldName.toLowerCase())) {
                foundPlots.add(plot);
            }
        }
        return foundPlots;
    }

    /**
     Returns plots that contains specified ID.
     **/
    public Set<Plot> getPlotsByID(String worldID) {
        Set<Plot> foundPlots = new HashSet<>();
        for (Plot plot : plots) {
            if (plot.getInformation().getCustomID().toLowerCase().contains(worldID.toLowerCase())) {
                foundPlots.add(plot);
            }
        }
        return foundPlots;
    }

    /**
     Returns plots that has specified category.
     **/
    public Set<Plot> getPlotsByCategory(PlotInfo.Category category) {
        Set<Plot> foundPlots = new HashSet<>();
        for (Plot plot : plots) {
            if (plot.getInformation().getCategory() == category) {
                foundPlots.add(plot);
            }
        }
        return foundPlots;
    }

    /**
     Returns plot where specified player in.
     **/
    public Plot getPlotByPlayer(Player player) {
        for (Plot plot : plots) {
            if (plot.getPlayers().contains(player)) {
                return plot;
            }
        }
        return null;
    }

    /**
     Returns developer plot where specified player in.
     **/
    public DevPlot getDevPlot(Player player) {
        for (Plot plot : plots) {
            if (plot.getDevPlot() != null && plot.getDevPlot().world != null) {
                if (plot.getPlayers().contains(player)) {
                    if (plot.getDevPlot().world.getPlayers().contains(player)) {
                        return plot.getDevPlot();
                    }
                }
            }
        }
        return null;
    }

    public DevPlot getDevPlot(World world) {
        for (Plot plot : plots) {
            if (plot.getDevPlot() != null && world.equals(plot.getDevPlot().world)) {
                return plot.getDevPlot();
            }
        }
        return null;
    }

    /**
     Returns plot that has same specified world.
     **/
    public Plot getPlotByWorld(World world) {
        for (Plot plot : plots) {
            if (world.equals(plot.getWorld())) {
                return plot;
            }
            if (world.equals(plot.getDevPlot().world)) {
                return plot;
            }
        }
        return null;
    }

    /**
     Returns plot that has same specified world name (plot60, plot21)).
     **/
    public Plot getPlotByWorldName(String worldName) {
        for (Plot plot : plots) {
            if (plot.getWorldName().equalsIgnoreCase(worldName)) {
                return plot;
            }
        }
        return null;
    }

    /**
     Returns plot that has same specified ID.
     **/
    public Plot getPlotByCustomID(String customID) {
        for (Plot plot : plots) {
            if (plot.getInformation().getCustomID().equalsIgnoreCase(customID)) {
                return plot;
            }
        }
        return null;
    }

}
