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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.ArgumentSlot;
import ua.mcchickenstudio.opencreative.coding.values.EventValue;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.listeners.player.InteractListener;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static ua.mcchickenstudio.opencreative.listeners.player.PlaceBlockListener.placeDevBlock;
import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebugError;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;

/**
 * <h1>CodingBlockPlacer</h1>
 * This class represents a coding block placer, that
 * builds coding blocks from {@link CodeConfiguration} config file in
 * developers worlds.
 */
public class CodingBlockPlacer {

    private final Material wallSign;
    private final Material container;
    private final int blocksPerColumnLimit;

    private final static Pattern INT_PATTERN = Pattern.compile("^-?[0-9]*$");
    private final static Pattern FLOAT_PATTERN = Pattern.compile("^-?[0-9]*\\.?[0-9]+$");

    public CodingBlockPlacer(@NotNull Material wallSign, @NotNull Material container, int maximumBlocks) {
        this.wallSign = wallSign;
        this.container = container;
        this.blocksPerColumnLimit = maximumBlocks;
    }

    public CodingBlockPlacer(@NotNull DevPlanet devPlanet) {
        this(devPlanet.getSignMaterial(), devPlanet.getContainerMaterial(),
                devPlanet.getDevPlatformer().getCodingBlocksLimit(devPlanet));
    }

    public enum CodePlacementResult {

        SUCCESSFULLY,
        NOTHING_TO_BUILD,
        ERROR,
        CANNOT_PLACE,
        NOT_ENOUGH_CODING_LINES;

        public boolean isSuccess() {
            return this == SUCCESSFULLY || this == NOTHING_TO_BUILD;
        }

    }

    /**
     * Places all coding blocks from specified configuration
     * section and returns result. Section must contain
     * executor blocks with actions inside them.
     * @param devPlanet developers planet where coding blocks will be built.
     * @param blocks configuration section containing coding blocks.
     * @return result of placing blocks.
     */
    public @NotNull CodePlacementResult placeCodingLines(@NotNull DevPlanet devPlanet, @NotNull ConfigurationSection blocks) {

        if (!devPlanet.isLoaded()) return CodePlacementResult.CANNOT_PLACE;

        List<Location> freeColumns = new ArrayList<>();
        int requiredColumns = blocks.getKeys(false).size();
        for (DevPlatform platform : devPlanet.getPlatforms()) {
            freeColumns.addAll(platform.getFreeColumns());
            if (freeColumns.size() >= requiredColumns) {
                break;
            }
        }

        return placeCodingLines(freeColumns, blocks);

    }

    /**
     * Places all coding from specified configuration
     * section into free columns. Locations of free columns
     * are locations of executor glass rows.
     * @param freeColumns locations of executor glass rows with free space through coding line.
     * @param blocks configuration section containing coding blocks.
     * @return result of placing blocks.
     */
    public @NotNull CodePlacementResult placeCodingLines(@NotNull List<Location> freeColumns, @NotNull ConfigurationSection blocks) {

        int requiredColumns = blocks.getKeys(false).size();
        if (requiredColumns == 0) return CodePlacementResult.NOTHING_TO_BUILD;

        if (freeColumns.size() < requiredColumns) {
            return CodePlacementResult.NOT_ENOUGH_CODING_LINES;
        }

        int columnIndex = 0;
        for (String key : blocks.getKeys(false)) {
            ConfigurationSection executorBlock = blocks.getConfigurationSection(key);
            if (executorBlock == null) continue;
            Location executorLocation = freeColumns.get(columnIndex).add(0,1,0);
            if (!placeExecutor(executorLocation, executorBlock)) {
                return CodePlacementResult.ERROR;
            }
            columnIndex++;
        }
        return CodePlacementResult.SUCCESSFULLY;

    }

    /**
     * Places executor and its actions through coding line.
     * @param location location of executor block, begin of coding line.
     * @param data configuration section of executor.
     * @return true - if successfully placed executor, false - an error has occurred.
     */
    public boolean placeExecutor(@NotNull Location location, @NotNull ConfigurationSection data) {
        try {
            if (blocksPerColumnLimit <= 0) return false;
            ExecutorType type = ExecutorType.getType(data.getString("type", ""));
            if (type != null) buildExecutorBlock(location, type, data);
            ConfigurationSection actions = data.getConfigurationSection("actions");
            if (actions == null) return true;
            Location actionLocation = location.clone();
            int blocksAmount = 1;
            for (String key : actions.getKeys(false)) {
                ConfigurationSection action = actions.getConfigurationSection(key);
                if (action == null) continue;
                actionLocation.add(2,0,0); // Moves for 2 blocks right ->
                if (blocksAmount > blocksPerColumnLimit) return false;
                if (!placeAction(actionLocation, action,
                        location.getBlockX() + (2 * blocksPerColumnLimit))) return false;
                blocksAmount++;
            }
            return true;
        } catch (Exception error) {
            sendDebugError("Failed to place executor block in " + location.getWorld().getName(), error);
            return false;
        }
    }

    /**
     * Builds executor block on specified location.
     * @param location location of executor block where it will be placed.
     * @param type type of executor.
     * @param data configuration section of executor.
     */
    private void buildExecutorBlock(@NotNull Location location, @NotNull ExecutorType type,
                                    @NotNull ConfigurationSection data) {
        Location signLocation = location.getBlock().getRelative(BlockFace.SOUTH).getLocation();
        switch (type) {
            case FUNCTION, METHOD -> {
                String callName = data.getString("name", "");
                placeDevBlock(location, type.getCategory().getBlock(),
                        type.getCategory().getAdditionalBlock(),
                        wallSign, type.getCategory().name().toLowerCase());
                setSignLine(signLocation, 3, callName);
            }
            case CYCLE -> {
                String cycleName = data.getString("name", "");
                int cycleRepeatingTime = data.getInt("time");
                cycleRepeatingTime = Math.clamp(cycleRepeatingTime, 5, 3600);
                placeDevBlock(location, ExecutorCategory.CYCLE.getBlock(),
                        ExecutorCategory.CYCLE.getAdditionalBlock(),
                        wallSign, "cycle");
                setSignLine(signLocation,1, cycleName);
                setSignLine(signLocation,3, String.valueOf(cycleRepeatingTime));
            }
            default -> {
                placeDevBlock(location, type.getCategory().getBlock(),
                        type.getCategory().getAdditionalBlock(),
                        wallSign, type.getCategory().name().toLowerCase());
                setSignLine(signLocation,3, type.name().toLowerCase());
            }
        }
    }

    /**
     * Places action and its actions, if it's multi action or condition.
     * @param location location of action.
     * @param data configuration section of action.
     * @param maximumX limit of X coordinate for placing blocks while moving right.
     * @return true - if successfully placed, false - error has occurred.
     */
    private boolean placeAction(@NotNull Location location, @NotNull ConfigurationSection data,
                                int maximumX) {
        try {
            ActionType type = ActionType.getType(data.getString("type", ""));
            if (type == null) return true;
            buildContainerBlock(location, data, type);
            buildActionBlock(location, data, type, maximumX);
        } catch (Exception error) {
            sendDebugError("Cannot place action block.", error);
            return false;
        }
        return true;
    }

    /**
     * Builds container block on top of action block,
     * if action type has arguments.
     * @param location location of action block.
     * @param data configuration section of action.
     * @param type type of action.
     */
    private void buildContainerBlock(@NotNull Location location, @NotNull ConfigurationSection data,
                                     @NotNull ActionType type) {
        ConfigurationSection arguments = data.getConfigurationSection("arguments");
        if (arguments != null && type.isChestRequired()) {
            Block containerBlock = location.getBlock().getRelative(BlockFace.UP);
            containerBlock.setType(container);
            BlockData blockData = containerBlock.getBlockData();
            ((Directional) blockData).setFacing(BlockFace.SOUTH);
            containerBlock.setBlockData(blockData);
            if (containerBlock.getState() instanceof InventoryHolder holder) {
                int slot = -1;
                for (ArgumentSlot argSlot : type.getArgumentsSlots()) {
                    ConfigurationSection argSection = arguments.getConfigurationSection(argSlot.getPath());
                    if (argSection == null) {
                        continue;
                    }
                    if (argSlot.isList()) {
                        for (int i = 1; i <= argSlot.getListSize(); i++) {
                            slot++;
                            ConfigurationSection valueSection = argSection.getConfigurationSection("value." + i + ".value");
                            Object valueObject = argSection.get("value." + i + ".value");
                            String valueTypeString = argSection.getString("value." + i + ".type");
                            if (valueObject == null || valueTypeString == null) continue;
                            ValueType valueType = ValueType.parseString(valueTypeString);
                            holder.getInventory().setItem(slot, getItem(valueType, valueSection, valueObject, false));
                        }
                    } else {
                        slot++;
                        ConfigurationSection valueSection = argSection.getConfigurationSection("value");
                        Object valueObject = argSection.get("value");
                        String valueTypeString = argSection.getString("type");
                        if (valueObject == null || valueTypeString == null) continue;
                        ValueType valueType = ValueType.parseString(valueTypeString);
                        holder.getInventory().setItem(slot, getItem(valueType, valueSection, valueObject, argSlot.isParameter()));
                    }
                }
            }
        }
    }

    /**
     * Builds action block on specified location.
     * @param location location of action block where it will be placed.
     * @param type type of action.
     * @param data configuration section of action.
     * @param maximumX limit of X coordinate for placing blocks while moving right.
     */
    private void buildActionBlock(@NotNull Location location, @NotNull ConfigurationSection data,
                                  @NotNull ActionType type, int maximumX) {
        Location signLocation = location.getBlock().getRelative(BlockFace.SOUTH).getLocation();
        String target = data.getString("target","").toLowerCase();
        switch (type) {
            case LAUNCH_FUNCTION, LAUNCH_METHOD -> {
                placeDevBlock(location, type.getCategory().getBlock(),
                        type.getCategory().getAdditionalBlock(),
                        wallSign, type.getCategory().name().toLowerCase());
                String name = data.getString("name","");
                setSignLine(signLocation,3, name);
                setSignLine(signLocation,4, target);
            }
            case SELECTION_SET, SELECTION_ADD, SELECTION_REMOVE -> {
                placeDevBlock(location, type.getCategory().getBlock(),
                        type.getCategory().getAdditionalBlock(),
                        wallSign, "");
                ConfigurationSection conditionInfo = data.getConfigurationSection("condition");
                if (conditionInfo != null) {
                    boolean isOpposed = conditionInfo.getBoolean("opposed", false);
                    ActionCategory conditionCategory = ActionCategory.valueOf(conditionInfo.getString("category"));
                    ActionType conditionType = ActionType.valueOf(conditionInfo.getString("type"));
                    if (isOpposed) setSignLine(signLocation, 1, "not");
                    setSignLine(signLocation, 2, conditionCategory.name().toLowerCase());
                    setSignLine(signLocation, 3, conditionType.name().toLowerCase());
                } else {
                    setSignLine(signLocation, 2, target);
                }
            }
            default -> {
                placeDevBlock(location, type.getCategory().getBlock(),
                        type.getCategory().getAdditionalBlock(),
                        wallSign, type.getCategory().name().toLowerCase());
                setSignLine(signLocation,3, type.name().toLowerCase());
                setSignLine(signLocation,4, target);
                if (type.getCategory().isCondition()) {
                    if (data.getBoolean("opposed",false)) {
                        setSignLine(signLocation, 1, "not");
                    }
                }
                if (type.getCategory().isMultiAction()) {
                    buildMultiActionBlock(location, data, maximumX);
                }
            }
        }
    }

    /**
     * Builds inside actions and else actions of multi action
     * or condition.
     * @param location location of multi action block.
     * @param data configuration section of multi action.
     * @param maximumX limit of X coordinate for placing blocks while moving right.
     */
    private void buildMultiActionBlock(@NotNull Location location, @NotNull ConfigurationSection data,
                                       int maximumX) {
        ConfigurationSection actions = data.getConfigurationSection("actions");
        if (actions != null) {
            for (String key : actions.getKeys(false)) {
                if (location.getBlockX() + 2 > maximumX) return;
                location.add(2,0,0);
                ConfigurationSection action = actions.getConfigurationSection(key);
                if (action == null) continue;
                placeAction(location, action, maximumX);
            }
            addEndingPiston(location);
        }
        ConfigurationSection elseActions = data.getConfigurationSection("else");
        if (elseActions != null) {
            placeDevBlock(location.add(2,0,0), ActionCategory.ELSE_CONDITION.getBlock(),
                    ActionCategory.ELSE_CONDITION.getAdditionalBlock(), wallSign,
                    "else_condition");
            for (String key : elseActions.getKeys(false)) {
                if (location.getBlockX() + 2 > maximumX) return;
                location.add(2,0,0);
                ConfigurationSection action = elseActions.getConfigurationSection(key);
                if (action == null) continue;
                placeAction(location, action, maximumX);
            }
            addEndingPiston(location);
        }
    }

    /**
     * Parses value and gives it as coding item stack.
     * If it's wrong value, then returns AIR.
     * @param type type of value.
     * @param data configuration section of value.
     * @param configValue section or string.
     * @param doNotDropMe prevent dropping this item on destroying container.
     * @return item stack of value, or AIR item.
     */
    @SuppressWarnings("deprecation")
    private @NotNull ItemStack getItem(@NotNull ValueType type, @Nullable ConfigurationSection data,
                                       @NotNull Object configValue, boolean doNotDropMe) {
        String stringValue = configValue.toString();
        ItemStack item = createItem(type.getMaterial(), 1,
                "menus.developer.variables.items." + type.name().toLowerCase().replace("_","-"));
        if (doNotDropMe) setPersistentData(item, getCodingDoNotDropMeKey(), "1");
        ItemMeta meta = item.getItemMeta();
        switch (type) {
            case LOCATION -> {
                if (data == null) return new ItemStack(Material.AIR);
                double x, y ,z;
                float yaw,pitch;
                x = data.getDouble("x");
                y = data.getDouble("y");
                z = data.getDouble("z");
                yaw = (float) data.getDouble("yaw");
                pitch = (float) data.getDouble("pitch");
                Location location = new Location(null, x, y, z, yaw, pitch);
                setDisplayName(item, InteractListener.formatLocation(location));
                setPersistentData(item,getCodingValueKey(),"LOCATION");
                return item;
            }
            case VECTOR -> {
                if (data == null) return new ItemStack(Material.AIR);
                double x, y ,z;
                x = data.getDouble("x");
                y = data.getDouble("y");
                z = data.getDouble("z");
                setDisplayName(item, ChatColor.translateAlternateColorCodes('&',
                        "&b" + x + " " + y + " " + z));
                setPersistentData(item,getCodingValueKey(),"VECTOR");
                return item;
            }
            case COLOR -> {
                if (data == null) return new ItemStack(Material.AIR);
                int r,g,b;
                r = data.getInt("red");
                g = data.getInt("blue");
                b = data.getInt("green");
                if (meta != null) {
                    meta.displayName(Component.text(r + " " + g + " " + b).color(TextColor.color(r,g,b)));
                    item.setItemMeta(meta);
                }
                setPersistentData(item,getCodingValueKey(),"COLOR");
                return item;
            }
            case VARIABLE -> {
                if (data == null) return new ItemStack(Material.AIR);
                String varName = data.getString("name","");
                String typeString = data.getString("type");
                VariableLink.VariableType varType = VariableLink.VariableType.getEnum(typeString);
                if (varType == null) {
                    varType = VariableLink.VariableType.GLOBAL;
                }
                setDisplayName(item, varType.getColor() + varName);
                setPersistentData(item,getCodingValueKey(),"VARIABLE");
                setPersistentData(item,getCodingVariableTypeKey(),varType.name());
                return item;
            }
            case EVENT_VALUE -> {
                if (data == null) return new ItemStack(Material.AIR);
                String valueType = data.getString("name");
                String targetType = data.getString("target","selected");
                if (valueType == null) return new ItemStack(Material.AIR);
                if (valueType.isEmpty()) return new ItemStack(Material.AIR);
                Target target = Target.getByText(targetType);
                if (valueType.startsWith("PLOT")) {
                    valueType = valueType.replace("PLOT","PLANET");
                }
                EventValue eventValue = EventValues.getInstance().getById(valueType.toLowerCase());
                if (eventValue != null) {
                    setDisplayName(item, eventValue.getLocaleName());
                } else {
                    setDisplayName(item, valueType);
                }
                setPersistentData(item, getCodingValueKey(), "EVENT_VALUE");
                setPersistentData(item, getCodingVariableTypeKey(), valueType);
                setPersistentData(item, getCodingTargetTypeKey(), target.name());
                return item;
            }
            case NUMBER -> {
                if (INT_PATTERN.matcher(stringValue).matches()) {
                    setDisplayName(item, ChatColor.translateAlternateColorCodes('&',
                            "&a" + Integer.parseInt(stringValue)));
                } else if (FLOAT_PATTERN.matcher(stringValue).matches()) {
                    setDisplayName(item, ChatColor.translateAlternateColorCodes('&',
                            "&a" + Float.parseFloat(stringValue)));
                }
                setPersistentData(item, getCodingValueKey(), "NUMBER");
                return item;
            }
            case TEXT -> {
                setDisplayName(item, stringValue);
                setPersistentData(item, getCodingValueKey(), "TEXT");
                return item;
            }
            case BOOLEAN -> {
                boolean value = Boolean.parseBoolean(stringValue);
                setDisplayName(item, ChatColor.translateAlternateColorCodes('&',
                        (value ? "&a" : "&c") + value));
                setPersistentData(item, getCodingValueKey(), "BOOLEAN");
                return item;
            }
            case PARTICLE -> {
                if (data == null) return new ItemStack(Material.AIR);
                String particle = data.getString("type");
                setPersistentData(item, getCodingValueKey(), "PARTICLE");
                setPersistentData(item, getCodingParticleTypeKey(), particle);
                return item;
            }
            case ITEM, ANY, POTION -> {
                if (data == null) return new ItemStack(Material.AIR);
                try {
                    item = fixItem(ItemStack.deserialize(data.getValues(false)));;
                } catch (Exception error) {
                    item = createItem(Material.BARRIER, 1, "items.developer.broken");
                }
                return item;
            }
            default -> {
                return new ItemStack(Material.AIR);
            }
        }
    }

    /**
     * Builds piston at the end of condition.
     * @param location location of condition block.
     */
    private void addEndingPiston(@NotNull Location location) {
        // o[
        location.add(3,0,0); // Moves for 3 blocks right ->
        Block farEastBlock = location.getBlock();
        farEastBlock.setType(Material.PISTON);
        // o[ ]
        Directional data = (Directional) farEastBlock.getBlockData();
        data.setFacing(BlockFace.WEST);
        farEastBlock.setBlockData(data);
        location.add(-1,0,0); // Returns to free slot before piston
    }

}
