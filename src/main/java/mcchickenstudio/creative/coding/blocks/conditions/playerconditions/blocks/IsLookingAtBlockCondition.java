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

import com.destroystokyo.paper.block.TargetBlockInfo;
import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.PlayerCondition;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class IsLookingAtBlockCondition extends PlayerCondition {

    public IsLookingAtBlockCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions) {
        super(executor, target, x, args, actions);
    }

    @Override
    public boolean checkPlayer(Player player) {
        boolean check = false;
        List<ItemStack> blocks = getArguments().getItemList("blocks",this);
        if (blocks.isEmpty()) return false;
        Block block = player.getTargetBlockExact(30);
        if (block == null) {
            return false;
        }
        boolean isPlayerLookingAt = false;
        Material blockType = block.getType();

        for (ItemStack checkBlock : blocks) {
            if (blockType == checkBlock.getType()) {
                isPlayerLookingAt = true;
            }
        }

        if (!isPlayerLookingAt) {
            return false;
        } else {
            check = true;
        }
        return check;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_PLAYER_STANDS_ON_BLOCK;
    }
}
