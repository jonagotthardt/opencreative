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

package mcchickenstudio.creative.coding.blocks.conditions.variableconditions.other;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.conditions.variableconditions.VariableCondition;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.variables.VariableLink;
import org.bukkit.entity.Entity;

import java.util.List;

public class VariableExistsCondition extends VariableCondition {
    public VariableExistsCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, boolean isOpposed) {
        super(executor, target, x, args, actions, isOpposed);
    }

    @Override
    public boolean check(Entity entity) {
        List<VariableLink> links = getArguments().getVarLinksList("variables",this);
        boolean requireAll = getArguments().getValue("all",false,this);
        boolean exists = false;
        for (VariableLink link : links) {
            if (getPlot().getVariables().getVariable(link,this) != null) {
                if (!requireAll) {
                    return true;
                }
                exists = true;
            } else {
                if (requireAll) {
                    return false;
                }
            }
        }
        return exists;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_VAR_EXISTS;
    }
}
