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

package mcchickenstudio.creative.coding.blocks.conditions.playerconditions.inventory;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.PlayerCondition;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HasItemInHandCondition extends PlayerCondition {

    public HasItemInHandCondition(Executor executor, int x, Arguments args, List<Action> actions) {
        super(executor, x, args, actions);
    }

    @Override
    public boolean check(List<Entity> selection) {
        boolean check = false;
        List<ItemStack> items = getArguments().getItemList("items");
        byte hand = getArguments().getValue("hand",(byte) 1);
        for (Player player : getPlayers(selection)) {

            ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

            boolean hasItemInHand = false;
            for (ItemStack itemStack : items) {
                if (hand == 1) {
                    if (itemInMainHand.equals(itemStack)) {
                        hasItemInHand = true;
                    }
                } else if (hand == 2) {
                    if (itemInOffHand.equals(itemStack)) {
                        hasItemInHand = true;
                    }
                } else if (hand == 3) {
                    if (itemInOffHand.equals(itemStack) && itemInMainHand.equals(itemStack)) {
                        hasItemInHand = true;
                    }
                } else {
                    if (itemInOffHand.equals(itemStack) || itemInMainHand.equals(itemStack)) {
                        hasItemInHand = true;
                    }
                }
            }
            if (!hasItemInHand) {
                return false;
            } else {
                check = true;
            }
        }
        return check;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_PLAYER_HAS_ITEM_IN_HAND;
    }
}
