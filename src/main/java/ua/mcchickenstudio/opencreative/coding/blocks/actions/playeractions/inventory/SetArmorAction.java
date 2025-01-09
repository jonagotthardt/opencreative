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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.inventory;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SetArmorAction extends PlayerAction {
    public SetArmorAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(Player player) {
        ItemStack helmet = getArguments().getValue("helmet", ItemStack.empty(),this);
        ItemStack chestplate = getArguments().getValue("chestplate",ItemStack.empty(),this);
        ItemStack leggings = getArguments().getValue("leggings",ItemStack.empty(),this);
        ItemStack boots = getArguments().getValue("boots",ItemStack.empty(),this);
        boolean replaceWithAir = getArguments().getValue("replace-with-air",false,this);
        if (replaceWithAir || !helmet.isEmpty()) {
            player.getInventory().setHelmet(helmet);
        }
        if (replaceWithAir || !chestplate.isEmpty()) {
            player.getInventory().setChestplate(chestplate);
        }
        if (replaceWithAir || !leggings.isEmpty()) {
            player.getInventory().setLeggings(leggings);
        }
        if (replaceWithAir || !boots.isEmpty()) {
            player.getInventory().setBoots(boots);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PLAYER_SET_ARMOR;
    }
}
