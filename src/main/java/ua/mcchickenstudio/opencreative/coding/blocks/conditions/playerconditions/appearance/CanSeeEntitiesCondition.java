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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.appearance;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.PlayerCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class CanSeeEntitiesCondition extends PlayerCondition {
    /**
     * Creates an Condition with linked executor and specified arguments.
     *
     * @param executor Executor where this action will be added.
     * @param x        X from Action's block location in developers planet.
     * @param args     List of arguments for action.
     */
    public CanSeeEntitiesCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, boolean isOpposed) {
        super(executor, target, x, args, actions, isOpposed);
    }

    @Override
    public boolean checkPlayer(Player player) {
        List<String> names = getArguments().getTextList("players",this);
        boolean requireAll = getArguments().getValue("all",false,this);
        boolean canSee = false;
        for (String name : names) {
            for (Entity entity : getEntitiesByNameOrUUID(name)) {
                if (entity instanceof Player) {
                    continue;
                }
                if (player.canSee(entity)) {
                    if (!requireAll) {
                        return true;
                    }
                    canSee = true;
                } else {
                    if (requireAll) {
                        return false;
                    }
                }
            }
        }
        return canSee;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_PLAYER_CAN_SEE_ENTITY;
    }
}
