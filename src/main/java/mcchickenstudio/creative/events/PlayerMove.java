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

package mcchickenstudio.creative.events;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import mcchickenstudio.creative.plots.Plot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



public class PlayerMove implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        WorldBorder border = event.getPlayer().getWorld().getWorldBorder();
        Location borderCenter = border.getCenter();

        double radius = border.getSize()/2;

        double borderCenterX1 = borderCenter.getX()+radius;
        double borderCenterX2 = borderCenter.getX()-radius;
        double borderCenterZ1 = borderCenter.getZ()+radius;
        double borderCenterZ2 = borderCenter.getZ()-radius;

        Location location = player.getLocation();
        double playerX = location.getX();
        double playerZ = location.getZ();

        if (!(borderCenterX1 > playerX && playerX > borderCenterX2)) {
            player.teleport(player.getWorld().getSpawnLocation());
        } else if (!(borderCenterZ1 > playerZ && playerZ > borderCenterZ2)) {
            player.teleport(player.getWorld().getSpawnLocation());
        }

        if (player.getY() < 0 && player.getWorld().getName().endsWith("dev")) {
            player.teleport(player.getWorld().getSpawnLocation());
        }

        if (isBlockChanged(event.getFrom(),event.getTo())) {
            EventRaiser.raiseMoveEvent(event.getPlayer(),event);
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) EventRaiser.raiseJumpEvent(event.getPlayer(),event);
    }

    @EventHandler
    public void onSneaking(PlayerToggleSneakEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());

        if (plot != null) {
            if (event.isSneaking())  EventRaiser.raiseStartSneakingEvent(event.getPlayer(),event);
            else EventRaiser.raiseStopSneakingEvent(event.getPlayer(),event);
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

    private boolean isBlockChanged(Location from, Location to) {
        return from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
    }
}
