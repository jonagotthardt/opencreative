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

package mcchickenstudio.creative.coding.test;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static mcchickenstudio.creative.utils.BlockUtils.getSignLine;
import static mcchickenstudio.creative.utils.BlockUtils.setSignLine;

public class LegacyConvertor {

    private boolean isRunning;
    private final BukkitRunnable runnable;
    private final List<Plot> plots;
    private int convertedPlotsAmount = 0;

    public LegacyConvertor(List<Plot> plots) {
        this.plots = plots;
        this.runnable = new BukkitRunnable() {

            private final int maxAwaitingTime = 30;

            private Plot currentPlot = plots.get(0);
            private int wastedTime = 0;
            private boolean convertedDevPlot = false;
            private boolean convertedCodeScript = false;

            @Override
            public void run() {
                wastedTime += 1;
                if (wastedTime > maxAwaitingTime) {
                    plots.remove(currentPlot);
                    if (plots.isEmpty()) {
                        end();
                        cancel();
                        return;
                    }
                    currentPlot = plots.getFirst();
                }
                if (!convertedDevPlot) {
                    if (currentPlot.devPlot.world == null) {
                        PlotManager.getInstance().loadPlot(currentPlot);
                        currentPlot.devPlot.loadDevPlotWorld();
                        return;
                    }
                    convertDevPlot(currentPlot.devPlot);
                }

            }

            public void resetValues() {
                wastedTime = 0;
                convertedDevPlot = false;
                convertedCodeScript = false;
            }
        };
    }

    public void convertCodingBlock(Block mainBlock, Location signLocation, String first, String second, String third, String fourth) {
        if ("action_player".equalsIgnoreCase(first)) {
            setSignLine(signLocation,(byte) 1,"player_action");
        }
    }

    public void start() {
        if (isRunning) return;
        isRunning = true;
        runnable.runTaskTimer(Main.getPlugin(),20L,20L);
    }

    public void end() {
        isRunning = false;
    }

    public void convertDevPlot(DevPlot devPlot) {
        World world = devPlot.world;
        byte y = 1;
        for (byte z = 4; z <= 96; z = (byte)(z+4)) {
            for (byte x = 4; x <= 96; x = (byte) (x + 2)) {
                Block codingBlock = world.getBlockAt(x, y, z);
                Location location = codingBlock.getRelative(BlockFace.SOUTH).getLocation();
                String firstSignLine = getSignLine(location,(byte) 1);
                String secondSignLine = getSignLine(location,(byte) 2);
                String thirdSignLine = getSignLine(location,(byte) 3);
                String fourthSignLine = getSignLine(location,(byte) 4);
                convertCodingBlock(codingBlock,location,firstSignLine,secondSignLine,thirdSignLine,fourthSignLine);
            }
        }
        PlotManager.getInstance().unloadPlot(devPlot.getPlot());
    }

    public void convertScript(File file) {
        YamlConfiguration script = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = script.getConfigurationSection("code.blocks");
        if (section != null) {
            List<Executor> executors = new ArrayList<>();
            Set<String> keys = section.getKeys(false);
            String path;
            for (String key : keys) {
                path = "code.blocks." + key;
                ConfigurationSection blockSection = script.getConfigurationSection(path);
                if (script.getString(path + ".type") != null && blockSection != null) {
                    ConfigurationSection newSection = convertScriptSection(blockSection);
                    if (!blockSection.equals(newSection)) {
                        script.set(path,newSection);
                    }
                }
            }
        }
        try {
            script.save(file);
        } catch (IOException e) {

        }
    }

    public ConfigurationSection convertScriptSection(ConfigurationSection section) {
        if (section.getString("type","").equalsIgnoreCase("action_player")) {
            section.set("type","player_action");
        }
        return section;
    }

}
