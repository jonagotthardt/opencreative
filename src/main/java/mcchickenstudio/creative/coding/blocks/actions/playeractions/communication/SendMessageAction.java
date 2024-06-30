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

package mcchickenstudio.creative.coding.blocks.actions.playeractions.communication;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;

import java.util.List;

public class SendMessageAction extends PlayerAction {

    public SendMessageAction(Executor executor, int x, Arguments args) {
        super(executor, x, args);
    }

    @Override
    public void execute(List<Entity> selection) {
        String type = getArguments().getValue("type","new-line");
        List<String> messages = getArguments().getTextList("messages");
        for (Entity entity : selection) {
            if (type.equals("new-line")) {
                for (String message : messages) {
                    entity.sendMessage(message);
                }
            } else if (type.equals("join-spaces")) {
                entity.sendMessage(String.join(" ",messages));
            } else {
                entity.sendMessage(String.join("",messages));
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PLAYER_SEND_MESSAGE;
    }
}
