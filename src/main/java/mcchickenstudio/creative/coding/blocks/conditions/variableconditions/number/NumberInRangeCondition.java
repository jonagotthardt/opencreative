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

package mcchickenstudio.creative.coding.blocks.conditions.variableconditions.number;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.conditions.variableconditions.VariableCondition;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;

import java.util.List;

public class NumberInRangeCondition extends VariableCondition {
    public NumberInRangeCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions) {
        super(executor, target, x, args, actions);
    }

    @Override
    public boolean check(Entity entity) {
        double min = getArguments().getValue("min",0.0d,this);
        boolean minEquals = getArguments().getValue("min-equals",false,this);
        double number = getArguments().getValue("number",5.0d,this);
        double max = getArguments().getValue("max",10.0d,this);
        boolean maxEquals = getArguments().getValue("max-equals",false,this);

        boolean minCondition = minEquals ? number >= min : number > min;
        boolean maxCondition = maxEquals ? number <= max : number < max;

        return minCondition && maxCondition;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_VAR_NUMBER_IN_RANGE;
    }
}
