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

import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.*;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.Plot;

import java.util.*;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.translateBlockSign;

public class PlayerPlaceBlock implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);

        if (devPlot != null) {

            Block block = event.getBlock();
            Block blockAgainst = event.getBlockAgainst();

            if (blockAgainst.getType() == devPlot.floorBlockMaterial) {
                if ((!(block.getType() == Material.PISTON && (blockAgainst.getZ() % 4) == 0)) && (!devPlot.getAllowedBlocks().contains(block.getType()))) {
                    player.sendActionBar(getLocaleMessage("world.dev-mode.cant-place-on-floor"));
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                    event.setCancelled(true);
                }
            } else if (blockAgainst.getType() == devPlot.eventBlockMaterial) {
                if (devPlot.getEventsBlocks().contains(block.getType())) {
                    Material additionalBlockMaterial = Material.REDSTONE_ORE;
                    String signText = "unknown";
                    switch (block.getType()) {
                        case DIAMOND_BLOCK:
                            additionalBlockMaterial = Material.DIAMOND_ORE;
                            signText = "event_player";
                            break;
                        case EMERALD_BLOCK:
                            additionalBlockMaterial = Material.EMERALD_ORE;
                            signText = "cycle";
                            break;
                        case GOLD_BLOCK:
                            additionalBlockMaterial = Material.GOLD_ORE;
                            signText = "event_world";
                            break;
                        case LAPIS_BLOCK:
                            additionalBlockMaterial = Material.LAPIS_ORE;
                            signText = "function";
                            break;
                    }
                    placeDevBlock(player, block, additionalBlockMaterial, signText, devPlot);
                } else {
                    player.sendActionBar(getLocaleMessage("world.dev-mode.cant-place-action-on-event"));
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                    event.setCancelled(true);
                }
            } else if (blockAgainst.getType() == devPlot.actionBlockMaterial) {
                if (devPlot.getActionsBlocks().contains(block.getType()) || devPlot.getConditionBlocks().contains(block.getType())) {
                    Material additionalBlockMaterial = Material.REDSTONE_ORE;
                    String signText = "Неизвестно";
                    switch (block.getType()) {
                        case COBBLESTONE:
                            additionalBlockMaterial = Material.STONE;
                            signText = "action_player";
                            break;
                        case IRON_BLOCK:
                            additionalBlockMaterial = Material.IRON_ORE;
                            signText = "action_variable";
                            break;
                        case NETHER_BRICKS:
                            additionalBlockMaterial = Material.NETHERRACK;
                            signText = "action_world";
                            break;
                        case OAK_PLANKS:
                            additionalBlockMaterial = Material.PISTON;
                            signText = "if_player";
                            break;
                    }
                    if (block.getRelative(BlockFace.EAST).getType() == Material.PISTON) {
                        move(block.getLocation(), BlockFace.EAST);
                    }
                    placeDevBlock(player, block, additionalBlockMaterial, signText, devPlot);
                } else {
                    player.sendActionBar(getLocaleMessage("world.dev-mode.cant-place-event-on-action"));
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                    event.setCancelled(true);
                }
            } else {
            /*else if (block.getType() == blockAgainst.getType()) {
                player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,100,1.2f);
                event.setCancelled(true);
            } else if (blockAgainst.getType() == Material.DIAMOND_ORE || blockAgainst.getType() == Material.IRON_ORE  || blockAgainst.getType() == Material.STONE || blockAgainst.getType() == Material.NETHERRACK) {
                player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,100,1.2f);
                event.setCancelled(true);
            } */
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 1.2f);
                event.setCancelled(true);
            }

        }

        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot != null)  EventRaiser.raisePlaceBlockEvent(event.getPlayer(),event);
    }

    public static void placeDevBlock(Player player, Block block, Material additionalBlockMaterial, String signText, DevPlot devPlot) {
        Block eastBlock = block.getRelative(BlockFace.EAST);
        eastBlock.setType(additionalBlockMaterial);
        if (eastBlock.getType() == Material.PISTON) {
            Directional data = (Directional) eastBlock.getBlockData();
            data.setFacing(BlockFace.EAST);
            eastBlock.setBlockData(data);
            Block farEastBlock = eastBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST);
            farEastBlock.setType(Material.PISTON);
            data = (Directional) farEastBlock.getBlockData();
            data.setFacing(BlockFace.WEST);
            farEastBlock.setBlockData(data);
        }

        Block wallSign = block.getRelative(BlockFace.SOUTH);
        wallSign.setType(Material.OAK_WALL_SIGN);

        Sign sign = (Sign) wallSign.getState();
        sign.setLine(1, signText);
        sign.update();

        translateBlockSign(wallSign);

        Directional data = (Directional) wallSign.getBlockData();
        data.setFacing(BlockFace.SOUTH);
        wallSign.setBlockData(data);
    }

    public static void move(Location location, BlockFace face) {

        if (face == BlockFace.EAST) {
            Set<Block> movedBlocks = new HashSet<>(); // Создаем множество для отслеживания уже перемещенных блоков
            for (double x = 99; x > location.getX(); x--) { // уменьшил диапазон на 2, чтобы не выйти за пределы мира
                Block oldBlock = location.getWorld().getBlockAt((int) x, location.getBlockY(), location.getBlockZ());
                if (oldBlock.getType() == Material.AIR) continue;
                if (!movedBlocks.contains(oldBlock)) { // Проверяем, был ли этот блок уже перемещен
                    Block newBlock = location.getWorld().getBlockAt((int) x + 2, location.getBlockY(), location.getBlockZ());
                    newBlock.setType(oldBlock.getType());
                    newBlock.setBlockData(oldBlock.getBlockData());
                    movedBlocks.add(newBlock);
                    Block signBlock = oldBlock.getRelative(BlockFace.SOUTH);
                    if (signBlock.getType().toString().contains("WALL_SIGN")) {
                        Sign oldSign = (Sign) signBlock.getState();
                        Block newSignBlock = newBlock.getRelative(BlockFace.SOUTH);
                        newSignBlock.setType(Material.OAK_WALL_SIGN);

                        Sign sign = (Sign) newSignBlock.getState();

                        if (!oldSign.lines().isEmpty()) {
                            sign.setLine(1, oldSign.getLine(1));
                        }
                        if (oldSign.lines().size() > 1) {
                            sign.setLine(2, oldSign.getLine(2));
                        }

                        sign.setBlockData(oldSign.getBlockData());
                        sign.update();
                    }
                    signBlock.setType(Material.AIR);
                    oldBlock.setType(Material.AIR);

                    Block chestBlock = oldBlock.getRelative(BlockFace.UP);
                    if (chestBlock.getType().toString().contains("CHEST")) {
                        Chest oldChest = (Chest) chestBlock.getState();
                        Block newChestBlock = newBlock.getRelative(BlockFace.UP);
                        newChestBlock.setType(Material.CHEST);
                        Chest newChest = (Chest) newChestBlock.getState();
                        newChest.getBlockInventory().setContents(oldChest.getBlockInventory().getContents());
                        chestBlock.setType(Material.AIR);
                    }

                    Block eastBlock = oldBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST);
                    Block newEastBlock = newBlock.getRelative(BlockFace.EAST);
                    movedBlocks.add(newEastBlock);
                    newEastBlock.setType(eastBlock.getType());

                }
            }
        } else if (face == BlockFace.WEST) {
            if (face == BlockFace.WEST) {
                return;
            }
            Set<Block> movedBlocks = new HashSet<>(); // Создаем множество для отслеживания уже перемещенных блоков
            for (double x = location.getX()+2; x < 99; x++) { // уменьшил диапазон на 2, чтобы не выйти за пределы мира
                Block oldBlock = location.getWorld().getBlockAt((int) x, location.getBlockY(), location.getBlockZ());
                if (oldBlock.getType() == Material.AIR) continue;
                if (!movedBlocks.contains(oldBlock)) { // Проверяем, был ли этот блок уже перемещен
                    Block newBlock = location.getWorld().getBlockAt((int) x-2, location.getBlockY(), location.getBlockZ());
                    newBlock.setType(oldBlock.getType());
                    movedBlocks.add(newBlock);
                    Block signBlock = oldBlock.getRelative(BlockFace.SOUTH);
                    if (signBlock.getType().toString().contains("WALL_SIGN")) {
                        Sign oldSign = (Sign) signBlock.getState();
                        Block newSignBlock = newBlock.getRelative(BlockFace.SOUTH);
                        newSignBlock.setType(Material.OAK_WALL_SIGN);

                        Sign sign = (Sign) newSignBlock.getState();

                        if (!oldSign.lines().isEmpty()) {
                            sign.setLine(1, oldSign.getLine(1));
                        }
                        if (oldSign.lines().size() > 1) {
                            sign.setLine(2, oldSign.getLine(2));
                        }

                        sign.setBlockData(oldSign.getBlockData());
                        sign.update();
                    }
                    signBlock.setType(Material.AIR);
                    oldBlock.setType(Material.AIR);

                    Block chestBlock = oldBlock.getRelative(BlockFace.UP);
                    if (chestBlock.getType().toString().contains("CHEST")) {
                        Chest oldChest = (Chest) chestBlock.getState();
                        Block newChestBlock = newBlock.getRelative(BlockFace.UP);
                        newChestBlock.setType(Material.CHEST);
                        Chest newChest = (Chest) newChestBlock.getState();
                        newChest.getBlockInventory().setContents(oldChest.getBlockInventory().getContents());
                        chestBlock.setType(Material.AIR);
                    }

                    Block eastBlock = oldBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST);
                    Block newEastBlock = newBlock.getRelative(BlockFace.EAST);
                    movedBlocks.add(newEastBlock);
                    newEastBlock.setType(eastBlock.getType());

                    newBlock.setType(Material.ENDER_CHEST);

                }
            }
        }
    }

        /*Location newBlockLocation = location.clone().add(1,0,0);
        Location from = location.clone().add(99,0,0);

        if (face == BlockFace.EAST) {

            for (double x = from.getX(); x >= newBlockLocation.getX(); x--) {

                    Location oldBlockLocation = new Location(location.getWorld(), x, location.getY(), location.getZ());

                    if (oldBlockLocation.getBlock().getType().equals(Material.AIR)) continue;


                    BlockState curBS = oldBlockLocation.getBlock().getState();
                    Location cur2 = new Location(newBlockLocation.getWorld(), x + 2, location.getY(), location.getZ());
                    cur2.getBlock().setType(oldBlockLocation.getBlock().getType());
                    cur2.getBlock().setBlockData(oldBlockLocation.getBlock().getBlockData());
                    if (curBS instanceof Chest) {
                        Chest cont = (Chest) curBS;
                        Chest chest = (Chest) cur2.getBlock().getState();
                        (chest).setCustomName(cont.getCustomName());
                        chest.update(true, false);
                        chest.getInventory().setContents(cont.getInventory().getContents());
                        cont.getInventory().clear();
                    }
                    if (curBS instanceof Sign) {
                        Sign sign = (Sign) curBS;
                        Sign sign2 = (Sign) cur2.getBlock().getState();
                        String[] lines = sign.getLines();
                        for (int l = 0; l < lines.length; l++) {
                            sign2.setLine(l, lines[l]);
                        }
                        sign2.update(true, false);
                    }
                    oldBlockLocation.getBlock().setType(Material.AIR);
                }
            }
        }*/

                /**/





}

