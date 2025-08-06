package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.location;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

public class GetLocationAllCoordinateAction extends VariableAction {
    public GetLocationAllCoordinateAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        Location location = getArguments().getValue("location",entity.getLocation(),this);

        VariableLink x = getArguments().getVariableLink("x",this);
        VariableLink y = getArguments().getVariableLink("y",this);
        VariableLink z = getArguments().getVariableLink("z",this);
        VariableLink pitch = getArguments().getVariableLink("pitch",this);
        VariableLink yaw = getArguments().getVariableLink("yaw",this);

        if (getArguments().pathExists("location")) {
            if (x != null) setVarValue(x, location.getX());
            if (y != null) setVarValue(y, location.getY());
            if (z != null) setVarValue(z, location.getZ());
            if (pitch != null) setVarValue(pitch, location.getPitch());
            if (yaw != null) setVarValue(yaw, location.getYaw());
        } else {
            if (getArguments().pathExists("x") || getArguments().pathExists("y") || getArguments().pathExists("z") || getArguments().pathExists("pitch") || getArguments().pathExists("yaw")) {
                throw new IllegalArgumentException("Not found location");
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.VAR_GET_LOCATION_ALL;
    }
}
