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

import mcchickenstudio.creative.coding.CreativeRunnable;
import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class SendDialogAction extends PlayerAction {
    public SendDialogAction(Executor executor, int x, Arguments args) {
        super(executor, x, args);
    }

    @Override
    protected void execute(List<Entity> selection) {
        List<Player> players = getPlayers(selection);
        if (players == null) return;
        int cooldown = getArguments().getValue("cooldown",20);
        List<String> text = getArguments().getTextList("messages");
        new CreativeRunnable(getPlot()) {
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
