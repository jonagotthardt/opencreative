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

import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.plots.DevPlatform;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.Plot;

import static mcchickenstudio.creative.listeners.player.PlayerPlaceBlock.move;
import static mcchickenstudio.creative.utils.BlockUtils.getClosingBracketX;
import static mcchickenstudio.creative.utils.ItemUtils.getCodingDoNotDropMeKey;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.translateBlockSign;

public class PlayerBreakBlock implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);
        if (devPlot != null) {
            Block block = event.getBlock();

            if (player.getInventory().getItemInMainHand().getType() == Material.COMPARATOR) {
                event.setCancelled(true);
                return;
            }

            DevPlatform platform = devPlot.getPlatformInLocation(block.getLocation());
            if (platform == null) {
                event.setCancelled(true);
                return;
            }

            if (devPlot.getIndestructibleBlocks().contains(block.getType())
                    || block.getType() == platform.getFloorMaterial()
                    || block.getType() == platform.getEventMaterial()
                    || block.getType() == platform.getActionMaterial()) {
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                event.setCancelled(true);
            }

            if (devPlot.getAllCodingBlocksForPlacing().contains(block.getType())) {
                destroyAdditionalBlocks(platform,block);
                event.setCancelled(true);
                if (ActionCategory.getByMaterial(block.getType()) != null) {
                    block.setType(Material.AIR);
                    move(block.getLocation(), BlockFace.WEST);
                } else {
                    if (ExecutorCategory.getByMaterial(block.getType()) != null) {
                        if (event.getPlayer().isSneaking()) {
                            for (byte x = (byte) block.getX(); x < platform.getEndX()-1; x = (byte) (x + 2)) {
                                Block actionBlock = block.getWorld().getBlockAt(x, block.getY(), block.getZ());
                                destroyAdditionalBlocks(platform,actionBlock);
                                actionBlock.setType(Material.AIR);
                            }
                        }
                    }
                    block.setType(Material.AIR);
                }
            }

            if (block.getType() == Material.CHEST) {
                Block blockAtDown = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
                if (blockAtDown.getType() == platform.getEventMaterial() || blockAtDown.getType() == platform.getActionMaterial()) {
                    event.setCancelled(true);
                }
            }

            if (block.getType() == Material.OAK_WALL_SIGN) {
                event.setCancelled(true);
                translateBlockSign(block, player);

            }
        } else if (plot != null) {
            if (ChangedWorld.isPlayerWithLocation(player) && !plot.getWorldPlayers().canBuild(player)) {
                player.sendActionBar(getLocaleMessage("not-builder"));
                event.setCancelled(true);
                return;
            }
            EventRaiser.raiseDestroyEvent(event.getPlayer(),event);
        }
    }

    private void destroyAdditionalBlocks(DevPlatform platform, Block block) {
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
        if (containerBlock.getState() instanceof InventoryHolder container) {
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
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) EventRaiser.raiseDamageBlockEvent(event.getPlayer(),event);
    }

}
