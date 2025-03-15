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

package ua.mcchickenstudio.opencreative.indev.values;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

public abstract class TextEventValue extends EventValueTest {

    public TextEventValue(String id, ItemStack displayIcon, MenusCategory category) {
        super(id, displayIcon, category);
    }

    /**
     * Returns a string that can be got from
     * player, event, action, or null.
     * @return string, or null.
     */
    public abstract @Nullable String getText(@NotNull ActionsHandler handler, @NotNull Action action);

    @Override
    public @Nullable Object getValue(@NotNull ActionsHandler handler, @NotNull Action action) {
        String text = getText(handler, action);
        if (text == null) return null;
        return new StringBuilder(text).substring(0,Math.min(1024,text.length()));
    }
}
