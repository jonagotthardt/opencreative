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
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.events.EventValues;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorType;
import mcchickenstudio.creative.coding.variables.ValueType;
import mcchickenstudio.creative.coding.menus.layouts.ArgumentSlot;
import mcchickenstudio.creative.coding.variables.VariableLink;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import mcchickenstudio.creative.plots.DevPlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcchickenstudio.creative.utils.ErrorUtils.sendPlotCompileErrorMessage;
import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>BlockParser</h1>
 * This class represents parser of coding blocks. It has methods to
 * read coding block information and save it into plot's code script.
 * @see CodeScript
 */
public class CodingBlockParser {

    /**
     * Checks every coding block on coding platform in developer's plot and saves them into codeScript.yml.
     * @param devPlot Developer's plot to check blocks.
     */
    public void parseCode(DevPlot devPlot) {

        World world = devPlot.world;
        devPlot.getPlot().stopBukkitRunnables();
        CodeScript script = devPlot.getPlot().getScript();
        script.clear();

        List<Block> unknownBlocks = new ArrayList<>();
        // For floors
        for (byte y = 1; y < devPlot.getFloors()*4; y=(byte)(y+4)) {

            // For coding executors
            for (byte z = 4; z < 96; z = (byte)(z+4)) {

                Block executorBlock = world.getBlockAt(4,y,z);
                ExecutorCategory executorCategory = ExecutorCategory.getByMaterial(executorBlock.getType());
                ExecutorType executorType = ExecutorType.getType(executorBlock);

                /*
                 * Checking executor. If executor is not detected,
                 * then we don't need to save actions inside,
                 * because we can't execute them without executor.
                 */
                if (executorCategory == null || executorType == null) {
                    if ((executorCategory == null && isSignEmpty(executorBlock, (byte) 2)) || (executorType == null && isSignEmpty(executorBlock, (byte) 3))) {
                        unknownBlocks.add(executorBlock);
                    }
                    continue;
                }
                script.saveExecutorBlock(executorBlock,executorCategory,executorType);

                // For coding actions
                List<String> multiActions = new ArrayList<>();
                for (byte x = 6; x < 96; x= (byte) (x+2)) {

                    Block actionBlock = world.getBlockAt(x,y,z);
                    ActionCategory actionCategory = ActionCategory.getByMaterial(actionBlock.getType());
                    ActionType actionType = ActionType.getType(actionBlock);
                    Target actionTarget = Target.getBySign(actionBlock.getLocation());
                    Block containerBlock = actionBlock.getRelative(BlockFace.UP);

                    if (actionCategory != null && actionCategory.isMultiAction()) {
                        multiActions.add((actionCategory.isCondition() ? "condition_block_" : "multi_action_") + script.getBlockNumber(actionBlock));
                        if (actionType == null) {
                            continue;
                        }
                    }

                    if (actionCategory == null || actionType == null) {
                        if (actionBlock.getType() != Material.END_STONE && ((actionCategory == null && isSignEmpty(actionBlock, (byte) 2)) || (actionType == null && isSignEmpty(actionBlock, (byte) 3)))) {
                            unknownBlocks.add(actionBlock);
                        }
                        /*
                         * Checking condition's piston. If it is beginning piston,
                         * we already added condition in conditions list..
                         */
                        if (world.getBlockAt(x+1,y,z).getType() == Material.PISTON) {
                            if (actionBlock.getType() == Material.END_STONE) {
                                devPlot.world.sendMessage(Component.text("this is normal"));
                                devPlot.world.sendMessage(Component.text(" first cond block -> "));
                            }
                            if (!multiActions.isEmpty()) {
                                String last = multiActions.getLast();
                                multiActions.remove(last);
                            } else {
                                sendPlotCompileErrorMessage(devPlot.getPlot(),world.getBlockAt(x+1,y,z),getLocaleMessage("plot-code-error.bad-piston"));
                                continue;
                            }
                        }
                        continue;
                    }
                    script.saveActionBlock(multiActions,actionBlock,actionCategory,actionType,actionTarget);
                    /*
                     * Checking items in container and saving
                     * them as arguments for action.
                     */
                    if (!(containerBlock.getState() instanceof InventoryHolder container)) continue;
                    byte slot = 0;
                    ItemStack[] content = container.getInventory().getContents();
                    for (ArgumentSlot argSlot : actionType.getArgumentsSlots()) {
                        ItemStack item = content[slot];
                        /*
                         * If argument slot is list, then we need
                         * handle and save every item into list.
                         */
                        if (argSlot.isList()) {
                            script.saveArguments(multiActions,actionBlock,argSlot.getPath(),null, ValueType.LIST);
                            for (byte i = 1; i < argSlot.getListSize()+1; i++) {
                                if (slot < content.length) {
                                    item = content[slot];
                                    if (item == null) {
                                        if (argSlot.acceptEmptyItems()) {
                                            item = new ItemStack(Material.AIR);
                                            script.saveArguments(multiActions,actionBlock,argSlot.getPath()+".value."+i,parseItemValue(item),parseItemType(item));
                                        }
                                    } else {
                                        script.saveArguments(multiActions,actionBlock,argSlot.getPath()+".value."+i,parseItemValue(item),parseItemType(item));
                                    }
                                }
                                slot++;
                            }
                        } else {
                            if (item != null) {
                                script.saveArguments(multiActions,actionBlock,argSlot.getPath(),parseItemValue(item),parseItemType(item));
                            }
                            slot++;
                        }
                    }
                }
            }
        }
        if (!unknownBlocks.isEmpty()) {
            /*
             * Warns world developer about old or unknown
             * coding blocks that were found while parsing.
             */
            sendPlotCompileErrorMessage(devPlot.getPlot(),unknownBlocks);
        }
        if (devPlot.getPlot().getScript().saveCode()) {
            devPlot.getPlot().getScript().loadCode();
        }

    }

    private static ValueType parseItemType(ItemStack item) {
        ValueType valueType = ValueType.ITEM;
        if (item.getItemMeta() != null) {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            String dataType = container.get(getCodingValueKey(), PersistentDataType.STRING);
            if (dataType != null) {
                try {
                    valueType = ValueType.valueOf(dataType);
                } catch (Exception ignored) {}
            }
            if (valueType == ValueType.VARIABLE || valueType == ValueType.EVENT_VALUE) {
                String variableType = container.get(getCodingVariableTypeKey(), PersistentDataType.STRING);
                if (variableType == null || variableType.isEmpty()) {
                    return ValueType.ITEM;
                }
            }
        }
        return valueType;
    }

    public static Object parseItemValue(ItemStack item) {
        ValueType valueType = parseItemType(item);
        if (valueType == ValueType.ITEM) {
            return item.serialize();
        }
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return item.serialize();
        }
        Component itemDisplayName = itemMeta.displayName();
        if (itemDisplayName == null) {
            return item.serialize();
        }
        String name = itemMeta.getDisplayName();
        switch (valueType) {
            case TEXT -> {
                return name;
            }
            case BOOLEAN -> {
                boolean bool;
                try {
                    bool = Boolean.parseBoolean(ChatColor.stripColor(name));
                    return bool;
                } catch (Exception ignored) {}
            }
            case NUMBER -> {
                double number;
                try {
                    number = Double.parseDouble(ChatColor.stripColor(name));
                    return number;
                } catch (Exception ignored) {}
            }
            case LOCATION -> {
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
                        return locationMap;
                    } catch (Exception ignored) {}
                }
            }
            case COLOR -> {
                Map<String, Object> colorMap = new HashMap<>();
                String colorString = ChatColor.stripColor(name);
                String[] colors = colorString.split(" ");
                if (colors.length == 3) {
                    try {
                        colorMap.put("red",Integer.parseInt(colors[0]));
                        colorMap.put("green",Integer.parseInt(colors[1]));
                        colorMap.put("blue",Integer.parseInt(colors[2]));
                        return colorMap;
                    } catch (Exception ignored) {}
                }
            }
            case VARIABLE -> {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                String variableType = container.get(getCodingVariableTypeKey(), PersistentDataType.STRING);
                if (variableType == null) break;
                VariableLink.VariableType type;
                Map<String, String> variableMap = new HashMap<>();
                try {
                    type = VariableLink.VariableType.valueOf(variableType);
                    variableMap.put("name",ChatColor.stripColor(name));
                    variableMap.put("type",type.name());
                    return variableMap;
                } catch (Exception ignored) {}
            }
            case EVENT_VALUE -> {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                String variableType = container.get(getCodingVariableTypeKey(), PersistentDataType.STRING);
                if (variableType == null) break;
                EventValues.Variable type;
                Map<String, String> valueMap = new HashMap<>();
                try {
                    type = EventValues.Variable.valueOf(variableType);
                    valueMap.put("name",type.name());
                    return valueMap;
                } catch (Exception ignored) {}
            }
            case PARTICLE -> {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                String particleType = container.get(getCodingParticleTypeKey(), PersistentDataType.STRING);
                if (particleType == null) break;
                Particle type;
                Map<String, String> valueMap = new HashMap<>();
                try {
                    type = Particle.valueOf(particleType);
                    valueMap.put("type",type.name());
                    return valueMap;
                } catch (Exception ignored) {}
            }
        }
        return name;
    }

    private boolean isSignEmpty(Block block, byte line) {
        Block signBlock = block.getRelative(BlockFace.SOUTH);
        if (signBlock.getType().name().contains("SIGN")) {
            Sign sign = (Sign) signBlock.getState();
            if (line > 0 && line < sign.lines().size()) {
                return (!sign.getLine(line - 1).isEmpty());
            }
            return false;
        }
        return false;
    }
}
