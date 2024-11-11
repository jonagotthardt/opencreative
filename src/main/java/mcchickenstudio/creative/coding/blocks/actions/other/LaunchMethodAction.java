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

package mcchickenstudio.creative.coding.blocks.actions.other;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.blocks.executors.Executors;
import mcchickenstudio.creative.coding.blocks.executors.other.Function;
import org.bukkit.entity.Entity;

public class LaunchMethodAction extends Action {

    public LaunchMethodAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        String name = getArguments().getValue("name","",this);
        if (name.isEmpty()) return;
        for (Executor executor : getPlot().getTerritory().getScript().getExecutors().getExecutorsList()) {
            if (executor instanceof Function function) {
                if (function.getName().equalsIgnoreCase(name)) {
                    Executors.activate(function, getEvent());
                }
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.LAUNCH_FUNCTION;
    }

    @Override
    public ActionCategory getActionCategory() {
        return ActionCategory.LAUNCH_FUNCTION_ACTION;
    }
}
