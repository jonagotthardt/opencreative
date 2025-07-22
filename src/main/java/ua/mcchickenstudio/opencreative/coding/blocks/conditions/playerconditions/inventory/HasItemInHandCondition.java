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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.inventory;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.PlayerCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HasItemInHandCondition extends PlayerCondition {

    public HasItemInHandCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean checkPlayer(Player player) {
        List<ItemStack> items = getArguments().getItemList("items",this);
        if (items.isEmpty()) return false;

        String hand = getArguments().getValue("hand","main-hand",this);
        boolean ignoreAmount = getArguments().getValue("ignore-amount",true,this);
        boolean ignoreName = getArguments().getValue("ignore-name",false,this);
        boolean ignoreLore = getArguments().getValue("ignore-lore",false,this);
        boolean ignoreEnchantments = getArguments().getValue("ignore-enchantments",false,this);
        boolean ignoreFlags = getArguments().getValue("ignore-flags",false,this);
        boolean ignoreMaterial = getArguments().getValue("ignore-material",false,this);
        boolean ignoreDamage = getArguments().getValue("ignore-damage",false,this);

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        itemInMainHand = ItemUtils.getItemWithIgnoreData(itemInMainHand,ignoreAmount,ignoreName,ignoreLore,ignoreFlags,ignoreEnchantments,ignoreMaterial, ignoreDamage);
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
        itemInOffHand = ItemUtils.getItemWithIgnoreData(itemInOffHand,ignoreAmount,ignoreName,ignoreLore,ignoreFlags,ignoreEnchantments,ignoreMaterial, ignoreDamage);
        for (ItemStack checkItem : items) {
            checkItem = ItemUtils.getItemWithIgnoreData(checkItem, ignoreAmount, ignoreName, ignoreLore, ignoreFlags, ignoreEnchantments, ignoreMaterial, ignoreDamage);
            if (hand.equals("main-hand")) {
                return itemInMainHand.equals(checkItem);
            } else if (hand.equals("off-hand")) {
                return itemInOffHand.equals(checkItem);
            } else {
                return (hand.equals("main-or-off-hands") ? itemInMainHand.equals(checkItem) || itemInOffHand.equals(checkItem) : itemInMainHand.equals(checkItem) && itemInOffHand.equals(checkItem));
            }
        }

        return false;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_PLAYER_HAS_ITEM_IN_HAND;
    }
}
