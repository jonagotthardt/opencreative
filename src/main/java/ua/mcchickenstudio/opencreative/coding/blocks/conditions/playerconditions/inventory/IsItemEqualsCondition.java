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
import ua.mcchickenstudio.opencreative.coding.values.events.EventItemValue;
import ua.mcchickenstudio.opencreative.utils.ErrorUtils;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class IsItemEqualsCondition extends PlayerCondition {

    public IsItemEqualsCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean checkPlayer(Player player) {
        if (!(getEventValue(EventItemValue.class) instanceof ItemStack eventItem)) {
            ErrorUtils.sendCodingNotFoundEventValue(getPlanet(),getExecutor(), EventItemValue.class);
            return false;
        }

        boolean ignoreAmount = getArguments().getValue("ignore-amount",true,this);
        boolean ignoreName = getArguments().getValue("ignore-name",false,this);
        boolean ignoreLore = getArguments().getValue("ignore-lore",false,this);
        boolean ignoreEnchantments = getArguments().getValue("ignore-enchantments",false,this);
        boolean ignoreFlags = getArguments().getValue("ignore-flags",false,this);
        boolean ignoreMaterial = getArguments().getValue("ignore-material",false,this);
        boolean ignoreDamage = getArguments().getValue("ignore-damage",false,this);

        boolean check = false;
        List<ItemStack> items = getArguments().getItemList("items",this);
        if (items.isEmpty()) return false;

        eventItem = ItemUtils.getItemWithIgnoreData(eventItem,ignoreAmount,ignoreName,ignoreLore,ignoreFlags,ignoreEnchantments,ignoreMaterial,ignoreDamage);
        for (ItemStack checkItem : items) {
            checkItem = ItemUtils.getItemWithIgnoreData(checkItem,ignoreAmount,ignoreName,ignoreLore,ignoreFlags,ignoreEnchantments,ignoreMaterial,ignoreDamage);
            if (eventItem.equals(checkItem)) {
                check = true;
            }
        }
        return check;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_PLAYER_ITEM_EQUALS;
    }
}
