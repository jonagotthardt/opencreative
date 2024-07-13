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
import static mcchickenstudio.creative.utils.FileUtils.*;

public class CodeScript {

    public final Plot linkedPlot;
    private final File file;
    public final List<Integer> blockActionsX = new ArrayList<>();
    private final Executors executors;
    private final YamlConfiguration scriptConfig;
    private boolean isCodeRunning = false;

    public CodeScript(Plot linkedPlot, File file) {
        this.linkedPlot = linkedPlot;
        this.file = file;
        for (int x = 6; x < 98; x = x + 2) {
            blockActionsX.add(x);
        }
        this.executors = new Executors(linkedPlot);
        this.scriptConfig = YamlConfiguration.loadConfiguration(file);
    }

    public boolean exists() {
        File linkedPlotFolder = getPlotFolder(linkedPlot);
        return linkedPlotFolder != null;
    }

    public void setCodeRunning(boolean codeRunning) {
        isCodeRunning = codeRunning;
    }

    public boolean isCodeRunning() {
        return isCodeRunning;
    }

    public int getBlockActionNumber(Block block) {
        return blockActionsX.indexOf(block.getX()) + 1;
    }

    public void clear() {
        ConfigurationSection section = scriptConfig.getConfigurationSection("code.blocks");

        scriptConfig.set("old-code.blocks", null);
        if (section == null) return;
        Map<String, Object> newCode = section.getValues(false);
        scriptConfig.set("old-code.blocks", newCode);
        scriptConfig.set("code.blocks", null);

        try {
            scriptConfig.save(file);
        } catch (IOException exception) {
            sendWarningErrorMessage("Произошла ошибка при попытке сохранить новый код в старый... " + this.linkedPlot.worldName);
        }

    }

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


    public void saveActionBlock(List<String> conditions, Block actionBlock, ActionCategory category, ActionType type, Target target) {
        int x = actionBlock.getX();
        int y = actionBlock.getY();
        int z = actionBlock.getZ();

        StringBuilder conditionsPath = new StringBuilder();
        for (String condition : conditions) {
            conditionsPath.append(condition).append(".actions.");
        }

        String path = getActionBlockPath(actionBlock,conditions);
        scriptConfig.set(path + ".category", category.name());
        scriptConfig.set(path + ".type", type.name());
        if (type == ActionType.LAUNCH_FUNCTION) {
            String thirdSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(),(byte) 3);
            if (thirdSignLine != null && !thirdSignLine.isEmpty()) {
                scriptConfig.set(path + ".name",thirdSignLine);
            }
        }

        if (target != Target.DEFAULT) {
            scriptConfig.set(path + ".target", target.name());
        }

        scriptConfig.set(path + ".location.x", x);
        scriptConfig.createSection(path + ".arguments");
    }

    public void setArgs(List<String> conditions, Block actionBlock, String argument, Object value, ValueType type) {String path = getActionBlockPath(actionBlock,conditions);
        scriptConfig.set(path + ".arguments." + argument + ".type",type.name());
        scriptConfig.set(path + ".arguments." + argument + ".value",value);
    }

    private String getActionBlockPath(Block actionBlock, List<String> conditions) {
        int y = actionBlock.getY();
        int z = actionBlock.getZ();
        StringBuilder conditionsPath = new StringBuilder();
        for (String condition : conditions) {
            conditionsPath.append(condition).append(".actions.");
        }

        String path;
        ActionCategory category = ActionCategory.getByMaterial(actionBlock.getType());
        if (category != null && category.isMultiAction()) {
            path = ("code.blocks.exec_block_" + z + "_" + y + ".actions." + conditionsPath);
            path = path.substring(0, path.length() - 9); // Remove last ".actions".
        } else {
            path = "code.blocks.exec_block_" + z + "_" + y + ".actions." + conditionsPath + "action_block" + getBlockActionNumber(actionBlock);
        }

        return path;
    }

    public boolean saveCode() {
        try {
            scriptConfig.save(file);
        } catch (IOException error) {
            sendCriticalErrorMessage("An IO Exception has occurred while saving code. " + error.getLocalizedMessage());
            return false;
        }
        return true;
    }

    /**
     * Loads code from codeScript.yml file.
     */
    public void loadCode() {
        executors.load(file);
    }

    public Executors getExecutors() {
        return executors;
    }
}

