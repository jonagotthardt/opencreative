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

package mcchickenstudio.creative.coding.blocks.actions.variableactions.list;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.VariableAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.variables.VariableLink;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CreateListAction extends VariableAction {
    public CreateListAction(Executor executor, int x, Arguments args) {
        super(executor, x, args);
    }

    @Override
    protected void execute(List<Entity> selection) {
        VariableLink variable = getArguments().getVariableLink("variable");
        boolean saveAsItems = getArguments().getValue("save-as-items",false);
        List<ItemStack> itemsList = getArguments().getItemList("elements");
        List<Object> elements = new ArrayList<>();
        if (!saveAsItems) {
            for (ItemStack item : itemsList) {
                elements.add(parseItemValue(item));
            }
        } else {
            elements.addAll(itemsList);
        }
        setVarValue(variable, elements);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.VAR_CREATE_LIST;
    }
}
