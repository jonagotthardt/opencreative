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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.other;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executors;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Function;
import org.bukkit.entity.Entity;

public class LaunchFunctionAction extends Action {

    public LaunchFunctionAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        String name = getArguments().getValue("name","",this);
        if (name.isEmpty()) return;
        for (Executor executor : getPlanet().getTerritory().getScript().getExecutors().getExecutorsList()) {
            if (executor instanceof Function function) {
                if (function.getName().equalsIgnoreCase(name)) {
                    getHandler().addActions(function.getActions());
                    Executors.simulateIncreaseCall(function);
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
