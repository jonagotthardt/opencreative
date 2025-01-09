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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.worldconditions.blocks;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.worldconditions.WorldCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.List;

public class IsWorldBlockCollidableCondition extends WorldCondition {

    public IsWorldBlockCollidableCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, boolean isOpposed) {
        super(executor, target, x, args, actions, isOpposed);
    }

    @Override
    public boolean check(Entity entity) {
        List<Location> blockLocations = getArguments().getLocationList("blocks",this);
        boolean requireAll = getArguments().getValue("all",true,this);
        boolean isCollidable = false;
        for (Location location : blockLocations) {
            if (getWorld().getBlockAt(location).isCollidable()) {
                if (!requireAll) {
                    return true;
                }
                isCollidable = true;
            } else {
                if (requireAll) {
                    return false;
                }
            }
        }
        return isCollidable;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_WORLD_BLOCK_IS_COLLIDABLE;
    }
}
