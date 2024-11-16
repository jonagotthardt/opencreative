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
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.VariableAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.exceptions.CollectionWithCollectionException;
import mcchickenstudio.creative.coding.variables.VariableLink;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AddToListAction extends VariableAction {
    public AddToListAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        VariableLink variable = getArguments().getVariableLink("variable",this);
        List<Object> list = new ArrayList<>(getArguments().getList("variable",this));
        List<Object> elements = getArguments().getList("elements",this);
        for (Object element : elements) {
            if (element instanceof Collection<?> || element instanceof Map<?,?>) {
                throw new CollectionWithCollectionException(list.getClass(),element.getClass());
            }
            list.add(element);
        }
        setVarValue(variable, list);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.VAR_ADD_TO_LIST;
    }
}
