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
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executors;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getPlanetScriptFile;

/**
 * <h1>CodeScript</h1>
 * This class represents configuration file that stores planet's code.
 * It has methods to load code and save coding blocks.
 * @see CodingBlockParser
 */
public class CodeScript {

    private final Planet planet;
    private final Executors executors;
    private YamlConfiguration scriptConfig;

    public CodeScript(Planet planet) {
        this.planet = planet;
        this.executors = new Executors(planet);
        this.scriptConfig = new YamlConfiguration();
    }

    /**
     * Loads code from codeScript.yml file.
     */
    public void loadCode() {
        scriptConfig = YamlConfiguration.loadConfiguration(getPlanetScriptFile(planet));
        sendCodingDebugLog(planet,"Loading code...");
        new BukkitRunnable() {
            @Override
            public void run() {
                executors.load(getPlanetScriptFile(planet));
            }
        }.run();
    }

    /**
     * Saves code script config into file.
     * @return true - if saved, false - if failed.
     */
    public boolean saveCode() {
        try {
            scriptConfig.save(getPlanetScriptFile(planet));
            return true;
        } catch (IOException error) {
            sendCriticalErrorMessage("An IO Exception has occurred while saving code.", error);
            return false;
        }
    }

    /**
     * Moves stored code in old-code section to prevent being overwritten by new code.
     */
    public void clear() {
        executors.getExecutorsList().clear();
        ConfigurationSection section = scriptConfig.getConfigurationSection("code.blocks");
        if (section == null) return;
        scriptConfig.set("old-code.blocks", null);
        Map<String, Object> newCode = section.getValues(false);
        scriptConfig.set("old-code.blocks", newCode);
        scriptConfig.set("code.blocks", null);
        try {
            scriptConfig.save(getPlanetScriptFile(planet));
        } catch (IOException exception) {
            sendCriticalErrorMessage("An error has occurred while clearing and saving code script " + this.getPlanet().getWorldName(),exception);
        }
    }

    public void unload() {
        scriptConfig = new YamlConfiguration();
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
    public void saveActionBlock(Block executorBlock, List<String> multiActions, Block actionBlock, ActionCategory category, ActionType type, Target target) {
        String path = getActionBlockPath(executorBlock,actionBlock,multiActions);

        scriptConfig.set(path + ".category", category.name());
        scriptConfig.set(path + ".type", type.name());

        if (type == ActionType.LAUNCH_FUNCTION || type == ActionType.LAUNCH_METHOD) {
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
    public void saveArguments(Block executorBlock, List<String> multiActions, Block actionBlock, String argument, Object value, ValueType type) {
        String path = getActionBlockPath(executorBlock,actionBlock,multiActions);
        scriptConfig.set(path + ".arguments." + argument + ".type",type.name());
        scriptConfig.set(path + ".arguments." + argument + ".value",value);
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
            conditionsPath.append(condition).append(".actions.");
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

    public Executors getExecutors() {
        return executors;
    }

    public Planet getPlanet() {
        return planet;
    }
}

