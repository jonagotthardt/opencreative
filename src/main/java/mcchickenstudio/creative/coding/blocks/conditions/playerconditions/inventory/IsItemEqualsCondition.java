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
import mcchickenstudio.creative.coding.blocks.events.EventVariables;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.utils.ItemUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingNotFoundTempVar;

public class IsItemEqualsCondition extends PlayerCondition {

    public IsItemEqualsCondition(Executor executor, int x, Arguments args, List<Action> actions) {
        super(executor, x, args, actions);
    }

    @Override
    public boolean check(List<Entity> selection) {
        if (!getExecutor().hasTempVariable(EventVariables.Variable.ITEM)) {
            sendCodingNotFoundTempVar(getPlot(),getExecutor(), EventVariables.Variable.ITEM);
            return false;
        }
        boolean ignoreAmount = getArguments().getValue("ignore-amount",true);
        boolean ignoreName = getArguments().getValue("ignore-name",false);
        boolean ignoreLore = getArguments().getValue("ignore-lore",false);
        boolean ignoreEnchantments = getArguments().getValue("ignore-enchantments",false);
        boolean ignoreFlags = getArguments().getValue("ignore-flags",false);
        boolean ignoreMaterial = getArguments().getValue("ignore-material",false);

        boolean check = false;
        List<ItemStack> items = getArguments().getItemList("items");
        if (items.isEmpty()) return false;

        ItemStack eventItem = (ItemStack) getExecutor().getVarValue(EventVariables.Variable.ITEM);
        eventItem = ItemUtils.getItemWithIgnoreData(eventItem,ignoreAmount,ignoreName,ignoreLore,ignoreFlags,ignoreEnchantments,ignoreMaterial);
        for (ItemStack checkItem : items) {
            checkItem = ItemUtils.getItemWithIgnoreData(checkItem,ignoreAmount,ignoreName,ignoreLore,ignoreFlags,ignoreEnchantments,ignoreMaterial);
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
