/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2025, McChicken Studio, mcchickenstudio@gmail.com
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
import org.bukkit.entity.Entity;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Method;

public final class LaunchMethodAction extends Action {

    public LaunchMethodAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        String name = getArguments().getValue("name","",this);
        if (name.isEmpty()) return;
        for (Executor executor : getPlanet().getTerritory().getScript().getExecutors().getExecutorsList()) {
            if (executor instanceof Method method) {
                if (method.getName().equalsIgnoreCase(name)) {
                    Executors.activate(method, getEvent());
                }
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.LAUNCH_METHOD;
    }

    @Override
    public ActionCategory getActionCategory() {
        return ActionCategory.LAUNCH_METHOD_ACTION;
    }
}
