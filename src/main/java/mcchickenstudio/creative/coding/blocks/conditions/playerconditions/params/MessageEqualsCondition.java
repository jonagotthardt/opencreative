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

package mcchickenstudio.creative.coding.blocks.conditions.playerconditions.params;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.PlayerCondition;
import mcchickenstudio.creative.coding.blocks.events.EventValues;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Player;

import java.util.List;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingNotFoundTempVar;

public class MessageEqualsCondition extends PlayerCondition {
    public MessageEqualsCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions) {
        super(executor, target, x, args, actions);
    }

    @Override
    public boolean checkPlayer(Player player) {
        if (!getHandler().hasTempVariable(EventValues.Variable.MESSAGE)) {
            sendCodingNotFoundTempVar(getPlot(),getExecutor(), EventValues.Variable.MESSAGE);
            return false;
        }
        boolean check = false;
        String originalMessage = getHandler().getVarValue(EventValues.Variable.MESSAGE).toString();
        List<String> messages = getArguments().getTextList("messages",this);
        boolean caps = getArguments().getValue("require-caps",false,this);
        for (String message : messages) {
            if (caps) {
                if (message.equals(originalMessage)) {
                    return true;
                }
            } else {
                if (message.equalsIgnoreCase(originalMessage)) {
                    return true;
                }
            }
        }
        return check;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_PLAYER_MESSAGE_EQUALS;
    }
}
