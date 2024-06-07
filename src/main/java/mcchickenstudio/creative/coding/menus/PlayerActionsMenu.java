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

package mcchickenstudio.creative.coding.menus;

import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerActionsMenu extends CodingBlockTypesMenu {

    public PlayerActionsMenu(Player player, Location location) {
        super(player, location, "actions","action_player");
    }

    @Override
    protected List<Object> getElements() {
        if (currentCategory == null) currentCategory = MenusCategory.COMMUNICATION;
        return new ArrayList<>(ActionType.getActionsByCategories(ActionCategory.PLAYER_ACTION,currentCategory));
    }

    @Override
    protected ItemStack getElementIcon(Object object) {
        if (object instanceof ActionType) {
            ActionType type = (ActionType) object;
            return type.getIcon();
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    @Override
    protected Set<MenusCategory> getMenusCategories() {
        return ActionType.getMenusCategories(ActionCategory.PLAYER_ACTION);
    }
}