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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VariableActionsMenu extends CodingBlockTypesMenu {

    public VariableActionsMenu(Player player, Location location) {
        super(player, location, "actions","variable_action");
        previousPageButtonSlot = 27;
        nextPageButtonSlot = 35;
        noElementsPageButtonSlot = 13;
        charmsBarSlots = new byte[]{45, 46, 47, 48, 49, 50, 51, 52, 53};
        decorationSlots = new byte[]{36, 37, 38, 39, 40, 41, 42, 43, 44};
        itemsSlots = new byte[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 35};
    }

    @Override
    protected List<Object> getElements() {
        if (currentCategory == null) currentCategory = MenusCategory.OTHER;
        return new ArrayList<>(ActionType.getActionsByCategories(ActionCategory.VARIABLE_ACTION,currentCategory));
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
        return ActionType.getMenusCategories(ActionCategory.VARIABLE_ACTION);
    }
}