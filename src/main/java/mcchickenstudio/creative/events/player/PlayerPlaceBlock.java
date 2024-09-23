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
import mcchickenstudio.creative.coding.menus.layouts.Layout;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.*;
import org.bukkit.block.data.Directional;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.inventory.InventoryHolder;

import java.util.*;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.translateBlockSign;

public class PlayerPlaceBlock implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);

        if (devPlot != null) {

            Block block = event.getBlock();
            Block blockAgainst = event.getBlockAgainst();

            if (blockAgainst.getType() == devPlot.getFloorBlockMaterial()) {
                if ((!(block.getType() == Material.PISTON && (blockAgainst.getZ() % 4) == 0 && blockAgainst.getRelative(BlockFace.WEST).getType() == devPlot.getActionBlockMaterial())) && (!(block.getType().name().contains("SIGN") &&  blockAgainst.getX() >= 4 && (blockAgainst.getX() % 2) == 0)) && (!devPlot.getAllowedBlocks().contains(block.getType())) || block.getY() <= 0) {
                    player.sendActionBar(getLocaleMessage("world.dev-mode.cant-place-on-floor"));
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                    event.setCancelled(true);
                }
            } else if (blockAgainst.getType() == devPlot.getEventBlockMaterial()) {
                if (devPlot.getEventsBlocks().contains(block.getType())) {
                    Material additionalBlockMaterial = Material.REDSTONE_ORE;
                    String signText = "unknown";
                    ExecutorCategory executorCategory = ExecutorCategory.getByMaterial(block.getType());
                    ActionCategory actionCategory = ActionCategory.getByMaterial(block.getType());
                    if (executorCategory != null) {
                        signText = executorCategory.name().toLowerCase();
                        additionalBlockMaterial = executorCategory.getAdditionalBlock();
                    } else if (actionCategory != null) {
                        signText = actionCategory.name().toLowerCase();
                        additionalBlockMaterial = actionCategory.getAdditionalBlock();
                    }
                    placeDevBlock(block, additionalBlockMaterial, signText);
                } else {
                    player.sendActionBar(getLocaleMessage("world.dev-mode.cant-place-action-on-event"));
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                    event.setCancelled(true);
                }
            } else if (blockAgainst.getType() == devPlot.getActionBlockMaterial()) {
                if (devPlot.getActionsBlocks().contains(block.getType())) {
                    Material additionalBlockMaterial = Material.REDSTONE_ORE;
                    String signText = "unknown";
                    ExecutorCategory executorCategory = ExecutorCategory.getByMaterial(block.getType());
                    ActionCategory actionCategory = ActionCategory.getByMaterial(block.getType());
                    if (executorCategory != null) {
                        signText = executorCategory.name().toLowerCase();
                        additionalBlockMaterial = executorCategory.getAdditionalBlock();
                    } else if (actionCategory != null) {
                        signText = actionCategory.name().toLowerCase();
                        additionalBlockMaterial = actionCategory.getAdditionalBlock();
                    }
                    if (block.getRelative(BlockFace.EAST).getType() == Material.PISTON) {
                        move(block.getLocation(), BlockFace.EAST);
                    }
                    placeDevBlock(block, additionalBlockMaterial, signText);
                } else {
                    player.sendActionBar(getLocaleMessage("world.dev-mode.cant-place-event-on-action"));
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                    event.setCancelled(true);
                }
            } else {
                if (block.getType() != Material.COMPARATOR) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                }
                event.setCancelled(true);
            }
        } else if (plot != null) {
            if (ChangedWorld.isPlayerWithLocation(player) && !plot.getWorldPlayers().canBuild(player)) {
                player.sendActionBar(getLocaleMessage("not-builder"));
                event.setCancelled(true);
                return;
            }
            EventRaiser.raisePlaceBlockEvent(event.getPlayer(),event);
        }
    }

    public static void placeDevBlock(Block block, Material additionalBlockMaterial, String signText) {
        Block eastBlock = block.getRelative(BlockFace.EAST);
        eastBlock.setType(additionalBlockMaterial);
        if (eastBlock.getType() == Material.PISTON) {
            Directional data = (Directional) eastBlock.getBlockData();
            data.setFacing(BlockFace.EAST);
            eastBlock.setBlockData(data);
            Block farEastBlock = eastBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST);
            move(eastBlock.getLocation(),BlockFace.EAST);
            farEastBlock.setType(Material.PISTON);
            data = (Directional) farEastBlock.getBlockData();
            data.setFacing(BlockFace.WEST);
            farEastBlock.setBlockData(data);
        }

        Block wallSign = block.getRelative(BlockFace.SOUTH);
        wallSign.setType(Material.OAK_WALL_SIGN);

        Sign sign = (Sign) wallSign.getState();
        sign.setLine(1, signText);
        if (block.getType() == Material.OXIDIZED_COPPER) {
            sign.setLine(2,"20");
        }
        sign.update();

        translateBlockSign(wallSign);

        Directional data = (Directional) wallSign.getBlockData();
        data.setFacing(BlockFace.SOUTH);
        wallSign.setBlockData(data);
    }

    public static void move(Location location, BlockFace face) {
        if (face == BlockFace.EAST) {
            /*
             Moves blocks to right
             */
            Set<Block> movedBlocks = new HashSet<>(); // Создаем множество для отслеживания уже перемещенных блоков
            for (double x = 95; x > location.getX(); x--) { // уменьшил диапазон на 2, чтобы не выйти за пределы мира
                Block oldBlock = location.getWorld().getBlockAt((int) x, location.getBlockY(), location.getBlockZ());
                if (oldBlock.getType() == Material.AIR) continue;
                if (!movedBlocks.contains(oldBlock)) { // Проверяем, был ли этот блок уже перемещен
                    Block newBlock = location.getWorld().getBlockAt((int) x + 2, location.getBlockY(), location.getBlockZ());
                    moveCodingBlock(oldBlock,newBlock);
                    movedBlocks.add(newBlock);
                }
            }
        } else if (face == BlockFace.WEST) {
            /*
             Moves blocks to left.
             */
            Set<Block> movedBlocks = new HashSet<>(); // Создаем множество для отслеживания уже перемещенных блоков
            for (double x = location.getX()+1; x < 100; x++) { // уменьшил диапазон на 2, чтобы не выйти за пределы мира
                Block oldBlock = location.getWorld().getBlockAt((int) x, location.getBlockY(), location.getBlockZ());
                if (oldBlock.getType() == Material.AIR) continue;
                if (!movedBlocks.contains(oldBlock)) { // Проверяем, был ли этот блок уже перемещен
                    Block newBlock = location.getWorld().getBlockAt((int) x-2, location.getBlockY(), location.getBlockZ());
                    moveCodingBlock(oldBlock,newBlock);
                    movedBlocks.add(oldBlock);
                }
            }
        }
    }

    /**
     * Moves coding block with wall sign and container to next block.
     * @param oldBlock Block to move and replace with air.
     * @param newBlock Block where to move.
     */
    public static void moveCodingBlock(Block oldBlock, Block newBlock) {
        Block oldSignBlock = oldBlock.getRelative(BlockFace.SOUTH);
        Block oldContainerBlock = oldBlock.getRelative(BlockFace.UP);

        Block newSignBlock = newBlock.getRelative(BlockFace.SOUTH);
        Block newContainerBlock = newBlock.getRelative(BlockFace.UP);

        newBlock.setType(oldBlock.getType());
        newBlock.setBlockData(oldBlock.getBlockData());

        if (oldSignBlock.getType().toString().contains("WALL_SIGN")) {
            Sign oldSign = (Sign) oldSignBlock.getState();
            newSignBlock.setType(Material.OAK_WALL_SIGN);

            Sign sign = (Sign) newSignBlock.getState();
            for (byte i = 0; i < oldSign.getSide(Side.FRONT).lines().size(); i++) {
                sign.getSide(Side.FRONT).line(i,oldSign.getSide(Side.FRONT).line(i));
            }
            sign.setBlockData(oldSign.getBlockData());
            sign.update();
            translateBlockSign(newSignBlock);
        }
        if (oldContainerBlock.getState() instanceof InventoryHolder container) {
            newContainerBlock.setType(oldContainerBlock.getType());
            newContainerBlock.setBlockData(oldContainerBlock.getBlockData());
            DevPlot devPlot = PlotManager.getInstance().getDevPlot(oldContainerBlock.getWorld());
            if (devPlot != null) {
                Layout layout = devPlot.getOpenedMenu(oldContainerBlock.getLocation());
                if (layout != null) {
                    for (Player player : layout.getViewers()) {
                        player.closeInventory();
                    }
                }
            }
            if (newContainerBlock.getState() instanceof InventoryHolder newContainer) {
                newContainer.getInventory().setContents(container.getInventory().getContents());
            }
        }
        oldSignBlock.setType(Material.AIR);
        oldBlock.setType(Material.AIR);
        oldContainerBlock.setType(Material.AIR);
    }
}

