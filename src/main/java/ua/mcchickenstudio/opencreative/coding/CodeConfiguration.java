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

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;

/**
 * <h1>CodeConfiguration</h1>
 * This class represents a code configuration, that
 * has methods to save executor and action blocks.
 */
public class CodeConfiguration extends YamlConfiguration {

    /**
     * Creates a new {@link CodeConfiguration}, loading from the given file.
     * <p>
     * Any errors loading the Configuration will be logged and then ignored.
     * If the specified input is not a valid config, a blank config will be
     * returned.
     * <p>
     * The encoding used may follow the system dependent default.
     *
     * @param file Input file
     * @return Resulting configuration
     */
    @NotNull
    public static CodeConfiguration loadConfiguration(@NotNull File file) {
        CodeConfiguration config = new CodeConfiguration();
        try {
            config.load(file);
        } catch (FileNotFoundException ignored) {
        } catch (IOException | InvalidConfigurationException ex) {
            OpenCreative.getPlugin().getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        }
        return config;
    }

    /**
     * Saves executor block data in configuration file.
     * @param block executor coding block.
     * @param category category of executor.
     * @param type type of executor.
     */
    public void saveExecutorBlock(Block block, ExecutorCategory category, ExecutorType type) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        String path = "code.blocks.exec_block_" + z + "_" + x;
        set(path + ".category", category.name());
        set(path + ".type", type.name());

        String firstSignLine = getSignLine(block.getRelative(BlockFace.SOUTH).getLocation(),(byte) 1);
        String thirdSignLine = getSignLine(block.getRelative(BlockFace.SOUTH).getLocation(),(byte) 3);

        if (category == ExecutorCategory.FUNCTION || category == ExecutorCategory.METHOD) {
            if (thirdSignLine != null && !thirdSignLine.isEmpty()) {
                set(path + ".name", thirdSignLine);
            }
        } else if (category == ExecutorCategory.CYCLE) {
            if (thirdSignLine != null && !thirdSignLine.isEmpty()) {
                set(path + ".time", Integer.parseInt(thirdSignLine));
            }
            if (firstSignLine != null && !firstSignLine.isEmpty()) {
                set(path + ".name", firstSignLine);
            }
        }

        set(path + ".location.x", x);
        set(path + ".location.y", y);
        set(path + ".location.z", z);
    }

    /**
     * Saves action block data in configuration file.
     * @param multiActions list of multi actions.
     * @param actionBlock action coding block.
     * @param category category of action.
     * @param type type of action.
     * @param target target for action.
     */
    public void saveActionBlock(Block executorBlock, List<String> multiActions, Block actionBlock, ActionCategory category, ActionType type, Target target) {
        String path = getActionBlockPath(executorBlock,actionBlock,multiActions);

        set(path + ".category", category.name());
        set(path + ".type", type.name());

        if (type == ActionType.LAUNCH_FUNCTION || type == ActionType.LAUNCH_METHOD) {
            String thirdSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(),(byte) 3);
            if (thirdSignLine != null && !thirdSignLine.isEmpty()) {
                set(path + ".name",thirdSignLine);
            }
        }

        if (category == ActionCategory.SELECTION_ACTION) {
            String firstSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(),(byte) 1);
            String secondSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(),(byte) 2);
            String thirdSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(),(byte) 3);
            if (secondSignLine != null && !secondSignLine.isEmpty() && thirdSignLine != null && !thirdSignLine.isEmpty()) {
                set(path + ".condition.category", secondSignLine.toUpperCase());
                set(path + ".condition.type", thirdSignLine.toUpperCase());
                if (firstSignLine != null && firstSignLine.equalsIgnoreCase("not")) {
                    set(path + ".condition.opposed",true);
                }
            } else if (secondSignLine != null && !secondSignLine.isEmpty()) {
                set(path + ".target", secondSignLine.toUpperCase());
            }
        } else if (target != Target.DEFAULT) {
            set(path + ".target", target.name());
        }

        if (category.isCondition()) {
            String firstSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(),(byte) 1);
            if (firstSignLine != null && firstSignLine.equalsIgnoreCase("not")) {
                set(path + ".opposed",true);
            }
        }

        set(path + ".location.x", actionBlock.getX());
        createSection(path + ".arguments");
    }

    /**
     * Saves arguments for action block in configuration file.
     * @param multiActions list of multi actions.
     * @param actionBlock action block to set arguments.
     * @param argument argument to set.
     * @param value value of argument.
     * @param type value type.
     */
    public void saveArguments(Block executorBlock, List<String> multiActions, Block actionBlock, String argument, Object value, ValueType type) {
        String path = getActionBlockPath(executorBlock,actionBlock,multiActions);
        set(path + ".arguments." + argument + ".type",type.name());
        set(path + ".arguments." + argument + ".value",value);
    }

    /**
     * Returns path of action block for setting parameters and arguments.
     * @param actionBlock coding action or condition block for getting path.
     * @param multiActions list of multi actions that have brackets and inside actions.
     * @return Configuration path of action block.
     */
    private String getActionBlockPath(Block executorBlock, Block actionBlock, List<String> multiActions) {
        int z = actionBlock.getZ();
        StringBuilder conditionsPath = new StringBuilder();
        for (String condition : multiActions) {
            conditionsPath
                    .append(condition.endsWith(".else") ? condition.replace(".else","") : condition)
                    .append(condition.endsWith(".else") ? ".else." : ".actions.");
        }
        String path = "code.blocks.exec_block_";
        StringBuilder builder = new StringBuilder(path);
        builder.append(z);
        builder.append("_");
        builder.append(executorBlock.getX());
        builder.append(".actions.");
        builder.append(conditionsPath);
        ActionCategory category = ActionCategory.getByMaterial(actionBlock.getType());
        if (category != null && category.isMultiAction()) {
            builder.delete(builder.length()-9,builder.length());
        } else {
            builder.append("action_block").append(getBlockNumber(actionBlock));
        }
        return builder.toString();
    }

    public int getBlockNumber(Block block) {
        return (block.getX() - 2) / 2;
    }

}
