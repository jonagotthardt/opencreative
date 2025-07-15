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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.blocks;

import org.bukkit.Material;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.PlayerCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingNotFoundTempVar;

public class IsBlockEqualsCondition extends PlayerCondition {

    public IsBlockEqualsCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean checkPlayer(Player player) {
        if (getHandler().hasTempVariable(EventValues.Variable.BLOCK_MATERIAL)) {
            sendCodingNotFoundTempVar(getPlanet(),getExecutor(), EventValues.Variable.BLOCK_MATERIAL);
            return false;
        }
        if (!(getHandler().getVarValue(EventValues.Variable.BLOCK_MATERIAL) instanceof Material material)) {
            return false;
        }
        List<ItemStack> blocks = getArguments().getItemList("blocks",this);
        if (blocks.isEmpty()) return false;
        for (ItemStack checkBlock : blocks) {
            if (material == checkBlock.getType()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_PLAYER_BLOCK_EQUALS;
    }
}
