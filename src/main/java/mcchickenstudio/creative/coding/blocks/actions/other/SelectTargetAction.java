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
import mcchickenstudio.creative.coding.blocks.conditions.Condition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.PlayerCondition;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.utils.ErrorUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SelectTargetAction extends Action {

    private final TargetAction targetAction;

    private final Target target;

    private final ActionCategory conditionCategory;
    private final ActionType conditionType;
    private final boolean isOpposed;

    public enum TargetAction {
        ADD_TARGET,
        REMOVE_TARGET,
        SET_TARGET,
    }

    public SelectTargetAction(Executor executor, int x, Arguments args, TargetAction targetAction, ActionCategory condition, ActionType conditionType, boolean isOpposed) {
        super(executor, Target.DEFAULT, x, args);
        this.target = null;
        this.isOpposed = isOpposed;
        this.targetAction = targetAction;
        this.conditionCategory = condition;
        this.conditionType = conditionType;
    }

    public SelectTargetAction(Executor executor, int x, Arguments args, TargetAction targetAction, Target target) {
        super(executor, Target.DEFAULT, x, args);
        this.target = target;
        this.targetAction = targetAction;
        this.isOpposed = false;
        this.conditionCategory = null;
        this.conditionType = null;
    }

    @Override
    protected void execute(Entity entity) {
        List<Entity> entities = new ArrayList<>();
        if (conditionCategory != null && conditionType != null) {
            if (!conditionCategory.isCondition()) {
                return;
            }
            try {
                Action action = conditionType.getActionClass().getConstructor(Executor.class, Target.class, int.class,Arguments.class,List.class,boolean.class).newInstance(getExecutor(),target,getX(),getArguments(),new ArrayList<>(),isOpposed);
                if (action instanceof PlayerCondition playerCondition) {
                    for (Player player : getPlot().world.getPlayers()) {
                        if (playerCondition.checkPlayer(player) ^ isOpposed) {
                            entities.add(player);
                        }
                    }
                } else if (action instanceof Condition condition) {
                    for (Entity checkEntity : getPlot().world.getEntities()) {
                        if (condition.check(checkEntity) ^ isOpposed) {
                            entities.add(checkEntity);
                        }
                    }
                }
            } catch (Exception e) {
                ErrorUtils.sendPlotCodeErrorMessage(getExecutor(),this, "Failed to execute select target action",e);
            }
        }
        switch (targetAction) {
            case ADD_TARGET -> getHandler().getMainActionHandler().getSelectedTargets().addAll(entities);
            case REMOVE_TARGET -> getHandler().getMainActionHandler().getSelectedTargets().forEach(entities::remove);
            case SET_TARGET -> {
                getHandler().getMainActionHandler().getSelectedTargets().clear();
                getHandler().getMainActionHandler().getSelectedTargets().addAll(entities);
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.LAUNCH_FUNCTION;
    }

    @Override
    public ActionCategory getActionCategory() {
        return ActionCategory.SELECTION_ACTION;
    }
}
