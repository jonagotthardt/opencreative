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

package ua.mcchickenstudio.opencreative.coding.menus.blocks;

import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerEventsMenu extends CodingBlockTypesMenu {

    public PlayerEventsMenu(Player player, Location location) {
        super(player, location, "events","event_player");
    }

    @Override
    protected List<Object> getElements() {
        if (currentCategory == null) currentCategory = MenusCategory.WORLD;
        return new ArrayList<>(ExecutorType.getExecutorsByCategories(ExecutorCategory.EVENT_PLAYER,currentCategory));
    }

    @Override
    protected ItemStack getElementIcon(Object object) {
        if (object instanceof ExecutorType type) {
            return type.getIcon();
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    @Override
    protected Set<MenusCategory> getMenusCategories() {
        return ExecutorType.getMenusCategories(ExecutorCategory.EVENT_PLAYER);
    }
}