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
import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorType;
import mcchickenstudio.creative.coding.blocks.executors.Executors;
import mcchickenstudio.creative.coding.variables.ValueType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import mcchickenstudio.creative.plots.Plot;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static mcchickenstudio.creative.utils.BlockUtils.getSignLine;
import static mcchickenstudio.creative.utils.ErrorUtils.*;

/**
 * <h1>CodeScript</h1>
 * This class represents configuration file that stores plot's code.
 * It has methods to load code and save coding blocks.
 * @see CodingBlockParser
 */
public class CodeScript {

    private final Plot linkedPlot;
    private final File file;
    private final Executors executors;
    private final YamlConfiguration scriptConfig;

    public CodeScript(Plot linkedPlot, File file) {
        this.linkedPlot = linkedPlot;
        this.file = file;
        this.executors = new Executors(linkedPlot);
        this.scriptConfig = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Loads code from codeScript.yml file.
     */
    public void loadCode() {
        executors.load(file);
    }

    /**
     * Saves code script config into file.
     * @return true - if saved, false - if failed.
     */
    public boolean saveCode() {
        try {
            scriptConfig.save(file);
            return true;
        } catch (IOException error) {
            sendCriticalErrorMessage("An IO Exception has occurred while saving code. ", error);
            return false;
        }
    }

    /**
     * Moves stored code in old-code section to prevent being overwritten by new code.
     */
    public void clear() {
        ConfigurationSection section = scriptConfig.getConfigurationSection("code.blocks");
        if (section == null) return;
        scriptConfig.set("old-code.blocks", null);
        Map<String, Object> newCode = section.getValues(false);
        scriptConfig.set("old-code.blocks", newCode);
        scriptConfig.set("code.blocks", null);
        try {
            scriptConfig.save(file);
        } catch (IOException exception) {
            sendCriticalErrorMessage("An error has occurred while clearing and saving code script " + this.getLinkedPlot().worldName,exception);
        }
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

        String path = "code.blocks.exec_block_" + z + "_" + y;
        scriptConfig.set(path + ".category", category.name());
        scriptConfig.set(path + ".type", type.name());

        String firstSignLine = getSignLine(block.getRelative(BlockFace.SOUTH).getLocation(),(byte) 1);
        String thirdSignLine = getSignLine(block.getRelative(BlockFace.SOUTH).getLocation(),(byte) 3);

        if (category == ExecutorCategory.FUNCTION || category == ExecutorCategory.METHOD) {
            if (thirdSignLine != null && !thirdSignLine.isEmpty()) {
                scriptConfig.set(path + ".name", thirdSignLine);
            }
        } else if (category == ExecutorCategory.CYCLE) {
            if (thirdSignLine != null && !thirdSignLine.isEmpty()) {
                scriptConfig.set(path + ".time", Integer.parseInt(thirdSignLine));
            }
            if (firstSignLine != null && !firstSignLine.isEmpty()) {
                scriptConfig.set(path + ".name", firstSignLine);
            }
        }

        scriptConfig.set(path + ".location.x", x);
        scriptConfig.set(path + ".location.y", y);
        scriptConfig.set(path + ".location.z", z);
    }

    /**
     * Saves action block data in configuration file.
     * @param multiActions list of multi actions.
     * @param actionBlock action coding block.
     * @param category category of action.
     * @param type type of action.
     * @param target target for action.
     */
    public void saveActionBlock(List<String> multiActions, Block actionBlock, ActionCategory category, ActionType type, Target target) {
        String path = getActionBlockPath(actionBlock,multiActions);

        scriptConfig.set(path + ".category", category.name());
        scriptConfig.set(path + ".type", type.name());

        if (type == ActionType.LAUNCH_FUNCTION) {
            String thirdSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(),(byte) 3);
            if (thirdSignLine != null && !thirdSignLine.isEmpty()) {
                scriptConfig.set(path + ".name",thirdSignLine);
            }
        }

        if (category == ActionCategory.SELECTION_ACTION) {
            String firstSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(),(byte) 1);
            String secondSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(),(byte) 2);
            String thirdSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(),(byte) 3);
            if (secondSignLine != null && !secondSignLine.isEmpty() && thirdSignLine != null && !thirdSignLine.isEmpty()) {
                scriptConfig.set(path + ".condition.category", secondSignLine.toUpperCase());
                scriptConfig.set(path + ".condition.type", thirdSignLine.toUpperCase());
                if (firstSignLine != null && firstSignLine.equalsIgnoreCase("not")) {
                    scriptConfig.set(path + ".condition.opposed",true);
                }
            } else if (secondSignLine != null && !secondSignLine.isEmpty()) {
                scriptConfig.set(path + ".target", secondSignLine.toUpperCase());
            }
        } else if (target != Target.DEFAULT) {
            scriptConfig.set(path + ".target", target.name());
        }

        if (category.isCondition()) {
            String firstSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(),(byte) 1);
            if (firstSignLine != null && firstSignLine.equalsIgnoreCase("not")) {
                scriptConfig.set(path + ".opposed",true);
            }
        }

        scriptConfig.set(path + ".location.x", actionBlock.getX());
        scriptConfig.createSection(path + ".arguments");
    }

    /**
     * Saves arguments for action block in configuration file.
     * @param multiActions list of multi actions.
     * @param actionBlock action block to set arguments.
     * @param argument argument to set.
     * @param value value of argument.
     * @param type value type.
     */
    public void saveArguments(List<String> multiActions, Block actionBlock, String argument, Object value, ValueType type) {
        String path = getActionBlockPath(actionBlock,multiActions);
        scriptConfig.set(path + ".arguments." + argument + ".type",type.name());
        scriptConfig.set(path + ".arguments." + argument + ".value",value);
    }

    /**
     * Returns path of action block for setting parameters and arguments.
     * @param actionBlock coding action or condition block for getting path.
     * @param multiActions list of multi actions that have brackets and inside actions.
     * @return Configuration path of action block.
     */
    private String getActionBlockPath(Block actionBlock, List<String> multiActions) {
        int y = actionBlock.getY();
        int z = actionBlock.getZ();
        StringBuilder conditionsPath = new StringBuilder();
        for (String condition : multiActions) {
            conditionsPath.append(condition).append(".actions.");
        }
        String path = "code.blocks.exec_block_" + z + "_" + y + ".actions." + conditionsPath;
        StringBuilder builder = new StringBuilder(path);
        ActionCategory category = ActionCategory.getByMaterial(actionBlock.getType());
        if (category != null && category.isMultiAction()) {
            builder.delete(path.length()-9,path.length());
        } else {
            builder.append("action_block").append(getBlockNumber(actionBlock));
        }
        return builder.toString();
    }

    public int getBlockNumber(Block block) {
        return (block.getX() - 2) / 2;
    }

    public Executors getExecutors() {
        return executors;
    }

    public Plot getLinkedPlot() {
        return linkedPlot;
    }
}

