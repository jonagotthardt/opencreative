/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2025, McChicken Studio, mcchickenstudio@gmail.com
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

package ua.mcchickenstudio.opencreative.listeners.player;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.util.Vector;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.isOutOfBorders;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInDevPlanet;


public final class MoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getY() < 0 && isEntityInDevPlanet(player)) {
            player.setVelocity(new Vector(0,0.6f * Math.ceil(Math.abs(player.getY())),0));
            if (player.getLocation().clone().toCenterLocation().add(0,1.5d,1).getBlock().isSolid()) {
                player.teleport(player.getLocation().clone().toCenterLocation().add(0,2.5,1));
            }
        }
        if (isBlockChanged(event.getFrom(),event.getTo())) {
            EventRaiser.raiseMoveEvent(event.getPlayer(),event);
            if (isEntityInDevPlanet(player)) {
                if (player.getY() >= 0 && player.getY() <= 4) {
                    int radius = 10;
                    int minX = player.getLocation().getBlockX()-radius;
                    int maxX = player.getLocation().getBlockX()+radius;
                    int minZ = player.getLocation().getBlockZ()-radius;
                    int maxZ = player.getLocation().getBlockZ()+radius;
                    for (int x = minX; x <= maxX; x++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            Block block = player.getWorld().getBlockAt(x,1,z);
                            if (block.getType().name().contains("WALL_SIGN")) {
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
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(event.getPlayer());
        if (planet != null) EventRaiser.raiseJumpEvent(event.getPlayer(),event);
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
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(event.getPlayer());

        if (planet != null) {
            if (event.isFlying())  EventRaiser.raiseStartFlyingEvent(event.getPlayer(),event);
            else  EventRaiser.raiseStopFlyingEvent(event.getPlayer(),event);
        }
    }

    @EventHandler
    public void onSprinting(PlayerToggleSprintEvent event) {
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(event.getPlayer());
        if (planet != null) {
            if (event.isSprinting())  EventRaiser.raiseStartRunningEvent(event.getPlayer(),event);
            else  EventRaiser.raiseStopRunningEvent(event.getPlayer(),event);
        }
    }

    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(event.getPlayer());
        if (planet != null) {
            EventRaiser.raiseChunkLoadEvent(event);
        }
    }

    @EventHandler
    public void onChunkUnload(PlayerChunkUnloadEvent event) {
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(event.getPlayer());
        if (planet != null) {
            EventRaiser.raiseChunkUnloadEvent(event);
        }
    }

    private boolean isBlockChanged(Location from, Location to) {
        return from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
    }
}
