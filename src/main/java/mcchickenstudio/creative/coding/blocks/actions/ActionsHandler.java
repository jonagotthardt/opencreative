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

package mcchickenstudio.creative.coding.blocks.actions;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.actions.controlactions.lines.WaitAction;
import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import mcchickenstudio.creative.coding.blocks.events.EventValues;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.variables.VariableLink;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static mcchickenstudio.creative.utils.ErrorUtils.sendPlotCodeErrorMessage;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.MessageUtils.messageExists;

/**
 * <h1>ActionHandler</h1>
 * This class represents actions handler that executes every action in list.
 * Executors and code blocks with brackets use handlers to execute code inside
 * brackets.
 * @see Executor
 * @see mcchickenstudio.creative.coding.blocks.conditions.Condition
 */
public class ActionsHandler {

    private final Executor executor;
    private final CreativeEvent event;
    private final EventValues variables;
    private final Action action;

    private final Set<Entity> selectedTargets;
    private final ActionsHandler parentActionsHandler;
    private final Queue<Action> actionsQueue = new LinkedList<>();
    private final boolean doNotUseTryFlag;
    private boolean stopped = false;
    private long waitDelay = 0;

    public ActionsHandler(Executor executor) {
        this.executor = executor;
        this.event = executor.getEvent();
        this.variables = new EventValues();
        Map<EventValues.Variable,Object> oldVars = executor.getVariables().getMap();
        for (EventValues.Variable var : oldVars.keySet()) {
            this.variables.setVariable(var,oldVars.get(var));
        }
        this.selectedTargets = new HashSet<>(event.getSelection());
        this.parentActionsHandler = null;
        this.action = null;
        this.doNotUseTryFlag = false;
    }

    public ActionsHandler(Action action) {
        this.parentActionsHandler = action.getHandler();
        ActionsHandler mainHandler = getMainActionHandler();
        this.executor = mainHandler.executor;
        this.event = mainHandler.event;
        this.variables = mainHandler.variables;
        this.action = action;
        this.selectedTargets = new HashSet<>(parentActionsHandler.selectedTargets);
        /*if (action.getActionType() == ActionType.HANDLER_CATCH_ERROR) {
            this.doNotUseTryFlag = true;
        } else {*/
            this.doNotUseTryFlag = false;
        //}
    }

    public final void executeActions(List<Action> actions) {
        actionsQueue.addAll(actions);
        executeNextAction();
    }

    private void executeNextAction() {
        if (actionsQueue.isEmpty()) {
            if (getMainActionHandler() == this) {
                executor.getPlot().getWorldVariables().garbageCollector(this);
            }

            /*if (action instanceof RepeatAction repeatAction) {
                if (action instanceof RepeatForLoopAction forLoopAction) {
                    VariableLink link = forLoopAction.getArguments().getVariableLink("variable",forLoopAction);
                    double add = forLoopAction.getArguments().getValue("add",1.0d,forLoopAction);
                    String type = forLoopAction.getArguments().getValue("value","less",forLoopAction);
                    double untilValue = forLoopAction.getArguments().getValue("range",10.0d,forLoopAction);
                    if (link == null) {
                        return;
                    }
                    double currentValue = forLoopAction.getArguments().getValue("variable",0.0d,forLoopAction);
                    boolean execute = switch (type.toLowerCase()) {
                        case "less" -> currentValue < untilValue;
                        case "less-equals" -> currentValue <= untilValue;
                        case "greater" -> currentValue > untilValue;
                        case "greater-equals" -> currentValue >= untilValue;
                        default -> false;
                    };
                    forLoopAction.setVarValue(link,currentValue+add);
                    if (execute) {
                        forLoopAction.executeActions();
                    }
                }
                repeatAction.prepareAndExecute(this);
            }
            */
            return;
        }
        Action nextAction = actionsQueue.poll();
        if (nextAction != null) {
            prepareAction(nextAction);
        }
    }

    public void prepareAction(Action action) {
        if (waitDelay < 1 ) {
            executeAction(action);
        } else {
            BukkitRunnable executeActionLaterRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                if (action == null || action.getPlot() == null || action.getPlot().getPlotMode() != Plot.Mode.PLAYING || !action.getPlot().isLoaded) {
                    cancel();
                }
                executeAction(action);
                action.getPlot().removeBukkitRunnable(this);
                }
            };
            action.getPlot().addBukkitRunnable(executeActionLaterRunnable);
            executeActionLaterRunnable.runTaskLater(Main.getPlugin(),waitDelay);
        }
    }

    private void executeAction(Action action) {
        if (!stopped) {
            if (doNotUseTryFlag) {
                action.prepareAndExecute(this);
            } else {
                try {
                    action.prepareAndExecute(this);
                } catch (Exception error) {
                    String id = error.getClass().getSimpleName().toLowerCase();
                    sendPlotCodeErrorMessage(executor, action, getLocaleMessage("plot-code-error." + (messageExists("plot-code-error." + id) ? id : "unknown")) + (error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage()).replace("mcchickenstudio.creative.coding.",""), error);
                }
            }
        }
        if (!(action instanceof WaitAction)) {
            setWaitDelay(0);
        } else {
            setWaitDelay(((WaitAction) action).getTime());
        }
        executeNextAction();
    }

    public void removeAllActions() {
        actionsQueue.clear();
    }

    public long getWaitDelay() {
        return waitDelay;
    }

    public ActionsHandler getMainActionHandler() {
        ActionsHandler handler = this.getParentActionHandler();
        ActionsHandler lastHandler = this;
        while (handler != null) {
            lastHandler = handler;
            handler = handler.getParentActionHandler();
        }
        return lastHandler;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public ActionsHandler getParentActionHandler() {
        return parentActionsHandler;
    }

    public void setWaitDelay(long waitDelay) {
        this.waitDelay = waitDelay;
    }

    public CreativeEvent getEvent() {
        return event;
    }

    public EventValues getVariables() {
        return getMainActionHandler().variables;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setVarValue(EventValues.Variable var, Object value) {
        getVariables().setVariable(var,value);
    }

    public Object getVarValue(EventValues.Variable var) {
        return getVariables().getVarValue(var);
    }

    public boolean hasTempVariable(EventValues.Variable var) {
        return getVariables().getVarValue(var) == null;
    }

    @Override
    public String toString() {
        return "ActionsHandler. Plot: " + executor.getPlot() + " WaitDelay: " + waitDelay + " Stopped: " + stopped + " Queue Size: " + actionsQueue.size();
    }

    public Set<Entity> getSelectedTargets() {
        return selectedTargets;
    }
}
