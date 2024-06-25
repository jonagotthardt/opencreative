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
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerNameEqualsCondition extends PlayerCondition {

    public PlayerNameEqualsCondition(Executor executor, int x, Arguments args, List<Action> actions) {
        super(executor, x, args, actions);
    }

    @Override
    public boolean check(List<Entity> selection) {
        boolean check = false;
        boolean requiredCaps = getArguments().getValue("require-caps",false);
        List<String> names = getArguments().getTextList("names");
        for (Player player : getPlayers(selection)) {
            boolean isNameEquals = false;
            for (String name : names) {
                if (requiredCaps) {
                    if (player.getName().equals(name)) {
                        isNameEquals = true;
                    }
                } else {
                    if (player.getName().equalsIgnoreCase(name)) {
                        isNameEquals = true;
                    }
                }
            }
            if (!isNameEquals) {
                return false;
            } else {
                check = true;
            }
        }
        return check;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_PLAYER_NAME_EQUALS;
    }
}
