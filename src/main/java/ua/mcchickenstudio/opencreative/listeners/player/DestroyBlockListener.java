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
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import static ua.mcchickenstudio.opencreative.listeners.player.PlaceBlockListener.move;
import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getClosingBracketX;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.getCodingDoNotDropMeKey;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInLobby;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

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
                    || block.getType() == platform.getActionMaterial()) {
                Sounds.DEV_NOT_ALLOWED.play(player);
                event.setCancelled(true);
            }

            if (devPlanet.getAllCodingBlocksForPlacing().contains(block.getType())) {
                if (ActionCategory.getByMaterial(block.getType()) != null) {
                    destroyAdditionalBlocks(platform,block,devPlanet.isDropItems());
                    block.setType(Material.AIR);
                    devPlanet.setCodeChanged(true);
                    move(block.getLocation(), BlockFace.WEST);
                } else {
                    if (ExecutorCategory.getByMaterial(block.getType()) != null) {
                        if (event.getPlayer().isSneaking()) {
                            for (int x = block.getX(); x < platform.getEndCoordinate()-1; x = x + 2) {
                                Block actionBlock = block.getWorld().getBlockAt(x, block.getY(), block.getZ());
                                destroyAdditionalBlocks(platform,actionBlock,devPlanet.isDropItems());
                                actionBlock.setType(Material.AIR);
                            }
                        }
                    }
                    devPlanet.setCodeChanged(true);
                    destroyAdditionalBlocks(platform,block,devPlanet.isDropItems());
                    block.setType(Material.AIR);
                    devPlanet.clearMarkedExecutors(block.getLocation());
                }
                event.setCancelled(true);
            }

            if (block.getType() == devPlanet.getContainerMaterial()) {
                Block blockAtDown = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
                if (blockAtDown.getType() == platform.getEventMaterial() || blockAtDown.getType() == platform.getActionMaterial()) {
                    event.setCancelled(true);
                }
            }

            if (block.getType().name().contains("WALL_SIGN")) {
                event.setCancelled(true);
                translateBlockSign(block, player);
            }
        } else if (planet != null) {
            if (ChangedWorld.isPlayerWithLocation(player) && !planet.getWorldPlayers().canBuild(player)) {
                player.sendActionBar(getLocaleMessage("not-builder"));
                event.setCancelled(true);
                return;
            }
            new DestroyBlockEvent(event.getPlayer(),event).callEvent();
            if (!event.isCancelled()) {
                Menus.onBlockDestroy(event.getBlock().getLocation());
            }
        } else if (isEntityInLobby(player) && OpenCreative.getSettings().isLobbyDisallowDestroyingBlocks()
                && !player.hasPermission("opencreative.lobby.destroying-blocks.bypass")) {
            event.setCancelled(true);
            player.sendActionBar(getLocaleMessage("not-for-lobby"));
        }
    }

    private void destroyAdditionalBlocks(DevPlatform platform, Block block, boolean dropItems) {
        Block containerBlock = block.getRelative(BlockFace.UP);
        Block additionalBlock = block.getRelative(BlockFace.EAST);
        Block signBlock = block.getRelative(BlockFace.SOUTH);

        if (additionalBlock.getType() == Material.PISTON) {
            int closingBracketX = getClosingBracketX(platform, block);
            if (closingBracketX != -1) {
                block.getWorld().getBlockAt(closingBracketX,block.getY(),block.getZ()).setType(Material.AIR);
            }
        }
        additionalBlock.setType(Material.AIR);
        signBlock.setType(Material.AIR);
        Menus.onBlockDestroy(signBlock.getLocation());
        if (dropItems && containerBlock.getState() instanceof InventoryHolder container) {
            Menus.onBlockDestroy(containerBlock.getLocation());
            for (ItemStack item : container.getInventory().getContents()) {
                if (item != null) {
                    if (item.getItemMeta() == null || !item.getItemMeta().getPersistentDataContainer().has(getCodingDoNotDropMeKey())) {
                        containerBlock.getWorld().dropItem(containerBlock.getLocation(),item);
                    }
                }
            }
        }
        containerBlock.setType(Material.AIR);
    }

    @EventHandler
    public void onStartDamaging(BlockDamageEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new DamageBlockEvent(event.getPlayer(),event).callEvent();
    }

}
