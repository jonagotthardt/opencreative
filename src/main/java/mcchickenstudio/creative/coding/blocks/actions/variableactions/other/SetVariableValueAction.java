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

package mcchickenstudio.creative.coding.blocks.actions.variableactions.other;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.VariableAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.variables.VariableLink;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SetVariableValueAction extends VariableAction {
    public SetVariableValueAction(Executor executor, int x, Arguments args) {
        super(executor, x, args);
    }

    @Override
    protected void execute(List<Entity> selection) {
        VariableLink link = getArguments().getVariableLink("variable");
        ItemStack item = getArguments().getValue("value", ItemStack.empty());
        if (item == null || item.equals(ItemStack.empty())) {
            return;
        }
        Object value = parseItemValue(item);
        setVarValue(link,value);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.VAR_SET_VALUE;
    }
}
