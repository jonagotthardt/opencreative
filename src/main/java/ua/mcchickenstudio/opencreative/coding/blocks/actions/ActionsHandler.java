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

package ua.mcchickenstudio.opencreative.coding.blocks.actions;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.controlactions.lines.WaitAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.controlleractions.other.MeasureTimeAction;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.PlayerException;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlanetCodeErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.messageExists;

/**
 * <h1>ActionHandler</h1>
 * This class represents actions handler that executes every action in list.
 * Executors and code blocks with brackets use handlers to execute code inside
 * brackets.
 * @see Executor
 * @see ua.mcchickenstudio.opencreative.coding.blocks.conditions.Condition
 * @since 5.0
 * @version 5.0
 * @author McChicken Studio
 */
public class ActionsHandler {

    private final Executor executor;
    private final WorldEvent event;
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
        this.action = action;
        this.selectedTargets = new HashSet<>(parentActionsHandler.selectedTargets);
        this.doNotUseTryFlag = action.getActionType() == ActionType.CONTROLLER_CATCH_ERROR || parentActionsHandler.doNotUseTryFlag;
    }

    public final void executeActions(List<Action> actions) {
        actionsQueue.addAll(actions);
        executeNextAction();
    }

    public final void addActions(List<Action> actions) {
        List<Action> current = new ArrayList<>(actionsQueue);
        actionsQueue.clear();
        actionsQueue.addAll(actions);
        actionsQueue.addAll(current);
    }

    private void executeNextAction() {
        if (executor.getPlanet().getMode() != Planet.Mode.PLAYING) {
            actionsQueue.clear();
            return;
        }
        if (actionsQueue.isEmpty()) {
            if (action instanceof MeasureTimeAction timer) {
                timer.measure();
            }
            if (getMainActionHandler() == this) {
                executor.getPlanet().getVariables().garbageCollector(this);
            }
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
                    if (action == null || action.getPlanet() == null || action.getPlanet().getMode() != Planet.Mode.PLAYING || !action.getPlanet().isLoaded()) {
                        cancel();
                    }
                    if (action != null) {
                        executeAction(action);
                        action.getPlanet().getTerritory().removeBukkitRunnable(this);
                    }
                }
            };
            action.getPlanet().getTerritory().addBukkitRunnable(executeActionLaterRunnable);
            executeActionLaterRunnable.runTaskLater(OpenCreative.getPlugin(),waitDelay);
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
                    sendPlanetCodeErrorMessage(executor, action,
                            getLocaleMessage("coding-error." + (messageExists("coding-error." + id) ? id : "unknown"))
                                    .replace("%player%", error instanceof PlayerException playerException ? playerException.getPlayerName() : "")
                            + (error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage()).replace("ua.mcchickenstudio.opencreative.coding.",""),
                            error);
                    removeAllActions();
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

    public WorldEvent getEvent() {
        return event;
    }

    public Executor getExecutor() {
        return executor;
    }

    @Override
    public String toString() {
        return "ActionsHandler. Planet: " + executor.getPlanet() + " WaitDelay: " + waitDelay + " Stopped: " + stopped + " Queue Size: " + actionsQueue.size();
    }

    public Set<Entity> getSelectedTargets() {
        return selectedTargets;
    }
}
