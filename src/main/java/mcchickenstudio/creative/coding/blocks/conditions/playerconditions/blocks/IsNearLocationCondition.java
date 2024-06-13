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

package mcchickenstudio.creative.coding.blocks.conditions.playerconditions.blocks;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.PlayerCondition;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class IsNearLocationCondition extends PlayerCondition {

    public IsNearLocationCondition(Executor executor, int x, Arguments args, List<Action> actions) {
        super(executor, x, args, actions);
    }

    @Override
    public boolean check(List<Entity> selection) {
        boolean check = false;
        List<Location> locations = getArguments().getLocationList("locations");
        float radius = getArguments().getValue("distance",5.0f);
        if (locations.isEmpty()) return false;
        for (Player player : getPlayers(selection)) {
            boolean isPlayerNear = false;

            for (Location location : locations) {
                if (player.getLocation().distance(location) <= radius) {
                    isPlayerNear = true;
                }
            }

            if (!isPlayerNear) {
                return false;
            } else {
                check = true;
            }
        }
        return check;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_PLAYER_IS_NEAR_LOCATION;
    }
}
