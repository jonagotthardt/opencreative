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

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import mcchickenstudio.creative.plots.Plot;

import static mcchickenstudio.creative.utils.BlockUtils.isOutOfBorders;
import static mcchickenstudio.creative.utils.PlayerUtils.isEntityInDevPlot;


public class PlayerMove implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getY() < 0 && isEntityInDevPlot(player)) {
            player.teleport(player.getWorld().getSpawnLocation());
        }
        if (isBlockChanged(event.getFrom(),event.getTo())) {
            EventRaiser.raiseMoveEvent(event.getPlayer(),event);
            if (isEntityInDevPlot(player)) {
                if (player.getY() >= 0 && player.getY() <= 4) {
                    int radius = 10;
                    int minX = player.getLocation().getBlockX()-radius;
                    int maxX = player.getLocation().getBlockX()+radius;
                    int minZ = player.getLocation().getBlockZ()-radius;
                    int maxZ = player.getLocation().getBlockZ()+radius;
                    for (int x = minX; x <= maxX; x++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            Block block = player.getWorld().getBlockAt(x,1,z);
                            if (block.getType() == Material.OAK_WALL_SIGN) {
                                PlayerUtils.translateSign(block,player);
                            }
                        }
                    }
                }
            }
            if (isOutOfBorders(event.getTo())) {
                if (isOutOfBorders(event.getFrom())) {
                    player.teleport(player.getWorld().getSpawnLocation());
                } else {
                    player.teleport(event.getFrom());
                }
            }
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) EventRaiser.raiseJumpEvent(event.getPlayer(),event);
    }

    @EventHandler
    public void onSneaking(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            EventRaiser.raiseStartSneakingEvent(event.getPlayer(),event);
        } else {
            EventRaiser.raiseStopSneakingEvent(event.getPlayer(),event);
        }
    }

    @EventHandler
    public void onFlying(PlayerToggleFlightEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());

        if (plot != null) {
            if (event.isFlying())  EventRaiser.raiseStartFlyingEvent(event.getPlayer(),event);
            else  EventRaiser.raiseStopFlyingEvent(event.getPlayer(),event);
        }
    }

    @EventHandler
    public void onSprinting(PlayerToggleSprintEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) {
            if (event.isSprinting())  EventRaiser.raiseStartRunningEvent(event.getPlayer(),event);
            else  EventRaiser.raiseStopRunningEvent(event.getPlayer(),event);
        }
    }

    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) {
            EventRaiser.raiseChunkLoadEvent(event);
        }
    }

    @EventHandler
    public void onChunkUnload(PlayerChunkUnloadEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) {
            EventRaiser.raiseChunkUnloadEvent(event);
        }
    }

    private boolean isBlockChanged(Location from, Location to) {
        return from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
    }
}
