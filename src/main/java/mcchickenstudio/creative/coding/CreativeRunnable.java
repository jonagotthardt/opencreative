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

package mcchickenstudio.creative.coding;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>CreativeRunnable</h1>
 * This class represents BukkitRunnable with modifications that
 * stop executing code when plot is unloaded, or plot is not
 * in Play mode anymore, or player is offline.
 */
public abstract class CreativeRunnable {

    private final Plot plot;
    private int id;

    public CreativeRunnable(Plot plot) {
        this.plot = plot;
    }

    public abstract void execute(Player player);

    public synchronized void runTaskTimer(List<Player> onlinePlayers, long period, long timer) {
        List<Player> currentPlayers = new ArrayList<>(onlinePlayers);
        id = new BukkitRunnable() {
            @Override
            public void run() {
                if (plot != null && plot.getPlotMode() == Plot.Mode.PLAYING) {
                    for (Player player : onlinePlayers) {
                        if (currentPlayers.isEmpty()) {
                            cancel();
                        }
                        if (plot.getPlotMode() != Plot.Mode.PLAYING) {
                            cancel();
                        }
                        if (player == null) {
                           continue;
                        }
                        if (!player.isOnline()) {
                            currentPlayers.remove(player);
                            continue;
                        }
                        Plot playerPlot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (!plot.equals(playerPlot)) {
                            currentPlayers.remove(player);
                            continue;
                        }
                        DevPlot playerDevPlot = PlotManager.getInstance().getDevPlot(player);
                        if (playerDevPlot != null) {
                            currentPlayers.remove(player);
                            continue;
                        }
                        execute(player);
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Main.getPlugin(),period,timer).getTaskId();
    }

    public synchronized void runTaskLater(List<Player> onlinePlayers, long delay) {
        List<Player> currentPlayers = new ArrayList<>(onlinePlayers);
        id = new BukkitRunnable() {
            @Override
            public void run() {
                if (plot != null && plot.getPlotMode() == Plot.Mode.PLAYING) {
                    for (Player player : onlinePlayers) {
                        if (currentPlayers.isEmpty()) {
                            cancel();
                        }
                        if (plot.getPlotMode() != Plot.Mode.PLAYING) {
                            cancel();
                        }
                        if (player == null) {
                            continue;
                        }
                        if (!player.isOnline()) {
                            currentPlayers.remove(player);
                            continue;
                        }
                        Plot playerPlot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (!plot.equals(playerPlot)) {
                            currentPlayers.remove(player);
                            continue;
                        }
                        DevPlot playerDevPlot = PlotManager.getInstance().getDevPlot(player);
                        if (playerDevPlot != null) {
                            currentPlayers.remove(player);
                            continue;
                        }
                        execute(player);
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskLater(Main.getPlugin(),delay).getTaskId();
    }

    public synchronized void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
