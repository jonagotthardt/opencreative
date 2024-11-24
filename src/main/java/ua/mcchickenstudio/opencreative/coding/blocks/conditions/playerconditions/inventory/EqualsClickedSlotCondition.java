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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.inventory;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.PlayerCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.entity.Player;

import java.util.List;

public class EqualsClickedSlotCondition extends PlayerCondition {

    public EqualsClickedSlotCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, boolean isOpposed) {
        super(executor, target, x, args, actions, isOpposed);
    }

    @Override
    public boolean checkPlayer(Player player) {
        if (getHandler().hasTempVariable(EventValues.Variable.CLICKED_SLOT)) {
            return false;
        }
        int clickedSlot = (int) getHandler().getVarValue(EventValues.Variable.CLICKED_SLOT);
        List<Double> slots = getArguments().getNumbersList("slots",this);
        for (double slot : slots) {
            if (clickedSlot == slot) return true;
        }
        return false;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_PLAYER_CLICKED_SLOT_EQUALS;
    }
}
