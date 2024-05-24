package mcchickenstudio.creative.coding.blocks.executors;

import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import mcchickenstudio.creative.coding.blocks.events.EventVariables;
import mcchickenstudio.creative.plots.Plot;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.ErrorUtils.sendPlotCodeErrorMessage;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>Executor</h1>
 * This class represents Executor that has actions to run.
 * Executor will be executed on events in plot.
 * @since 1.5
 * @version 1.5
 * @author McChicken Studio
 */
public abstract class Executor {

    private final Plot PLOT;
    private final int X;
    private final int Y;
    private final int Z;
    private final List<Action> ACTIONS = new ArrayList<>();
    private final EventVariables variables = new EventVariables();
    private CreativeEvent event;

    /**
     * Creates an Executor with specified plot and block's location in developers plot.
     * @param plot Plot where executor will work.
     * @param x X from Executor's block location in developers plot.
     * @param y Y from Executor's block location in developers plot.
     * @param z Z from Executor's block location in developers plot.
     */
    public Executor(Plot plot, int x, int y, int z) {
        this.PLOT = plot;
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    /**
     * Executes all actions with specified event.
     * @param event Event that occurred in plot.
     */
    public void run(CreativeEvent event) {
        setTempVars(event);
        executeActions(event);
    }

    protected void setTempVars(CreativeEvent event) {}
    protected void executeActions(CreativeEvent event) {
        this.event = event;
        for (Action action : ACTIONS) {
            try {
                action.execute(event.getSelection());
            } catch (IndexOutOfBoundsException e) {
                sendPlotCodeErrorMessage(this, action, getLocaleMessage("plot-code-error.arguments"));
            } catch (NumberFormatException e) {
                sendPlotCodeErrorMessage(this, action, getLocaleMessage("plot-code-error.wrong-number"));
            } catch (IllegalArgumentException e) {
                sendPlotCodeErrorMessage(this, action, getLocaleMessage("plot-code-error.wrong-argument") + e.getMessage());
            } catch (Exception e) {
                sendPlotCodeErrorMessage(this, action, getLocaleMessage("plot-code-error.unknown") + e.getMessage());
            }
        }
    }

    protected void setVar(EventVariables.Variable var, Object value) {
        variables.setVariable(var,value);
    }

    public Object getVarValue(EventVariables.Variable var) {
        return variables.getVarValue(var);
    }

    /**
     * Sets actions list for executor.
     * @param actions List of actions.
     */
    public final void setActions(List<Action> actions) {
        this.ACTIONS.clear();
        actions.forEach(this::addAction);
    }

    private void addAction(Action action) {
        ACTIONS.add(action);
    }

    public abstract ExecutorType getExecutorType();
    public abstract ExecutorCategory getExecutorCategory();

    @Override
    public String toString() {
        return "Executor | Plot: " + getPlot().worldName + " Coords: " + X + " " + Y + " " + Z;
    }

    public final int getX() {
        return X;
    }

    public final int getY() {
        return Y;
    }

    public final int getZ() {
        return Z;
    }

    public final Plot getPlot() {
        return PLOT;
    }

    public CreativeEvent getEvent() {
        return event;
    }
}
