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

package mcchickenstudio.creative.listeners.player;

import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import static mcchickenstudio.creative.utils.PlayerUtils.hidePlayerInTab;

public class GameModeChange implements Listener {

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot == null) {
            // If player is not in plot
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!player.getWorld().equals(onlinePlayer.getWorld())) {
                    hidePlayerInTab(onlinePlayer,player);
                    hidePlayerInTab(player,onlinePlayer);
                }
            }
            return;
        }
        if (plot.getTerritory().getWorld() == null) return;
        // If player is in plot
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.getWorld().equals(plot.getTerritory().getWorld()) && (plot.getDevPlot().world != null && !onlinePlayer.getWorld().equals(plot.getDevPlot().world))) {
                hidePlayerInTab(onlinePlayer,player);
                hidePlayerInTab(player,onlinePlayer);
            }
        }
    }

}
