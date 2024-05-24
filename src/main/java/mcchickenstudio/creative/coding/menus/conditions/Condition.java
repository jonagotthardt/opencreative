/*
package mcchickenstudio.creative.coding.menus.conditions;

import mcchickenstudio.creative.coding.menus.actions.Action;
import mcchickenstudio.creative.coding.menus.executors.Executor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.List;

public abstract class Condition extends Action {

    private final List<String> arguments;
    private final int parameter;
    private final Location location;
    private final boolean isOpposed;
    private final List<Action> actions;

    public Condition(Location location, List<String> arguments, int parameter, boolean isOpposed, List<Action> actions) {
        super(location,arguments,parameter);
        this.arguments = arguments;
        this.parameter = parameter;
        this.location = location;
        this.isOpposed = isOpposed;
        this.actions = actions;
    }

    public Location getLocation() {
        return location;
    }

    public int getParameter() {
        return parameter;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public boolean isOpposed() {
        return isOpposed;
    }

    public List<Action> getActions() {
        return actions;
    }

    public abstract void execute(Executor executor, Entity entity);

}
*/
