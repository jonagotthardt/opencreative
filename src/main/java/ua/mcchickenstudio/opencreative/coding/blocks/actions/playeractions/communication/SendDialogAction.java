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

import ua.mcchickenstudio.opencreative.coding.CreativeRunnable;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class SendDialogAction extends PlayerAction {
    public SendDialogAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(Player player) {
        List<Player> players = new ArrayList<>(List.of(player));
        int cooldown = getArguments().getValue("cooldown",20,this);
        List<String> text = getArguments().getTextList("messages",this);
        new CreativeRunnable(getPlanet()) {
            byte current = 0;
            @Override
            public void execute(Player player) {
                if (current == text.size()) {
                    cancel();
                } else {
                    String message = text.get(current);
                    player.sendMessage(message);
                    current++;
                }
            }
        }.runTaskTimer(players,0,cooldown);
    }


    @Override
    public ActionType getActionType() {
        return ActionType.PLAYER_SEND_DIALOG;
    }
}
