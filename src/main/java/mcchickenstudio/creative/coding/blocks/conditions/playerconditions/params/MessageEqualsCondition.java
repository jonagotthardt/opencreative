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
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.PlayerCondition;
import mcchickenstudio.creative.coding.blocks.events.EventVariables;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.blocks.executors.player.world.ChatExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingNotFoundTempVar;

public class MessageEqualsCondition extends PlayerCondition {
    public MessageEqualsCondition(Executor executor, int x, Arguments args, List<Action> actions) {
        super(executor, x, args, actions);
    }

    @Override
    public boolean check(List<Entity> selection) {
        if (!getExecutor().hasTempVariable(EventVariables.Variable.MESSAGE)) {
            sendCodingNotFoundTempVar(getPlot(),getExecutor(), EventVariables.Variable.MESSAGE);
            return false;
        }
        boolean check = false;
        String originalMessage = getExecutor().getVarValue(EventVariables.Variable.MESSAGE).toString();
        List<String> messages = getArguments().getTextList("messages");
        boolean caps = getArguments().getValue("boolean",false);
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
