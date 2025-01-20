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

package ua.mcchickenstudio.opencreative.coding.variables.values;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.CodingPackContent;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

/**
 * <h1>EventValue</h1>
 * This class represents a value, that can be got
 * from event, actions handler, action and target.
 */
public abstract class EventValueTest implements CodingPackContent {

    private final String id;
    private final ItemStack displayIcon;
    private final MenusCategory category;

    public EventValueTest(String id, ItemStack displayIcon, MenusCategory category) {
        this.id = id;
        this.displayIcon = displayIcon;
        this.category = category;
    }

    /**
     * Returns an icon that will be used
     * in event values menu.
     * @return icon of event value to display.
     */
    public ItemStack getDisplayIcon() {
        return displayIcon;
    }

    /**
     * Returns a category, that event
     * value belongs to.
     * @return category where event value will be able.
     */
    public MenusCategory getCategory() {
        return category;
    }

    /**
     * Returns a value that will be set instead of
     * event value name tag item.
     * @param handler handler of action to get value.
     * @param action action to get value.
     * @return string, number, boolean, location, vector, or null.
     */
    public abstract @Nullable Object getValue(@NotNull ActionsHandler handler, @NotNull Action action);

}
