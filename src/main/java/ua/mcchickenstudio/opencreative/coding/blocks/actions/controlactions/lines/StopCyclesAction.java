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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.controlactions.lines;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.controlactions.ControlAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Cycle;
import org.bukkit.entity.Entity;

import java.util.List;

public class StopCyclesAction extends ControlAction {

    public StopCyclesAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        List<String> list = getArguments().getTextList("names",this);
        for (String name : list) {
            for (Executor executor : getPlot().getTerritory().getScript().getExecutors().getExecutorsList()) {
                if (executor instanceof Cycle cycle) {
                    if (cycle.getName().equalsIgnoreCase(name)) {
                        cycle.stop();
                    }
                }
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.CONTROL_STOP_CYCLES;
    }
}
