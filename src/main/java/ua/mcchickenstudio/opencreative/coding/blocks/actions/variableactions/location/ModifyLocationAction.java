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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.location;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public final class ModifyLocationAction extends VariableAction {
    public ModifyLocationAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        VariableLink link = getArguments().getVariableLink("variable",this);
        Location location = getArguments().getValue("location",entity.getLocation(),this);
        location = location.clone();
        boolean add = getArguments().getValue("add",false,this);
        double x = (add ? 0.0d : location.getX());
        double y = (add ? 0.0d : location.getY());
        double z = (add ? 0.0d : location.getZ());
        float yaw = (add ? 0.0f : location.getYaw());
        float pitch = (add ? 0.0f : location.getPitch());
        if (getArguments().pathExists("x")) {
            x = getArguments().getValue("x",x,this);
        }
        if (getArguments().pathExists("y")) {
            y = getArguments().getValue("y",y,this);
        }
        if (getArguments().pathExists("z")) {
            z = getArguments().getValue("z",z,this);
        }
        if (getArguments().pathExists("yaw")) {
            yaw = getArguments().getValue("yaw",yaw,this);
        }
        if (getArguments().pathExists("pitch")) {
            pitch = getArguments().getValue("pitch",pitch,this);
        }
        if (add) {
            location.setX(location.getX()+x);
            location.setY(location.getY()+y);
            location.setZ(location.getZ()+z);
            location.setYaw(location.getYaw()+yaw);
            location.setPitch(location.getPitch()+pitch);
        } else {
            location.setX(x);
            location.setY(y);
            location.setZ(z);
            location.setYaw(yaw);
            location.setPitch(pitch);
        }
        setVarValue(link,location);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.VAR_MODIFY_LOCATION;
    }
}
