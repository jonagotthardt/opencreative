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

package mcchickenstudio.creative.listeners.world;

import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotFlags;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import static mcchickenstudio.creative.utils.WorldUtils.isDevPlot;

public class BlockChanged implements Listener {

    @EventHandler
    public void onBlockChanged(BlockFadeEvent event) {
        World world = event.getBlock().getWorld();
        Plot plot = PlotManager.getInstance().getPlotByWorld(world);
        if (plot != null) {
            if (plot.getFlagValue(PlotFlags.PlotFlag.BLOCK_CHANGING) == 2) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockChanged(BlockFormEvent event) {
        World world = event.getBlock().getWorld();
        DevPlot devPlot = PlotManager.getInstance().getDevPlot(world);
        if (devPlot != null) {
            if (devPlot.getEventBlockMaterial() == event.getBlock().getRelative(BlockFace.DOWN).getType() || devPlot.getActionBlockMaterial() == event.getBlock().getRelative(BlockFace.DOWN).getType()) {
                event.setCancelled(true);
                return;
            }
        }
        Plot plot = PlotManager.getInstance().getPlotByWorld(world);
        if (plot != null) {
            if (plot.getFlagValue(PlotFlags.PlotFlag.BLOCK_CHANGING) == 2) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplosion(EntityExplodeEvent event) {
        World world = event.getLocation().getWorld();
        Plot plot = PlotManager.getInstance().getPlotByWorld(world);
        if (plot != null) {
            if (plot.getFlagValue(PlotFlags.PlotFlag.BLOCK_EXPLOSION) == 2) {
                event.blockList().clear();
            }
        }
        if (isDevPlot(world)) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onBlockExplosion(BlockExplodeEvent event) {
        World world = event.getBlock().getLocation().getWorld();
        Plot plot = PlotManager.getInstance().getPlotByWorld(world);
        if (plot != null) {
            if (plot.getFlagValue(PlotFlags.PlotFlag.BLOCK_EXPLOSION) == 2) {
                event.blockList().clear();
            }
        }
        if (isDevPlot(world)) {
            event.blockList().clear();
        }
    }

}
