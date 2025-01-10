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

package ua.mcchickenstudio.opencreative.coding.menus.blocks;

import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldConditionsMenu extends CodingBlockTypesMenu {

    public WorldConditionsMenu(Player player, Location location) {
        super(player, location, "conditions","world_condition");
    }

    @Override
    protected List<Object> getElements() {
        if (currentCategory == null) currentCategory = MenusCategory.BLOCKS;
        return new ArrayList<>(ActionType.getActionsByCategories(ActionCategory.WORLD_CONDITION,currentCategory));
    }

    @Override
    protected ItemStack getElementIcon(Object object) {
        if (object instanceof ActionType type) {
            return type.getIcon();
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    @Override
    protected Set<MenusCategory> getMenusCategories() {
        return ActionType.getMenusCategories(ActionCategory.WORLD_CONDITION);
    }
}