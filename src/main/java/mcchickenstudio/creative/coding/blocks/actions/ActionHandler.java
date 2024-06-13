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
import mcchickenstudio.creative.coding.blocks.actions.controlactions.events.CancelEventAction;
import mcchickenstudio.creative.coding.blocks.actions.controlactions.lines.StopCodeLineAction;
import mcchickenstudio.creative.coding.blocks.actions.controlactions.lines.WaitAction;
import mcchickenstudio.creative.coding.blocks.conditions.Condition;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static mcchickenstudio.creative.utils.ErrorUtils.sendPlotCodeErrorMessage;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class ActionHandler {

    private final Executor executor;
    private long waitDelay = 0;
    private final Queue<Action> actionQueue = new LinkedList<>();

    private void clear() {
        actionQueue.clear();
    }

    public ActionHandler(Executor executor) {
        this.executor = executor;
    }

    public void executeActions(List<Action> actions) {
        actionQueue.addAll(actions);
        runNextAction();
    }

    public void runNextAction() {
        if (actionQueue.isEmpty()) {
            return;
        }
        Action action = actionQueue.poll();
        if (action != null) {
            if (action instanceof StopCodeLineAction) {
                executor.getHandler().clear();
            }
            executeAction(action,executor.getEvent().getSelection());
        }
    }

    public void executeAction(Action action, List<Entity> selection) {
        if (waitDelay < 1 ) {
            runAction(action,selection);
        } else {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (action == null || action.getPlot() == null || action.getPlot().plotMode != Plot.Mode.PLAYING || !action.getPlot().isLoaded) {
                        cancel();
                    }
                    runAction(action,selection);
                    action.getPlot().removeBukkitRunnable(this);
                }
            };
            action.getPlot().addBukkitRunnable(runnable);
            runnable.runTaskLater(Main.getPlugin(),waitDelay);
        }
    }

    private void runAction(Action action, List<Entity> selection) {
        try {
            action.run(selection);
        } catch (IndexOutOfBoundsException e) {
            sendPlotCodeErrorMessage(executor, action, getLocaleMessage("plot-code-error.arguments"));
        } catch (NumberFormatException e) {
            sendPlotCodeErrorMessage(executor, action, getLocaleMessage("plot-code-error.wrong-number"));
        } catch (IllegalArgumentException e) {
            sendPlotCodeErrorMessage(executor, action, getLocaleMessage("plot-code-error.wrong-argument") + e.getMessage());
        } catch (Exception e) {
            sendPlotCodeErrorMessage(executor, action, getLocaleMessage("plot-code-error.unknown") + e.getMessage());
        } finally {
            if (!(action instanceof WaitAction)) {
                setWaitDelay(0);
            } else {
                setWaitDelay(((WaitAction) action).getTime());
            }
            runNextAction();
        }
    }

    public long getWaitDelay() {
        return waitDelay;
    }

    public void setWaitDelay(long waitDelay) {
        this.waitDelay = waitDelay;
    }
}
