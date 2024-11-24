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

package ua.mcchickenstudio.opencreative.listeners.world;

import ua.mcchickenstudio.opencreative.OpenCreative;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.plots.Plot;
import ua.mcchickenstudio.opencreative.plots.PlotManager;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.sendMessageOnce;
import static ua.mcchickenstudio.opencreative.utils.WorldUtils.isDevPlot;

public class BlockRedstone implements Listener {


    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Location location = event.getBlock().getLocation();

        Plot plot = PlotManager.getInstance().getPlotByWorld(location.getWorld());
        if (plot != null) {
            plot.getLimits().setLastRedstoneOperationsAmount(plot.getLimits().getLastRedstoneOperationsAmount()+1);
            if (plot.getLimits().getLastRedstoneOperationsAmount() > plot.getLimits().getRedstoneOperationsLimit()) {
                    sendMessageOnce(plot,getLocaleMessage("world.redstone-limit").replace("%count%",String.valueOf(plot.getLimits().getRedstoneOperationsLimit())),5);
                    if (location.getBlock().getType() == Material.OBSERVER) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                location.getBlock().setType(Material.AIR);
                            }
                        }.runTaskLater(OpenCreative.getPlugin(),1L);
                    } else {
                        location.getBlock().setType(Material.CAVE_AIR);
                    }
                    plot.getLimits().setLastRedstoneOperationsAmount(0);
            }
            if (plot.getLimits().getLastRedstoneOperationsAmount() > 0) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plot.getLimits().setLastRedstoneOperationsAmount(plot.getLimits().getLastRedstoneOperationsAmount()-1);
                    }
                }.runTaskLater(OpenCreative.getPlugin(),5L);
            }
        }

    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
        if (isDevPlot(event.getBlock().getWorld())) {
            event.setCancelled(true);
        }
    }

}
