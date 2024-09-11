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

package mcchickenstudio.creative.events.player;

import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.Plot;

import static mcchickenstudio.creative.events.player.PlayerPlaceBlock.move;
import static mcchickenstudio.creative.utils.BlockUtils.getClosingBracketX;
import static mcchickenstudio.creative.utils.ItemUtils.getCodingDoNotDropMeKey;
import static mcchickenstudio.creative.utils.PlayerUtils.translateBlockSign;

public class PlayerBreakBlock implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);
        if (devPlot != null) {
            Block block = event.getBlock();

            if (player.getInventory().getItemInMainHand().getType() == Material.COMPARATOR) {
                event.setCancelled(true);
                return;
            }

            if (devPlot.getIndestructibleBlocks().contains(block.getType())) {
                player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,100,1.2f);
                event.setCancelled(true);
            }

            if (devPlot.getAllCodingBlocksForPlacing().contains(block.getType())) {
                destroyAdditionalBlocks(block);
                event.setCancelled(true);
                if (ActionCategory.getByMaterial(block.getType()) != null) {
                    block.setType(Material.AIR);
                    move(block.getLocation(),BlockFace.WEST);
                } else {
                    if (ExecutorCategory.getByMaterial(block.getType()) != null) {
                        if (event.getPlayer().isSneaking()) {
                            for (byte x = (byte) block.getX(); x < 99; x = (byte) (x + 2)) {
                                Block actionBlock = block.getWorld().getBlockAt(x, block.getY(), block.getZ());
                                destroyAdditionalBlocks(actionBlock);
                                actionBlock.setType(Material.AIR);
                            }
                        }
                    }
                    block.setType(Material.AIR);
                }
            }

            if (block.getType() == Material.CHEST) {
                Block blockAtDown = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
                if (blockAtDown.getType() == devPlot.eventBlockMaterial || blockAtDown.getType() == devPlot.actionBlockMaterial) {
                    event.setCancelled(true);
                }
            }

            if (block.getType() == Material.OAK_WALL_SIGN) {
                event.setCancelled(true);
                translateBlockSign(block,player);

            }

        }

        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot != null) EventRaiser.raiseDestroyEvent(event.getPlayer(),event);
    }

    private void destroyAdditionalBlocks(Block block) {
        Block chestBlock = block.getRelative(BlockFace.UP);
        Block additionalBlock = block.getRelative(BlockFace.EAST);
        Block signBlock = block.getRelative(BlockFace.SOUTH);

        if (additionalBlock.getType() == Material.PISTON) {
            int closingBracketX = getClosingBracketX(block);
            if (closingBracketX != -1) {
                block.getWorld().getBlockAt(closingBracketX,block.getY(),block.getZ()).setType(Material.AIR);
            }
        }
        additionalBlock.setType(Material.AIR);
        signBlock.setType(Material.AIR);
        if (chestBlock.getType() == Material.CHEST) {
            Chest chest = (Chest) chestBlock.getState();
            for (ItemStack item : chest.getBlockInventory().getContents()) {
                if (item != null) {
                    if (item.getItemMeta() == null || !item.getItemMeta().getPersistentDataContainer().has(getCodingDoNotDropMeKey())) {
                        chestBlock.getWorld().dropItem(chestBlock.getLocation(),item);
                    }
                }
            }
        }
        chestBlock.setType(Material.AIR);
    }

    @EventHandler
    public void onStartDamaging(BlockDamageEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) EventRaiser.raiseDamageBlockEvent(event.getPlayer(),event);
    }

}
