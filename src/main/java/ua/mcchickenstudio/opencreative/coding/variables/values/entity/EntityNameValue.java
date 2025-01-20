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

package ua.mcchickenstudio.opencreative.coding.variables.values.entity;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.variables.values.TextEventValue;

public class EntityNameValue extends TextEventValue {

    public EntityNameValue() {
        super("nickname", new ItemStack(Material.NAME_TAG), MenusCategory.ENTITY);
    }

    @Override
    public @Nullable String getText(@NotNull ActionsHandler handler, @NotNull Action action) {
        return action.getEntity() != null ? action.getEntity().getName() : null;
    }

    @Override
    public String getCodingPackId() {
        return "default";
    }

    @Override
    public String getName() {
        return "Entity's name";
    }

    @Override
    public String getDescription() {
        return "Returns an entity's name";
    }
}
