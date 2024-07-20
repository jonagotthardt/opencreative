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
import mcchickenstudio.creative.utils.ErrorUtils;
import mcchickenstudio.creative.utils.FileUtils;
import mcchickenstudio.creative.utils.MessageUtils;
import mcchickenstudio.creative.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import mcchickenstudio.creative.Main;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.PlayerUtils.getPlayerPlotSize;
import static mcchickenstudio.creative.utils.PlayerUtils.teleportToLobby;

public class PlotManager {

    private static PlotManager plotManager;
    final Plugin plugin = Main.getPlugin();

    private PlotManager() {}
    public static PlotManager getInstance() {
        if (plotManager == null) {
            plotManager = new PlotManager();
        }
        return plotManager;
    }

    private final List<Plot> plots = new ArrayList<>();

    public List<Plot> getPlots() {
        return plots;
    }

    public void addToPlots(Plot plot) {
        plots.add(plot);
    }

    public void clearPlots() {
        plots.clear();
    }

    /**
     Load plot, for example if player tries to join it. It loads world folder, world and code script.
     **/
    public void loadPlot(Plot plot) {
        FileUtils.loadWorldFolder(plot.worldName,true);
        Bukkit.createWorld(new WorldCreator(plot.worldName));
        plot.world = Bukkit.getWorld(plot.worldName);
        plot.isLoaded = true;
        plot.setScript(new CodeScript(plot, FileUtils.getPlotScriptFile(plot)));
        FileUtils.setPlotConfigParameter(plot,"last-activity-time",System.currentTimeMillis());
        plot.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
        plot.world.getWorldBorder().setSize(getPlayerPlotSize(plot.getOwnerGroup()));
        plot.getWorldVariables().load();
    }


    /**
    Unload plot, for example if no players playing in plot.
     **/
    public void unloadPlot(Plot plot) {
        plot.getWorldVariables().save();
        FileUtils.setPlotConfigParameter(plot,"last-activity-time",System.currentTimeMillis());
        FileUtils.setPlotConfigParameter(plot,"mode", plot.getPlotMode());
        plot.isLoaded = false;
        for (Player player : plot.getPlayers()) {
            teleportToLobby(player);
        }
        plot.stopBukkitRunnables();
        if (Bukkit.unloadWorld(plot.worldName,true)) {
            FileUtils.unloadWorldFolder(plot.worldName,true);
            if (Bukkit.getWorld(plot.devPlot.worldName) != null) {
                for (Player player : plot.devPlot.world.getPlayers()) {
                    teleportToLobby(player);
                }
                plot.devPlot.setLoaded(false);
                if (Bukkit.unloadWorld(plot.devPlot.worldName,true)) {
                    FileUtils.unloadWorldFolder(plot.devPlot.worldName,true);
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
            if (plot.devPlot.exists()) {
                FileUtils.deleteWorld(FileUtils.getDevPlotFolder(plot.devPlot));
            }
            // Удаляет папку мира
            plot.setPlotSharing(Plot.Sharing.CLOSED);
            plots.remove(plot);
            FileUtils.deleteWorld(FileUtils.getPlotFolder(plot));

            // После 3 секунд удаления мир отгружается полностью
            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                Bukkit.unloadWorld(plot.worldName,false);
                if (plot.devPlot.isLoaded()) Bukkit.unloadWorld(plot.devPlot.worldName, false);
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
            if (plot.devPlot != null) {
                FileUtils.deleteWorld(FileUtils.getDevPlotFolder(plot.devPlot));
            }
            // После 3 секунд удаления мир отгружается полностью
            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                Bukkit.unloadWorld(plot.worldName,false);
                player.sendMessage(MessageUtils.getLocaleMessage("deleting-world.message"));
            }, 60);
        } catch (NullPointerException error) {
            ErrorUtils.sendCriticalErrorMessage("При удалении мира возникла ошибка: " + error.getMessage());
        }
    }


    /**
     Returns plots that contains specified name.
     **/
    public List<Plot> getPlotsByPlotName(String worldName) {
        List<Plot> foundPlots = new ArrayList<>();
        for (Plot plot : plots) {
            if (plot.getPlotName().toLowerCase().contains(worldName.toLowerCase())) {
                foundPlots.add(plot);
            }
        }
        return foundPlots;
    }

    /**
     Returns plots that contains specified ID.
     **/
    public List<Plot> getPlotsByID(String worldID) {
        List<Plot> foundPlots = new ArrayList<>();
        for (Plot plot : plots) {
            if (plot.getPlotCustomID().toLowerCase().contains(worldID.toLowerCase())) {
                foundPlots.add(plot);
            }
        }
        return foundPlots;
    }

    /**
     Returns plots that has specified category.
     **/
    public List<Plot> getPlotsByCategory(Plot.Category category) {
        List<Plot> foundPlots = new ArrayList<>();
        for (Plot plot : plots) {
            if (plot.getPlotCategory() == category) {
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
            if (plot.devPlot != null && plot.devPlot.world != null) {
                if (plot.getPlayers().contains(player)) {
                    if (plot.devPlot.world.getPlayers().contains(player)) {
                        return plot.devPlot;
                    }
                }
            }
        }
        return null;
    }

    /**
     Returns plot that has same specified world.
     **/
    public Plot getPlotByWorld(World world) {
        for (Plot plot : plots) {
            if (plot.world == world) {
                return plot;
            }
            if (plot.devPlot != null && plot.devPlot.isLoaded() && plot.devPlot.world == world) {
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
            if (plot.worldName.equalsIgnoreCase(worldName)) {
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
            if (plot.getPlotCustomID().equalsIgnoreCase(customID)) {
                return plot;
            }
        }
        return null;
    }

}
