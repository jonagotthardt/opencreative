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

package ua.mcchickenstudio.opencreative.coding;

import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.ArgumentSlot;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlanetCompileErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>BlockParser</h1>
 * This class represents parser of coding blocks. It has methods to
 * read coding block information and save it into planet's code script.getConfig().
 * @see CodeScript
 */
public class CodingBlockParser {

    /**
     * Saves all code lines in developer planet and
     * launches new code.
     * @param devPlanet developer planet to parse code.
     */
    public void parseCode(DevPlanet devPlanet) {
        long time = System.currentTimeMillis();
        sendCodingDebugLog(devPlanet.getPlanet(),"Shutting down executors and clearing...");
        devPlanet.getPlanet().getTerritory().stopBukkitRunnables();
        CodeScript script = devPlanet.getPlanet().getTerritory().getScript();
        script.clear();
        sendCodingDebugLog(devPlanet.getPlanet(),"Parsing every block, please wait...");
        parseAllExecutors(devPlanet, script.getConfig());
        sendCodingDebugLog(devPlanet.getPlanet(),"Parsed code in " + (System.currentTimeMillis() - time) + " ms.");
        if (script.saveCode()) {
            devPlanet.getPlanet().getTerritory().getScript().loadCode();
        }
    }

    /**
     * Adds all code lines from developer planet to configuration.
     * Don't forget to save it.
     * @param devPlanet developer planet to parse code.
     * @param config config to add executors.
     */
    public void parseAllExecutors(DevPlanet devPlanet, CodeConfiguration config) {

        List<Location> locations = new ArrayList<>();
        List<DevPlatform> platforms = devPlanet.getPlatforms();

        // For platforms
        for (DevPlatform platform : platforms) {
            // For coding executors
            for (int z = platform.getBeginZ() + 4; z <= platform.getEndZ() - 4; z = z + 4) {
                Block executorBlock = devPlanet.getWorld().getBlockAt(platform.getBeginX() + 4, 1, z);
                locations.add(executorBlock.getLocation());
            }
        }
        parseExecutors(devPlanet, config, locations);

    }

    /**
     * Adds specified code lines from developer planet to configuration.
     * Locations represent locations of executors cells (blue glass).
     * Don't forget to save it.
     * @param devPlanet developer planet to parse code.
     * @param config config to add executors.
     * @return true - code is fine, false - troubles while parsing.
     */
    public boolean parseExecutors(DevPlanet devPlanet, CodeConfiguration config, List<Location> executorsLocations) {

        boolean isCodeFine = true;
        World world = devPlanet.getWorld();

        List<Block> unknownBlocks = new ArrayList<>();
        List<DevPlatform> platforms = devPlanet.getPlatforms();
        Collections.reverse(platforms); // Reversing to make executors from first platform as first

        // For coding executors
        for (Location executorLocation : executorsLocations) {

            int executorX = executorLocation.getBlockX();
            int z = executorLocation.getBlockZ();

            Block executorBlock = world.getBlockAt(executorX,1, z);
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
            config.saveExecutorBlock(executorBlock,executorCategory,executorType);

            // For coding actions
            List<String> multiActions = new ArrayList<>();
            for (int x = executorX+2; x <= executorX+92; x = x+2) {

                Block actionBlock = world.getBlockAt(x,1, z);
                ActionCategory actionCategory = ActionCategory.getByMaterial(actionBlock.getType());
                ActionType actionType = ActionType.getType(actionBlock);
                Target actionTarget = Target.getBySign(actionBlock.getLocation());
                Block containerBlock = actionBlock.getRelative(BlockFace.UP);

                if (actionCategory != null && actionCategory != ActionCategory.ELSE_CONDITION && actionCategory.isMultiAction()) {
                    multiActions.add((actionCategory.isCondition() ? "condition_block_" : "multi_action_") + config.getBlockNumber(actionBlock));
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
                    if (world.getBlockAt(x+1,1,z).getType() == Material.PISTON) {
                        if (!multiActions.isEmpty()) {
                            String last = multiActions.getLast();
                            multiActions.remove(last);
                            if (world.getBlockAt(x+2,1,z).getType() == Material.END_STONE) {
                                multiActions.add(last+".else");
                                x=x+2;
                            }
                        } else {
                            sendPlanetCompileErrorMessage(devPlanet.getPlanet(),world.getBlockAt(x+1,1,z),getLocaleMessage("coding-error.bad-piston"));
                            isCodeFine = false;
                            continue;
                        }
                    }
                    continue;
                }
                config.saveActionBlock(executorBlock,multiActions,actionBlock,actionCategory,actionType,actionTarget);
                /*
                 * Checking items in container and saving
                 * them as arguments for action.
                 */
                if (!(containerBlock.getState() instanceof InventoryHolder container)) continue;
                byte slot = 0;
                ItemStack[] content = container.getInventory().getContents();
                if (actionType.getCategory() == ActionCategory.SELECTION_ACTION) {
                    actionType = ActionType.getTypeFromSelectionAction(actionBlock);
                    if (actionType == null) continue;
                }
                for (ArgumentSlot argSlot : actionType.getArgumentsSlots()) {
                    ItemStack item = content[slot];
                    /*
                     * If argument slot is list, then we need
                     * handle and save every item into list.
                     */
                    if (argSlot.isList()) {
                        config.saveArguments(executorBlock,multiActions,actionBlock,argSlot.getPath(),null, ValueType.LIST);
                        for (byte i = 1; i < argSlot.getListSize()+1; i++) {
                            if (slot < content.length) {
                                item = content[slot];
                                if (item == null) {
                                    if (argSlot.acceptEmptyItems()) {
                                        item = new ItemStack(Material.AIR);
                                        config.saveArguments(executorBlock,multiActions,actionBlock,argSlot.getPath()+".value."+i,parseItemValue(item),parseItemType(item));
                                    }
                                } else {
                                    config.saveArguments(executorBlock,multiActions,actionBlock,argSlot.getPath()+".value."+i,parseItemValue(item),parseItemType(item));
                                }
                            }
                            slot++;
                        }
                    } else {
                        if (item != null) {
                            config.saveArguments(executorBlock,multiActions,actionBlock,argSlot.getPath(),parseItemValue(item),parseItemType(item));
                        }
                        slot++;
                    }
                }
            }
        }

        if (!unknownBlocks.isEmpty()) {
            /*
             * Warns world developer about old or unknown
             * coding blocks that were found while parsing.
             */
            sendPlanetCompileErrorMessage(devPlanet.getPlanet(),unknownBlocks);
            return false;
        }
        return isCodeFine;
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
            case VECTOR -> {
                Map<String, Object> vectorMap = new HashMap<>();
                String vectorString = ChatColor.stripColor(name);
                String[] coords = vectorString.split(" ");
                if (coords.length == 3) {
                    try {
                        vectorMap.put("x",Double.parseDouble(coords[0]));
                        vectorMap.put("y",Double.parseDouble(coords[1]));
                        vectorMap.put("z",Double.parseDouble(coords[2]));
                        return vectorMap;
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
                String variableType = getPersistentData(item, getCodingVariableTypeKey());
                if (variableType.startsWith("PLOT")) {
                    variableType = variableType.replace("PLOT", "PLANET");
                    ItemUtils.setPersistentData(item, getCodingVariableTypeKey(), variableType);
                }
                if (!EventValues.getInstance().exists(variableType.toLowerCase())) {
                    break;
                }
                String targetType = getPersistentData(item, getCodingTargetTypeKey());
                Map<String, String> valueMap = new HashMap<>();
                if (!targetType.isEmpty()) {
                    valueMap.put("name", variableType);
                    valueMap.put("target", targetType);
                }
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
