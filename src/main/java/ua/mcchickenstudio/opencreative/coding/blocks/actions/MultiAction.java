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

package ua.mcchickenstudio.opencreative.coding.blocks.actions;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugAction;

/**
 * <h1>MultiAction</h1>
 * This class represents an action, that has actions
 * inside to execute. All multi-actions have piston
 * as additional block.
 * @see ua.mcchickenstudio.opencreative.coding.blocks.conditions.Condition
 * @since 5.0
 * @version 5.0
 * @author McChicken Studio
 */
public abstract class MultiAction extends Action {

    private final List<Action> actions;

    /**
     * Creates MultiAction, that will execute actions inside.
     * @param executor Executor, from which this action will be executed.
     * @param target Target, that will execute action.
     * @param x X Coordinate of coding block in developer's world.
     * @param args Arguments for action.
     * @param actions Actions, that will be executed with this action.
     */
    public MultiAction(Executor executor, Target target, int x, Arguments args, List<Action> actions) {
        super(executor, target, x, args);
        this.actions = actions;
    }

    @Override
    public void prepareAndExecute(ActionsHandler handler) {
        this.handler = handler;
        this.event = getExecutor().getEvent();
        sendCodingDebugAction(this);
        execute(entity);
    }

    public void executeActions() {
        new ActionsHandler(this).executeActions(actions);
    }

    public List<Action> getActions() {
        return actions;
    }
}
