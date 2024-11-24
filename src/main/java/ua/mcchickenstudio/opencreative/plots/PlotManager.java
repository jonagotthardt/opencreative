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

package ua.mcchickenstudio.opencreative.plots;

import ua.mcchickenstudio.opencreative.events.plot.PlotDeletionEvent;
import ua.mcchickenstudio.opencreative.events.plot.PlotRegisterEvent;
import ua.mcchickenstudio.opencreative.events.plot.PlotSharingChangeEvent;
import ua.mcchickenstudio.opencreative.utils.*;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.OpenCreative;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;

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
        new PlotRegisterEvent(plot).callEvent();
    }

    /**
     * Creates and loads a new plot for player with specified world generator.
     * @param owner Owner of plot.
     * @param id Id of plot.
     * @param generator Generator of world.
     */
    public void createPlot(Player owner, int id, WorldUtils.WorldGenerator generator) {
        createPlot(owner,id,generator, World.Environment.NORMAL,new Random().nextInt(),false);
    }

    /**
     * Creates and loads a new plot for player with specified world generator, environment, seed and generate sturctures option.
     * @param owner Owner of plot.
     * @param id Id of plot.
     * @param generator Generator of world.
     * @param environment Environment of world.
     * @param seed Seed for generation.
     * @param generateStructures Generate or not generate structures.
     */
    public void createPlot(Player owner, int id, WorldUtils.WorldGenerator generator, World.Environment environment, long seed, boolean generateStructures) {
        owner.showTitle(Title.title(
                toComponent(getLocaleMessage("creating-world.title")), toComponent(getLocaleMessage("creating-world.subtitle")),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(30), Duration.ofSeconds(2))
        ));
        OpenCreative.getPlugin().getLogger().info("Creating new plot " + id + " by " + owner.getName() + "...");

        createWorldSettings(id, false, owner, environment);
        Plot plot = new Plot(id);

        FileUtils.loadWorldFolder(plot.getWorldName(),true);
        if (plot.getTerritory().generateWorld(generator,environment,seed,generateStructures) != null) {
            plot.connectPlayer(owner);
            plot.getTerritory().getWorld().getSpawnLocation().getChunk().load(true);
            owner.showTitle(Title.title(
                    toComponent(getLocaleMessage("creating-world.welcome-title",owner)), toComponent(getLocaleMessage("creating-world.welcome-subtitle",owner)),
                    Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(9), Duration.ofSeconds(2))
            ));
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
     Delete plot on player request. It teleports plot players to spawn, closes plot, unloads world and deletes world folder.
     **/
    public void deletePlot(Plot plot, Player player) {
        new PlotDeletionEvent(plot).callEvent();
        try {
            for (Player p : plot.getPlayers()) {
                PlayerUtils.teleportToLobby(p);
            }
            PlotSharingChangeEvent plotEvent = new PlotSharingChangeEvent(plot,plot.getSharing(),Plot.Sharing.PUBLIC);
            plotEvent.callEvent();
            if (!plotEvent.isCancelled()) {
                plot.setSharing(Plot.Sharing.CLOSED);
            }
            plots.remove(plot);
            FileUtils.deleteFolder(FileUtils.getPlotFolder(plot));
            FileUtils.deleteFolder(FileUtils.getDevPlotFolder(plot.getDevPlot()));
            Bukkit.getServer().getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
                Bukkit.unloadWorld(plot.getWorldName(),false);
                player.sendMessage(MessageUtils.getLocaleMessage("deleting-world.message"));
            }, 60);
        } catch (NullPointerException error) {
            ErrorUtils.sendCriticalErrorMessage("Error while deleting world " + plot.getId(), error);
        }
    }

    public List<Plot> getRecommendedPlots() {
        List<Plot> featuredPlots = new ArrayList<>();
        Set<Integer> featuredIds = OpenCreative.getSettings().getRecommendedWorldsIDs();
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
            if (plot.getDevPlot() != null && plot.getDevPlot().getWorld() != null) {
                if (plot.getPlayers().contains(player)) {
                    if (plot.getDevPlot().getWorld().getPlayers().contains(player)) {
                        return plot.getDevPlot();
                    }
                }
            }
        }
        return null;
    }

    public DevPlot getDevPlot(World world) {
        for (Plot plot : plots) {
            if (plot.getDevPlot() != null && world.equals(plot.getDevPlot().getWorld())) {
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
            if (world.equals(plot.getTerritory().getWorld())) {
                return plot;
            }
            if (world.equals(plot.getDevPlot().getWorld())) {
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
    public Plot getPlotById(String id) {
        for (Plot plot : plots) {
            if (id.equalsIgnoreCase(String.valueOf(plot.getId()))) {
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
