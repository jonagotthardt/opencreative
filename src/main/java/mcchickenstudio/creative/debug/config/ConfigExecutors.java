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

package mcchickenstudio.creative.debug.config;

import mcchickenstudio.creative.coding.CodeScript;
import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorType;
import mcchickenstudio.creative.coding.variables.ValueType;
import mcchickenstudio.creative.coding.menus.layouts.ArgumentSlot;
import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigExecutors {

    private final Plot plot;
  /*  private final YamlConfiguration config;
    private final File file;*/
    private final List<ConfigExecutor> executorList = new ArrayList<>();
    private final World devPlotWorld;

    public ConfigExecutors(Plot plot) {
        this.plot = plot;
        this.devPlotWorld = plot.devPlot.world;
    /*    this.config = config;
        this.file = file;*/
    }

    public ConfigExecutors create() {

        DevPlot devPlot = plot.devPlot;
        World world = devPlot.world;
        CodeScript script = devPlot.linkedPlot.script;
        script.clear();

        List<Block> unknownBlocks = new ArrayList<>();
        // For floors
        for (byte y = 1; y < devPlot.getFloors()*4; y=(byte)(y+4)) {

            // For code lines
            for (byte z = 4; z < 96; z = (byte) (z + 4)) {
                Block executorBlock = world.getBlockAt(4,y,z);
                ConfigExecutor executor = createExecutor(executorBlock);
                if (executor != null) {
                    executorList.add(executor);
                }
            }
        }
        return this;
    }

    private ConfigExecutor createExecutor(Block executorBlock) {
        ExecutorCategory executorCategory = ExecutorCategory.getByMaterial(executorBlock.getType());
        ExecutorType executorType = ExecutorType.getType(executorBlock);
        if (executorCategory == null || executorType == null) {
            return null;
        }
        System.out.println("Creating executor " + executorType + executorCategory);
        ConfigExecutor executor = new ConfigExecutor(executorCategory,executorType);
        System.out.println("Making actions for executor " + executorBlock.getX() + " -> 96 " );
        List<ConfigAction> actions = createActionList(executorBlock,executorBlock.getX(),96);
        executor.setActions(actions);
        return executor;
    }

    private List<ConfigAction> createActionList(Block executorBlock, int start, int end) {
        System.out.println("Creating action list... " + start + " -> " + end);
        List<ConfigAction> actions = new ArrayList<>();
        for (int x = start; x < end; x=x+2) {
            Block actionBlock = devPlotWorld.getBlockAt(x,executorBlock.getY(),executorBlock.getZ());
            System.out.println("Action block " + x + "/" + end + " " + actionBlock.getType());
            ConfigAction action = createAction(executorBlock, actionBlock);
            if (action != null) {
                System.out.println("Found an action, adding...");
                actions.add(action);
            }
        }
        return actions;
    }

    List<ConfigCondition> conditions = new ArrayList<>();

    private ConfigAction createAction(Block executorBlock, Block actionBlock) {
        ActionCategory actionCategory = ActionCategory.getByMaterial(actionBlock.getType());
        ActionType actionType = ActionType.getType(actionBlock);
        if (actionCategory == null || actionType == null) {
            return null;
        }
        System.out.println("Creating confiig action... " + actionCategory + actionType);
        ConfigAction configAction;
        if (actionType.isCondition()) {
            configAction = new ConfigCondition(actionCategory,actionType);
            ((ConfigCondition) configAction).setActions(createActionList(executorBlock,actionBlock.getX()+2,getClosingBracketX(actionBlock)));
            ((ConfigCondition) configAction).setReactions(createActionList(executorBlock, getClosingBracketX(actionBlock)+3, getClosingBracketX(devPlotWorld.getBlockAt(getClosingBracketX(actionBlock)+1,executorBlock.getY(),executorBlock.getZ()))));

        } else if (actionType.isMultiAction()) {
            System.out.println("Multiaction");
            configAction = new ConfigMultiAction(actionCategory,actionType);
        } else {
            System.out.println("Default action");
            configAction = new ConfigAction(actionCategory,actionType);
        }
        if (actionType.isChestRequired()) {
            System.out.println("Chest is required. Parsing chest");
            parseChest(actionBlock.getRelative(BlockFace.UP),configAction);
        }
        return configAction;
    }

    private void parseChest(Block container, ConfigAction action) {
        if (container.getType() != Material.CHEST) return;
        ActionType actionType = action.getType();
        byte slot = 0;
        ItemStack[] content = ((Chest) container.getState()).getBlockInventory().getContents();
        for (ArgumentSlot argSlot : actionType.getArgumentsSlots()) {
            ItemStack item = content[slot];
            if (argSlot.isList()) {
                action.addArgument(argSlot,null);
                for (byte i = 1; i < argSlot.getListSize(); i++) {
                    if (slot < content.length) {
                        item = content[slot];
                        if (item == null) {
                            if (argSlot.acceptEmptyItems()) {
                                item = new ItemStack(Material.AIR);
                                action.addArgument(argSlot.getPath()+"."+i,parseItemType(item, argSlot.isItemStack()),parseItemValue(item,argSlot.isItemStack()));
                            }
                        } else  {
                            action.addArgument(argSlot.getPath()+"."+i,parseItemType(item, argSlot.isItemStack()),parseItemValue(item,argSlot.isItemStack()));
                        }
                    }
                    slot++;
                }
            } else {
                if (item != null) {
                    action.addArgument(argSlot,parseItemValue(item,argSlot.isItemStack()));
                }
            }
            slot++;
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
                    System.out.println("AIR + PISTON");
                    if (!conditions.isEmpty()) {
                        String last = conditions.get(conditions.size()-1);
                        System.out.println("deleting last " + last);
                        conditions.remove(last);
                    } else {
                        System.out.println("found " + location.getX() + " -> " + block.getRelative(BlockFace.EAST).getX());
                        return block.getRelative(BlockFace.EAST).getX();
                    }
                }
            } else if (block.getType() == Material.OAK_PLANKS) {
                if (block.getRelative(BlockFace.EAST).getType() == Material.PISTON) {
                    System.out.println("FOUND A NEW CONDITION");
                    conditions.add("cound" + block.getX());
                }
            }
        }
        return -1;
    }

    private ValueType parseItemType(ItemStack item, boolean isItemStack) {
        if (isItemStack) {
            return ValueType.ITEM;
        }
        return ValueType.getByMaterial(item.getType());
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


}
