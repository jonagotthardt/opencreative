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

import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionsHandler;
import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import mcchickenstudio.creative.coding.blocks.events.EventValues;
import mcchickenstudio.creative.plots.Plot;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingDebugExecutor;

/**
 * <h1>Executor</h1>
 * This class represents Executor that has actions to run.
 * Executor will be executed on events in plot.
 * @since 5.0
 * @version 5.0
 * @author McChicken Studio
 */
public abstract class Executor {

    private final Plot plot;
    private final int x;
    private final int y;
    private final int z;
    private final List<Action> actions = new ArrayList<>();
    private final EventValues variables = new EventValues();
    private CreativeEvent event;
    private ActionsHandler handler;

    /**
     * Creates an Executor with specified plot and block's location in developers plot.
     * @param plot Plot where executor will work.
     * @param x X from Executor's block location in developers plot.
     * @param y Y from Executor's block location in developers plot.
     * @param z Z from Executor's block location in developers plot.
     */
    public Executor(Plot plot, int x, int y, int z) {
        this.plot = plot;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Executes all actions with specified event.
     * @param event Event that occurred in plot.
     */
    public void run(CreativeEvent event) {
        sendCodingDebugExecutor(this);
        setTempVars(event);
        executeActions(event);
    }

    protected void setTempVars(CreativeEvent event) {}
    protected void executeActions(CreativeEvent event) {
        this.event = event;
        handler = new ActionsHandler(this);
        handler.executeActions(actions);
        variables.clear();
    }

    public void setTempVar(EventValues.Variable var, Object value) {
        variables.setVariable(var,value);
    }

    public Object getVarValue(EventValues.Variable var) {
        return variables.getVarValue(var);
    }

    /**
     * Sets actions list for executor.
     * @param actions List of actions.
     */
    public final void setActions(List<Action> actions) {
        this.actions.clear();
        actions.forEach(this::addAction);
    }

    private void addAction(Action action) {
        actions.add(action);
    }

    public abstract ExecutorType getExecutorType();
    public abstract ExecutorCategory getExecutorCategory();

    @Override
    public String toString() {
        return "Executor | Plot: " + getPlot().worldName + " Coords: " + x + " " + y + " " + z;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final int getZ() {
        return z;
    }

    public final Plot getPlot() {
        return plot;
    }

    public EventValues getVariables() {
        return variables;
    }

    public CreativeEvent getEvent() {
        return event;
    }

    public ActionsHandler getHandler() {
        return handler;
    }
}
