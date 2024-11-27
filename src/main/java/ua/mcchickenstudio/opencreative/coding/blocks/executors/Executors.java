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

package ua.mcchickenstudio.opencreative.coding.blocks.executors;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.plots.Plot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>Executors</h1>
 * This class represents Executors in every plot code script.
 * @since 5.0
 * @version 5.0
 * @author McChicken Studio
 */
public class Executors {

    protected final Plot plot;
    private List<Executor> executorsList = new ArrayList<>();

    private final Map<Executor,Integer> lastExecutorsCallsAmount = new HashMap<>();

    public Executors(Plot plot) {
        this.plot = plot;
    }

    public static void activate(WorldEvent event) {
        Plot plot = event.getPlot();
        if (plot == null || plot.getTerritory().getScript() == null || plot.getTerritory().getScript().getExecutors() == null) return;
        Executors executors = plot.getTerritory().getScript().getExecutors();
        for (Executor executor : executors.executorsList) {
            if (executor.getExecutorType().getEventClass() == event.getClass()) {
                activate(executor,event);
            }
        }
    }

    public static void activate(Executor executor, WorldEvent event) {
        Plot plot = executor.getPlot();
        if (plot == null || plot.getTerritory().getScript() == null || plot.getTerritory().getScript().getExecutors() == null) return;
        Executors executors = plot.getTerritory().getScript().getExecutors();
        if (executors.getLastExecutorCallsAmount(executor) > plot.getLimits().getCodeOperationsLimit()) {
            executors.clearExecutionsAmount(executor);
            stopPlotCode(plot);
            sendPlotCodeCriticalErrorMessage(plot,executor,getLocaleMessage("plot-code-error.operations-limit",false).replace("%limit%",String.valueOf(plot.getLimits().getCodeOperationsLimit())));
        } else {
            executors.increaseCallsAmount(executor);
            executor.run(event);
            new BukkitRunnable() {
                @Override
                public void run() {
                    executors.decreaseCallsAmount(executor);
                }
            }.runTaskLater(OpenCreative.getPlugin(),35L);
        }
    }

    public static void simulateIncreaseCall(Executor executor) {
        Plot plot = executor.getPlot();
        Executors executors = plot.getTerritory().getScript().getExecutors();
        if (executors.getLastExecutorCallsAmount(executor) > plot.getLimits().getCodeOperationsLimit()) {
            executors.clearExecutionsAmount(executor);
            stopPlotCode(plot);
            sendPlotCodeCriticalErrorMessage(plot,executor,getLocaleMessage("plot-code-error.operations-limit",false).replace("%limit%",String.valueOf(plot.getLimits().getCodeOperationsLimit())));
        } else {
            executors.increaseCallsAmount(executor);
            new BukkitRunnable() {
                @Override
                public void run() {
                    executors.decreaseCallsAmount(executor);
                }
            }.runTaskLater(OpenCreative.getPlugin(),35L);
        }
    }

    public void load(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("code.blocks");
        if (section != null) {
            List<Executor> executors = new ArrayList<>();
            Set<String> keys = section.getKeys(false);
            String path;
            for (String key : keys) {
                path = "code.blocks." + key;
                if (config.getString(path + ".type") != null) {
                    Executor executor = createExecutor(config,path);
                    if (executor != null) {
                        executors.add(executor);
                    }
                }
            }
            this.executorsList = executors;
        }
    }

    public List<Executor> getExecutorsList() {
        return executorsList;
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
            ExecutorType type = ExecutorType.valueOf(config.getString(path+".type"));
            if (type == ExecutorType.CYCLE) {
                String name = config.getString(path+".name");
                int time = config.getInt(path+".time");
                /*
                 * We will not create cycle without name,
                 * because it can't be called in code.
                 */
                if (name != null) {
                    executor = type.getExecutorClass().getConstructor(Plot.class,int.class,int.class,int.class,String.class,int.class).newInstance(plot,coords[0],coords[1],coords[2],name,(time >= 5 && time <= 3600 ? time : 20));
                }
            } else if (type == ExecutorType.FUNCTION || type == ExecutorType.METHOD) {
                String name = config.getString(path+".name");
                /*
                 * We will not create function without name,
                 * because it can't be called in code.
                 */
                if (name != null) {
                    executor = type.getExecutorClass().getConstructor(Plot.class,int.class,int.class,int.class,String.class).newInstance(plot,coords[0],coords[1],coords[2],name);
                }
            } else {
                executor = type.getExecutorClass().getConstructor(Plot.class,int.class,int.class,int.class).newInstance(plot,coords[0],coords[1],coords[2]);
            }
            List<Action> allActionsList = createActionList(executor, path + ".actions",config);
            if (!allActionsList.isEmpty()) {
                executor.setActions(allActionsList);
            }
        } catch (Exception e) {
            sendCriticalErrorMessage("Failed to create an executor",e);
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
            Target target = Target.DEFAULT;
            String targetString = config.getString(path + ".target");
            if (targetString != null && !targetString.isEmpty()) {
                target = Target.valueOf(targetString);
            }
            ConfigurationSection section = config.getConfigurationSection(path + ".arguments");
            if (section != null) {
                args.load(section);
            }
            if (actionType == ActionType.LAUNCH_FUNCTION || actionType == ActionType.LAUNCH_METHOD) {
                if (config.getString(path+".name") != null) {
                    args.setArgumentValue("name", ValueType.TEXT,config.getString(path+".name"));
                }
            } else if (actionType == ActionType.SELECTION_SET || actionType == ActionType.SELECTION_ADD || actionType == ActionType.SELECTION_REMOVE) {
                if (config.getConfigurationSection(path+".condition") != null) {
                    boolean isOpposed = config.getBoolean(path+".condition.opposed",false);
                    ActionCategory conditionCategory = ActionCategory.valueOf(config.getString(path+".condition.category"));
                    ActionType conditionType = ActionType.valueOf(config.getString(path+".condition.type"));
                    return actionType.getActionClass().getConstructor(Executor.class,int.class, Arguments.class, ActionCategory.class, ActionType.class, boolean.class).newInstance(executor,config.getInt(path+".location.x"),args,conditionCategory,conditionType,isOpposed);
                } else if (config.getString(path+".target") != null){
                    if (targetString != null && !targetString.isEmpty()) {
                        target = Target.valueOf(targetString);
                    }
                    return actionType.getActionClass().getConstructor(Executor.class,int.class, Arguments.class, Target.class).newInstance(executor,config.getInt(path+".location.x"),args,target);
                }
                if (config.getString(path+".condition.type") != null) {
                    args.setArgumentValue("name", ValueType.TEXT,config.getString(path+".name"));
                }
            }
            if (actionType.getCategory().isMultiAction()) {
                if (config.getConfigurationSection(path+".actions") != null) {
                    if (actionType.getCategory().isCondition()) {
                        boolean isOpposed = config.getBoolean(path+".opposed",false);
                        return actionType.getActionClass().getConstructor(Executor.class, Target.class, int.class,Arguments.class,List.class,boolean.class).newInstance(executor,target,config.getInt(path+".location.x"),args,createActionList(executor,path+".actions",config),isOpposed);
                    } else {
                        return actionType.getActionClass().getConstructor(Executor.class, Target.class, int.class,Arguments.class,List.class).newInstance(executor,target,config.getInt(path+".location.x"),args,createActionList(executor,path+".actions",config));
                    }
                }
            }
            return actionType.getActionClass().getConstructor(Executor.class, Target.class, int.class,Arguments.class).newInstance(executor,target,config.getInt(path+".location.x"),args);
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
