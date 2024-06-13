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

package mcchickenstudio.creative.coding.blocks.conditions;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.actions.ActionHandler;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;

import java.util.List;

public abstract class Condition extends Action {

    private final List<Action> actions;
    private List<Action> reactions;
    private final boolean isOpposed = false;

    /**
     * Creates an Condition with linked executor and specified arguments.
     *
     * @param executor Executor where this action will be added.
     * @param x        X from Action's block location in developers plot.
     * @param args     List of arguments for action.
     */
    public Condition(Executor executor, int x, Arguments args, List<Action> actions) {
        super(executor, x, args);
        this.actions = actions;
    }

    public abstract boolean check(List<Entity> selection);

    @Override
    public void execute(List<Entity> selection) {
        if (check(selection) && !isOpposed) {
            new ActionHandler(getExecutor()).executeActions(actions);
        }
    }

    @Override
    public ActionCategory getActionCategory() {
        return ActionCategory.PLAYER_CONDITION;
    }

    public List<Action> getActions() {
        return actions;
    }
}
