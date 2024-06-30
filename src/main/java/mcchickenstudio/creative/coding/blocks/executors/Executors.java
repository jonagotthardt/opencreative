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

package mcchickenstudio.creative.coding.blocks.executors;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

import static mcchickenstudio.creative.utils.ErrorUtils.sendPlotCodeErrorMessage;
import static mcchickenstudio.creative.utils.ErrorUtils.stopPlotCode;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>Executors</h1>
 * This class represents Executors in every plot code script.
 * @since 1.5
 * @version 1.5
 * @author McChicken Studio
 */
public class Executors {

    protected final Plot plot;
    private List<Executor> executorsList = new ArrayList<>();
    private final Map<Executor,Integer> lastExecutorsCallsAmount = new HashMap<>();

    public Executors(Plot plot) {
        this.plot = plot;
    }

    public static boolean activate(CreativeEvent event) {
        Plot plot = event.getPlot();
        if (plot == null || plot.script == null || plot.script.getExecutors() == null) return false;
        Executors executors = plot.script.getExecutors();
        for (Executor executor : executors.executorsList) {
            if (executor.getExecutorType().getEventClass() == event.getClass()) {
                if (executors.getLastExecutorCallsAmount(executor) > plot.codeOperationsLimit) {
                    executors.clearExecutionsAmount(executor);
                    sendPlotCodeErrorMessage(plot,executor,getLocaleMessage("plot-code-error.operations-limit",false).replace("%limit%",String.valueOf(plot.codeOperationsLimit)));
                    stopPlotCode(plot);
                    return false;
                } else {
                    executors.increaseCallsAmount(executor);
                    executor.run(event);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            executors.decreaseCallsAmount(executor);
                        }
                    }.runTaskLater(Main.getPlugin(),35L);
                }

            }
        }
        return true;
    }

    public void load(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("code.blocks");
        if (section != null) {
            List<Executor> executors = new ArrayList<>();

            Set<String> keys = section.getKeys(false);
            String path = "";

            for (String key : keys) {
                path = "code.blocks." + key;
                if (config.getString(path + ".type") != null) {
                    Executor executor = createExecutor(config,path);
                    if (executor != null) executors.add(executor);
                }
            }
            this.executorsList = executors;
        }
    }

    private int[] getCoords(YamlConfiguration config, String path) {
        int[] coords = new int[3];
        coords[0] = config.getInt(path + ".location.x");
        coords[1] = config.getInt(path + ".location.y");
        coords[2] = config.getInt(path + ".location.z");
        return coords;
    }

    private Executor createExecutor(YamlConfiguration config, String path) {
        Executor executor = null;
        try {
            int[] coords = getCoords(config,path);
            executor = ExecutorType.valueOf(config.getString(path+".type")).getExecutorClass().getConstructor(Plot.class,int.class,int.class,int.class).newInstance(plot,coords[0],coords[1],coords[2]);
            List<Action> allActionsList = createActionList(executor, path + ".actions",config);
            if (!allActionsList.isEmpty()) {
                executor.setActions(allActionsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executor;
    }

    private List<Action> createActionList(Executor executor, String path, YamlConfiguration config) {
        List<Action> actionList = new ArrayList<>();
        ConfigurationSection actions = config.getConfigurationSection(path);
        if (actions != null) {
            Set<String> actionsBlocks = actions.getKeys(false);
            for (String actionBlock : actionsBlocks) {
                String actionPath = path + "." + actionBlock;
                Action action = createAction(executor, actionPath, config);
                if (action != null) {
                    actionList.add(action);
                }
            }
        }
        return actionList;
    }

    private Action createAction(Executor executor, String path, YamlConfiguration config) {

        String type = config.getString(path + ".type");
        if (type == null) return null;

        try {
            ActionType actionType = ActionType.valueOf(type);
            Arguments args = new Arguments(executor.getPlot(),executor);
            ConfigurationSection section = config.getConfigurationSection(path + ".arguments");
            if (section != null) {
                args.load(section);
            }
            if (config.getConfigurationSection(path+".actions") != null) {
                return actionType.getActionClass().getConstructor(Executor.class,int.class,Arguments.class,List.class).newInstance(executor,config.getInt(path+".location.x"),args,createActionList(executor,path+".actions",config));
            }
            return actionType.getActionClass().getConstructor(Executor.class,int.class,Arguments.class).newInstance(executor,config.getInt(path+".location.x"),args);
        } catch (Exception error) {
            return null;
        }
    }

    private void increaseCallsAmount(Executor executor) {
        lastExecutorsCallsAmount.put(executor,getLastExecutorCallsAmount(executor)+1);
    }

    private void decreaseCallsAmount(Executor executor) {
        lastExecutorsCallsAmount.put(executor,getLastExecutorCallsAmount(executor)-1);
    }

    private int getLastExecutorCallsAmount(Executor executor) {
        if (!lastExecutorsCallsAmount.containsKey(executor)) return 0;
        return lastExecutorsCallsAmount.get(executor);
    }

    private void clearExecutionsAmount(Executor executor) {
        lastExecutorsCallsAmount.remove(executor);
    }

}
