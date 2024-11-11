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
import java.util.Set;

public abstract class SelectionAction extends Action {

    private final Target target;

    private final ActionCategory conditionCategory;
    private final ActionType conditionType;
    private final boolean isOpposed;

    public SelectionAction(Executor executor, int x, Arguments args, ActionCategory condition, ActionType conditionType, boolean isOpposed) {
        super(executor, Target.DEFAULT, x, args);
        this.target = null;
        this.isOpposed = isOpposed;
        this.conditionCategory = condition;
        this.conditionType = conditionType;
    }

    public SelectionAction(Executor executor, int x, Arguments args, Target target) {
        super(executor, target, x, args);
        this.target = target;
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
                    playerCondition.setHandler(this.getHandler());
                    for (Player player : getPlot().getTerritory().getWorld().getPlayers()) {
                        playerCondition.setEntity(player);
                        if (playerCondition.checkPlayer(player) ^ isOpposed) {
                            entities.add(player);
                        }
                    }
                } else if (action instanceof Condition condition) {
                    condition.setHandler(this.getHandler());
                    for (Entity checkEntity : getPlot().getTerritory().getWorld().getEntities()) {
                        condition.setEntity(checkEntity);
                        if (condition.check(checkEntity) ^ isOpposed) {
                            entities.add(checkEntity);
                        }
                    }
                }
            } catch (Exception e) {
                ErrorUtils.sendPlotCodeErrorMessage(getExecutor(),this, "Failed to execute select target action",e);
            }
        } else if (target != null) {
            entities.addAll(getTargets());
        }
        modifyTargets(entities, getHandler().getSelectedTargets());
    }

    protected abstract void modifyTargets(List<Entity> newTarget, Set<Entity> currentTarget);

    @Override
    public ActionCategory getActionCategory() {
        return ActionCategory.SELECTION_ACTION;
    }
}
