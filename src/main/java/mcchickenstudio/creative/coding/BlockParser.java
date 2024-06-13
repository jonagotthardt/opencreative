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

package mcchickenstudio.creative.coding;

import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorType;
import mcchickenstudio.creative.coding.blocks.variables.VariableType;
import mcchickenstudio.creative.coding.config.ConfigExecutors;
import mcchickenstudio.creative.coding.menus.layouts.ArgumentSlot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import mcchickenstudio.creative.plots.DevPlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcchickenstudio.creative.utils.ErrorUtils.sendPlotCompileErrorMessage;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class BlockParser {

    public void parseCode(DevPlot devPlot) {

        World world = devPlot.world;
        CodeScript script = devPlot.linkedPlot.script;
        script.clear();

        List<Block> unknownBlocks = new ArrayList<>();
        // For floors
        for (byte y = 1; y < devPlot.getFloors()*4; y=(byte)(y+4)) {

            // For code lines
            for (byte z = 4; z < 96; z = (byte)(z+4)) {

                Block executorBlock = world.getBlockAt(4,y,z);
                ExecutorCategory executorCategory = ExecutorCategory.getByMaterial(executorBlock.getType());
                ExecutorType executorType = ExecutorType.getType(executorBlock);

                if (executorCategory == null || executorType == null) {
                    if ((executorCategory == null && !isSignEmpty(executorBlock,(byte) 2)) || (executorType == null && !isSignEmpty(executorBlock,(byte) 3))) {
                        unknownBlocks.add(executorBlock);
                    }
                    continue;
                }
                script.saveExecutorBlock(executorBlock,executorCategory,executorType);

                List<String> conditions = new ArrayList<>();
                for (byte x = 6; x < 96; x= (byte) (x+2)) {

                    Block actionBlock = world.getBlockAt(x,y,z);
                    ActionCategory actionCategory = ActionCategory.getByMaterial(actionBlock.getType());
                    ActionType actionType = ActionType.getType(actionBlock);
                    Block container = actionBlock.getRelative(BlockFace.UP);

                    if (actionCategory == ActionCategory.PLAYER_CONDITION) {
                        conditions.add("condition_block_" + script.getBlockActionNumber(actionBlock));
                    }

                    if (actionCategory == null || actionType == null) {
                        if (actionBlock.getType() != Material.END_STONE && ((actionCategory == null && !isSignEmpty(actionBlock,(byte) 2)) || (actionType == null && !isSignEmpty(actionBlock,(byte) 3)))) {
                            unknownBlocks.add(actionBlock);
                        }
                        if (world.getBlockAt(x+1,y,z).getType() == Material.PISTON) {
                            if (!conditions.isEmpty()) {
                                String last = conditions.get(conditions.size()-1);
                                conditions.remove(last);
                            } else {
                                sendPlotCompileErrorMessage(devPlot.linkedPlot,actionBlock,getLocaleMessage("plot-code-error.bad-piston"));
                                continue;
                            }
                        }
                        continue;
                    }


                    script.saveActionBlock(conditions,actionBlock,actionCategory,actionType);

                    if (container.getType() != Material.CHEST) continue;
                    byte slot = 0;
                    ItemStack[] content = ((Chest) container.getState()).getBlockInventory().getContents();
                    for (ArgumentSlot argSlot : actionType.getArgumentsSlots()) {
                        ItemStack item = content[slot];
                        if (argSlot.isList()) {
                            script.setArgs(conditions,actionBlock,argSlot.getPath(),null,VariableType.LIST);
                            for (byte i = 1; i < argSlot.getListSize(); i++) {
                                if (slot < content.length) {
                                    item = content[slot];
                                    if (item == null) {
                                        if (argSlot.acceptEmptyItems()) {
                                            item = new ItemStack(Material.AIR);
                                            script.setArgs(conditions,actionBlock,argSlot.getPath()+".value."+i,parseItemValue(item,argSlot.isItemStack()),parseItemType(item, argSlot.isItemStack()));
                                        }
                                    } else  {
                                        script.setArgs(conditions,actionBlock,argSlot.getPath()+".value."+i,parseItemValue(item,argSlot.isItemStack()),parseItemType(item, argSlot.isItemStack()));
                                    }
                                }
                                slot++;
                            }
                        } else {
                            if (item != null) {
                                script.setArgs(conditions,actionBlock,argSlot.getPath(),parseItemValue(item,argSlot.isItemStack()),parseItemType(item, argSlot.isItemStack()));
                            }
                        }
                        slot++;
                    }
                }
            }
        }
        if (!unknownBlocks.isEmpty()) {
            sendPlotCompileErrorMessage(devPlot.linkedPlot,unknownBlocks);
        }
        devPlot.linkedPlot.script.loadCode();
    }

    private VariableType parseItemType(ItemStack item, boolean isItemStack) {
        if (isItemStack) {
            return VariableType.ITEM;
        }
        return VariableType.getByMaterial(item.getType());
    }

    private Object parseItemValue(ItemStack item, boolean isItemStack) {
        if (isItemStack) {
            return item.serialize();
        }
        if (!item.hasItemMeta()) return "";
        if (item.getItemMeta().displayName() == null) return "";
        String name = item.getItemMeta().getDisplayName();
        switch(item.getType()) {
            case SLIME_BALL:
                return ChatColor.stripColor(name);
            case BOOK:
                return name;
            case CLOCK:
                return ChatColor.stripColor(name);
            case MAGMA_CREAM:
                return ChatColor.stripColor(name);
            case PAPER:
                Map<String, Object> locationMap = new HashMap<>();
                String locationString = ChatColor.stripColor(name);
                String[] locCoords = locationString.split(" ");
                if (locCoords.length == 5) {
                    try {
                        locationMap.put("x",Double.parseDouble(locCoords[0]));
                        locationMap.put("y",Double.parseDouble(locCoords[1]));
                        locationMap.put("z",Double.parseDouble(locCoords[2]));
                        locationMap.put("yaw",Float.parseFloat(locCoords[3]));
                        locationMap.put("pitch",Float.parseFloat(locCoords[4]));
                    } catch (Exception error) {

                    }
                }
                return locationMap;
            default:
                return name;
        }
    }

    public int getClosingBracketX(Block firstBlock) {
        Location location = firstBlock.getLocation();
        World world = location.getWorld();

        List<String> conditions = new ArrayList<>();
        for (byte x = (byte) (location.getX()+2); x < 96; x= (byte) (x+2)) {
            Block block = world.getBlockAt(new Location(world,x,location.getBlockY(),location.getBlockZ()));
            if (block.getType() == Material.AIR) {
                if (block.getRelative(BlockFace.EAST).getType() == Material.PISTON) {
                    if (!conditions.isEmpty()) {
                        String last = conditions.get(conditions.size()-1);
                        conditions.remove(last);
                    } else {
                        return block.getRelative(BlockFace.EAST).getX();
                    }
                }
            } else if (block.getType() == Material.OAK_PLANKS) {
                if (block.getRelative(BlockFace.EAST).getType() == Material.PISTON) {
                    conditions.add("cound" + block.getX());
                }
            }
        }
        return -1;
    }

    /*public void parseCodeold(DevPlot devPlot) {

        World world = devPlot.world;
        CodeScript script = devPlot.linkedPlot.script;
        script.clear();

        // For floors
        for (byte y = 1; y < devPlot.getFloors()*4; y=(byte)(y+4)) {

            // For code lines
            for (byte z = 4; z < 96; z = (byte)(z+4)) {

                Block executorBlock = world.getBlockAt(4,y,z);
                String executorType = "";
                String executorSubtype = getSubtype(executorBlock);
                switch(executorBlock.getType()) {
                    case DIAMOND_BLOCK:
                        executorType = "event_player";
                        break;
                }
                if (!executorType.isEmpty() && !executorSubtype.isEmpty()) {
                    script.setExecBlock(executorBlock,executorType,executorSubtype);
                } else {
                    continue;
                }
                List<String> conditions = new ArrayList<>();
                for (byte x = 6; x < 96; x= (byte) (x+2)) {

                    Block actionBlock = world.getBlockAt(x,y,z);
                    String actionType = "";
                    String actionSubtype = getSubtype(actionBlock);
                    Block container = actionBlock.getRelative(BlockFace.UP);

                    switch (actionBlock.getType()) {
                        case COBBLESTONE: {
                            actionType = "action_player";
                            break;
                        }
                        case OAK_PLANKS: {
                            actionType = "if_player";
                            conditions.add("condition_block_" + script.getBlockActionNumber(actionBlock));
                            break;
                        }
                        case AIR: {
                            if (world.getBlockAt(x+1,y,z).getType() == Material.PISTON) {
                                if (!conditions.isEmpty()) {
                                    String last = conditions.get(conditions.size()-1);
                                    conditions.remove(last);
                                } else {
                                    sendPlotCompileErrorMessage(devPlot.linkedPlot,actionBlock,getLocaleMessage("plot-code-error.bad-piston"));
                                    return;
                                }
                            }
                        }
                    }

                    if (!actionType.isEmpty() && !actionSubtype.isEmpty()) {

                        List<String> arguments;
                        if (actionSubtype.equalsIgnoreCase("give_items")) {
                            arguments = new ArrayList<>(parseChestArguments(container, true));
                        } else {
                            arguments = new ArrayList<>(parseChestArguments(container, false));
                        }

                        if (!(actionBlock.getType() == Material.OAK_PLANKS)) {
                            script.setActionBlock(conditions,executorBlock,actionBlock,actionType,actionSubtype,arguments);
                        } else {
                            script.setConditionBlock(conditions,executorBlock,actionBlock,actionType,actionSubtype,arguments);
                        }

                    }

                }
            }
        }
        devPlot.linkedPlot.script.loadCode();
    }*/
    private boolean isSignEmpty(Block block, byte line) {
        Block signBlock = block.getRelative(BlockFace.SOUTH);
        if (signBlock.getType().name().contains("SIGN")) {
            Sign sign = (Sign) signBlock.getState();
            if (line > 0 && line < sign.lines().size()) {
                return (sign.getLine(line-1).isEmpty());
            }
            return true;
        }
        return true;
    }
}
