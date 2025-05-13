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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.planets.PlanetRunnable;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;

public abstract class VariableAction extends Action {

    public VariableAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public ActionCategory getActionCategory() {
        return ActionCategory.VARIABLE_ACTION;
    }

    public boolean cannotChangeListElements(int size) {
        if (size + getPlanet().getLimits().getLastVariableElementsChangesAmount() > getPlanet().getLimits().getVariableElementsChangesLimit()) {
            return true;
        }
        return false;
    }

    public void changeListElementsChangesAmount(int size) {
        getPlanet().getLimits().setLastVariableElementsChangesAmount(getPlanet().getLimits().getLastVariableElementsChangesAmount()+size);
        getPlanet().getTerritory().scheduleAsyncRunnable(new PlanetRunnable(getPlanet()) {
            @Override
            public void execute() {
                getPlanet().getLimits().setLastVariableElementsChangesAmount(0);
            }
        },20L);
    }
}
