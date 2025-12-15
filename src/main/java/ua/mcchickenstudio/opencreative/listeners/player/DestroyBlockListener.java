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

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;

import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.DamageBlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.DestroyBlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.menus.Menus;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.InventoryHolder;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import static ua.mcchickenstudio.opencreative.listeners.player.PlaceBlockListener.move;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getPlayerLocaleComponent;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInLobby;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

/**
 * <h1>DestroyBlockListener</h1>
 * This class represents a listener for when a player
 * destroys block or damages block in world.
 */
public final class DestroyBlockListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        if (devPlanet != null) {
            Block block = event.getBlock();

            if (player.getInventory().getItemInMainHand().getType() == Material.COMPARATOR) {
                event.setCancelled(true);
                return;
            }

            DevPlatform platform = devPlanet.getPlatformInLocation(block.getLocation());
            if (platform == null) {
                event.setCancelled(true);
                return;
            }

            if (devPlanet.getIndestructibleBlocks().contains(block.getType())
                    || block.getType() == platform.getFloorMaterial()
                    || block.getType() == platform.getEventMaterial()
                    || block.getType() == platform.getActionMaterial()
                    || (block.getType() == Material.PISTON && block.getRelative(BlockFace.WEST).isSolid())
            ) {
                Sounds.DEV_NOT_ALLOWED.play(player);
                event.setCancelled(true);
            }

            if (block.getType() == Material.REDSTONE_WALL_TORCH) {
                devPlanet.setCodeChanged(true);
                return;
            }

            if (devPlanet.getAllCodingBlocksForPlacing().contains(block.getType())) {
                if (ActionCategory.getByMaterial(block.getType()) != null) {
                    platform.destroyCodingBlock(block.getLocation(), devPlanet.isDropItems());
                    devPlanet.setCodeChanged(true);
                    move(block.getLocation(), BlockFace.WEST);
                } else {
                    if (ExecutorCategory.getByMaterial(block.getType()) != null
                        && player.isSneaking()) {
                        platform.destroyCodingLine(block.getLocation(), devPlanet.isDropItems());
                        devPlanet.setCodeChanged(true);
                    } else {
                        devPlanet.setCodeChanged(true);
                        platform.destroyCodingBlock(block.getLocation(), devPlanet.isDropItems());
                        devPlanet.clearMarkedExecutors(block.getLocation());
                    }

                }
                event.setCancelled(true);
            }

            if (block.getType().name().contains("WALL_SIGN")) {
                event.setCancelled(true);
                translateBlockSign(block, player);
            }

            if (block.getState() instanceof InventoryHolder) {
                Block blockAtDown = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
                if (blockAtDown.getType() != platform.getEventMaterial() && blockAtDown.getType() != platform.getActionMaterial()) {
                    return;
                }
                event.setCancelled(true);
            }
        } else if (planet != null) {
            if (ChangedWorld.isPlayerWithLocation(player)) {
                if (!planet.getWorldPlayers().canBuild(player)) {
                    player.sendActionBar(getPlayerLocaleComponent("not-builder", player));
                    event.setCancelled(true);
                    return;
                }
                if (player.getInventory().getItemInMainHand().getType() == Material.PAPER) {
                    event.setCancelled(true);
                }
                return;
            }
            new DestroyBlockEvent(event.getPlayer(),event).callEvent();
            if (!event.isCancelled()) {
                Menus.onBlockDestroy(event.getBlock().getLocation());
            }
        } else if (isEntityInLobby(player) && OpenCreative.getSettings().isLobbyDisallowDestroyingBlocks()
                && !player.hasPermission("opencreative.lobby.destroying-blocks.bypass")) {
            event.setCancelled(true);
            player.sendActionBar(getPlayerLocaleComponent("not-for-lobby", player));
        }
    }

    @EventHandler
    public void onStartDamaging(BlockDamageEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new DamageBlockEvent(event.getPlayer(),event).callEvent();
    }

}
