/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

public interface CodeStorage {

    /**
     * Loads data from file.
     * <p>
     * Any errors loading the Configuration will be logged and then ignored.
     * If the specified input is not a valid config, a blank config will be
     * returned.
     * <p>
     * The encoding used may follow the system dependent default.
     *
     * @param file Input file
     */
     void loadCode(@NotNull File file);

    /**
     * Saves executor block data in configuration file.
     *
     * @param block    executor coding block.
     * @param category category of executor.
     * @param type     type of executor.
     * @param debug    should print debug logs or not.
     */
    void saveExecutorBlock(@NotNull Block block, boolean notDependsOnHeight,
                           @NotNull ExecutorCategory category, @NotNull ExecutorType type,
                           boolean debug);

    /**
     * Saves executor block data in configuration file.
     *
     * @param block    executor coding block.
     * @param category category of executor.
     * @param type     type of executor.
     */
    void saveExecutorBlock(@NotNull Block block, boolean notDependsOnHeight,
                                  @NotNull ExecutorCategory category, @NotNull ExecutorType type);

    /**
     * Saves action block data in configuration file.
     *
     * @param multiActions list of multi actions.
     * @param actionBlock  action coding block.
     * @param category     category of action.
     * @param type         type of action.
     * @param target       target for action.
     */
    void saveActionBlock(@NotNull Block executorBlock, boolean notDependsOnHeight,
                         @NotNull List<String> multiActions, @NotNull Block actionBlock,
                         @NotNull ActionCategory category, @NotNull ActionType type,
                         @NotNull Target target);

    /**
     * Saves arguments for action block in configuration file.
     *
     * @param multiActions list of multi actions.
     * @param actionBlock  action block to set arguments.
     * @param argument     argument to set.
     * @param value        value of argument.
     * @param type         value type.
     */
    void saveArguments(@NotNull Block executorBlock, boolean notDependsOnHeight,
                       @NotNull List<String> multiActions, @NotNull Block actionBlock,
                       @NotNull String argument, @Nullable Object value, @NotNull ValueType type);


    /**
     * Sets value in path.
     *
     * @param path path for value.
     * @param value new value.
     */
    void set(@NotNull String path, @Nullable Object value);

    /**
     * Returns configuration section, or null - if not exists.
     *
     * @param path path of section.
     * @return configuration section, or null.
     */
    @Nullable ConfigurationSection getSection(@NotNull String path);

    /**
     * Saves code to file.
     *
     * @param file file to save in.
     */
    void saveToFile(@NotNull File file) throws IOException;

}
