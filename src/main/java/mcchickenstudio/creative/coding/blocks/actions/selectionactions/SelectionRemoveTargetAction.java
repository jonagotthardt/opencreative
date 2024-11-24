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

package mcchickenstudio.creative.coding.blocks.actions.selectionactions;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Set;

public class SelectionRemoveTargetAction extends SelectionAction {

    public SelectionRemoveTargetAction(Executor executor, int x, Arguments args, ActionCategory condition, ActionType conditionType, boolean isOpposed) {
        super(executor, x, args, condition, conditionType, isOpposed);
    }

    public SelectionRemoveTargetAction(Executor executor, int x, Arguments args, Target target) {
        super(executor, x, args, target);
    }

    @Override
    protected void modifyTargets(List<Entity> newTarget, Set<Entity> currentTarget) {
        for (Entity target : newTarget) {
            currentTarget.remove(target);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SELECTION_REMOVE;
    }
}
