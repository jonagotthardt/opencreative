/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package mcchickenstudio.creative.coding;

import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.blocks.executors.Executors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import mcchickenstudio.creative.coding.menus.executors.PlayerExecutorSubtype;
import mcchickenstudio.creative.plots.Plot;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static mcchickenstudio.creative.utils.ErrorUtils.*;
import static mcchickenstudio.creative.utils.FileUtils.*;
import static mcchickenstudio.creative.utils.PlayerUtils.translateBlockSign;

public class CodeScript {

    public final Plot linkedPlot;
    private final File file;
    public final List<Integer> blockActionsX = new ArrayList<>();
    private final Executors executors;

    public CodeScript(Plot linkedPlot, File file) {
        this.linkedPlot = linkedPlot;
        this.file = file;
        for (int x = 6; x < 98; x = x + 2) {
            blockActionsX.add(x);
        }
        this.executors = new Executors(linkedPlot);
    }

    public boolean exists() {

        File linkedPlotFolder = getPlotFolder(linkedPlot);
        if (linkedPlotFolder == null) return false;

        return true;
    }

    public void setExecBlock(Block block, String type, String subtype) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        YamlConfiguration scriptConfig = YamlConfiguration.loadConfiguration(file);

        String path = "code.blocks.exec_block_" + z + "_" + y;
        scriptConfig.set(path + ".type", type);
        scriptConfig.set(path + ".subtype", subtype);
        scriptConfig.set(path + ".location.x", x);
        scriptConfig.set(path + ".location.y", y);
        scriptConfig.set(path + ".location.z", z);

        try {
            scriptConfig.save(file);
        } catch (IOException error) {
            sendCriticalErrorMessage("Произошла ошибка при сохранении файла скрипта. " + error.getLocalizedMessage());
        }
    }

    @Deprecated
    public void addExecutor(Block block, String mainType, PlayerExecutorSubtype mainSubType) {

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        YamlConfiguration scriptConfig = YamlConfiguration.loadConfiguration(file);

        String path = "code.blocks.exec_block_" + z + "_" + y;
        scriptConfig.set(path + ".type", mainType);
        scriptConfig.set(path + ".subtype", mainSubType.toString());
        scriptConfig.set(path + ".location.x", 4);
        scriptConfig.set(path + ".location.y", y);
        scriptConfig.set(path + ".location.z", z);

        try {
            scriptConfig.save(file);
        } catch (IOException error) {
            sendCriticalErrorMessage("Произошла ошибка при сохранении файла скрипта. " + error.getLocalizedMessage());
        }
    }

    public void setActionBlock(List<String> conditions, Block executorBlock, Block actionBlock, String actionType, String actionSubtype, int actionParameter, List<String> arguments) {
        int x = actionBlock.getX();
        int y = actionBlock.getY();
        int z = actionBlock.getZ();

        YamlConfiguration scriptConfig = YamlConfiguration.loadConfiguration(file);

        StringBuilder conditionsPath = new StringBuilder();
        for (String condition : conditions) {
            conditionsPath.append(condition).append(".actions.");
        }

        String path = "code.blocks.exec_block_" + z + "_" + y + ".actions." + conditionsPath + "action_block" + getBlockActionNumber(actionBlock);

        scriptConfig.set(path + ".type", actionType);
        scriptConfig.set(path + ".subtype", actionSubtype);
        scriptConfig.set(path + ".argument", actionParameter);

        scriptConfig.set(path + ".location.x", x);
        scriptConfig.set(path + ".arguments", arguments);

        try {
            scriptConfig.save(file);
        } catch (IOException error) {
            sendCriticalErrorMessage("Произошла ошибка при сохранении файла скрипта. " + error.getLocalizedMessage());
        }
    }

    public void setConditionBlock(List<String> conditions, Block executorBlock, Block actionBlock, String actionType, String actionSubtype, int actionParameter, List<String> arguments) {
        int x = actionBlock.getX();
        int y = actionBlock.getY();
        int z = actionBlock.getZ();

        YamlConfiguration scriptConfig = YamlConfiguration.loadConfiguration(file);

        StringBuilder conditionsPath = new StringBuilder();
        for (String condition : conditions) {
            conditionsPath.append(condition).append(".actions.");
        }

        String path = ("code.blocks.exec_block_" + z + "_" + y + ".actions." + conditionsPath);
        //TODO: не использовать этот костыль
        path = path.substring(0,path.length()-9);

        scriptConfig.set(path + ".type", actionType);
        scriptConfig.set(path + ".subtype", actionSubtype);
        scriptConfig.set(path + ".argument", actionParameter);

        scriptConfig.set(path + ".location.x", x);
        scriptConfig.set(path + ".arguments", arguments);

        try {
            scriptConfig.save(file);
        } catch (IOException error) {
            sendCriticalErrorMessage("Произошла ошибка при сохранении файла скрипта. " + error.getLocalizedMessage());
        }
    }

    public int getBlockActionNumber(Block block) {
        return blockActionsX.indexOf(block.getX()) + 1;
    }

    public void setSignLineSubtype(Location location, String line) {
        Block block = location.getBlock();
        if (block.getType() == Material.OAK_WALL_SIGN) {
            Sign signBlock = (Sign) block.getState();
            signBlock.setLine(2, line);
            signBlock.update();
        }
        translateBlockSign(block);
    }

    public void clear() {

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("code.blocks");

        config.set("old-code.blocks", null);
        if (section == null) return;
        Map<String, Object> newCode = section.getValues(true);
        config.set("old-code.blocks", newCode);
        config.set("code.blocks", null);

        try {
            config.save(file);
        } catch (IOException exception) {
            sendWarningErrorMessage("Произошла ошибка при попытке сохранить новый код в старый... " + this.linkedPlot.worldName);
        }

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

