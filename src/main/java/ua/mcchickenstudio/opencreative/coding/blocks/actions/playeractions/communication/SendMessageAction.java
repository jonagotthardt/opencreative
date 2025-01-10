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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.communication;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.List;

public final class SendMessageAction extends PlayerAction {

    public SendMessageAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(Player player) {
        String type = getArguments().getValue("type","new-line",this);
        List<String> messages = getArguments().getTextList("messages",this);
        String message;
        if (type.equals("new-line")) {
            message = String.join("\n",messages);
        } else if (type.equals("join-spaces")) {
            message = String.join(" ",messages);
        } else {
            message = String.join("",messages);
        }
        if (message.length() > 1024) {
            throw new RuntimeException("Can't send message with length above 1024 symbols.");
        }
        if (message.contains("§")) {
            player.sendMessage(message);
        } else {
            Component miniMessage = MiniMessage.miniMessage().deserialize(message);
            ClickEvent clickEvent = miniMessage.clickEvent();
            if (clickEvent != null && clickEvent.action() == ClickEvent.Action.RUN_COMMAND) {
                miniMessage = miniMessage.clickEvent(null);
            }
            player.sendMessage(miniMessage);
        }
    }


    @Override
    public ActionType getActionType() {
        return ActionType.PLAYER_SEND_MESSAGE;
    }
}
