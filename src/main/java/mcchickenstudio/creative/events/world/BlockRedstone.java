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

package mcchickenstudio.creative.events.world;

import mcchickenstudio.creative.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.MessageUtils.sendMessageOnce;

public class BlockRedstone implements Listener {

    @NotNull
    static final Plugin plugin = Main.getPlugin();

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Location location = event.getBlock().getLocation();
        Plot plot = PlotManager.getInstance().getPlotByWorld(location.getWorld());
        if (plot != null) {
            plot.lastRedstoneOperationsAmount++;
            if (plot.lastRedstoneOperationsAmount > plot.redstoneOperationsLimit) {
                    sendMessageOnce(plot,getLocaleMessage("world.redstone-limit").replace("%count%",String.valueOf(plot.redstoneOperationsLimit)),5);
                    if (location.getBlock().getType() == Material.OBSERVER) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                location.getBlock().setType(Material.AIR);
                            }
                        }.runTaskLater(plugin,1L);
                    } else {
                        location.getBlock().setType(Material.CAVE_AIR);
                    }
                    plot.lastRedstoneOperationsAmount = 0;
            }
            if (plot.lastRedstoneOperationsAmount > 0) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plot.lastRedstoneOperationsAmount = plot.lastRedstoneOperationsAmount-1;
                    }
                }.runTaskLater(plugin,5L);
            }
        }
    }

}
