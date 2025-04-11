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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.menus.BlockMenu;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>BlocksCategorySelectionMenu</h1>
 * This class represents a menu for selecting menus category
 * and opening a coding block type selection menu.
 */
public class BlocksCategorySelectionMenu extends MenusCategorySelectionMenu implements BlockMenu {

    private final ExecutorCategory executorCategory;
    private final ActionCategory actionCategory;
    private final Location signLocation;

    public BlocksCategorySelectionMenu(@NotNull Player player,
                                       @NotNull Location location,
                                       @NotNull ExecutorCategory category) {
        super(player,category.getItem(),
                category.getStainedPane(),
                ExecutorType.getMenusCategories(category),
                ChatColor.stripColor(getLocaleMessage("blocks." + category.name().toLowerCase())),
                "events");
        this.signLocation = location;
        this.executorCategory = category;
        this.actionCategory = null;
    }

    public BlocksCategorySelectionMenu(@NotNull Player player,
                                       @NotNull Location location,
                                       @NotNull ActionCategory category) {
        super(player,category.getItem(),
                category.getStainedPane(),
                ActionType.getMenusCategories(category),
                ChatColor.stripColor(getLocaleMessage("blocks." + category.name().toLowerCase())),
                category.isCondition() ? "conditions" : "actions");
        this.signLocation = location;
        this.executorCategory = null;
        this.actionCategory = category;
    }

    @Override
    public @NotNull ContentWithMenusCategoryMenu<?> getContentBrowserMenu() {
        BlocksWithMenusCategoryMenu<?> content;
        if (mainCategory.equalsIgnoreCase("events")) {
            content = new ExecutorTypeSelectionMenu(player, signLocation, executorCategory);
        } else {
            content = new ActionTypeSelectionMenu(player, signLocation, actionCategory);
        }
        content.setSignLocation(signLocation);
        return content;
    }

    @Override
    public @Nullable BlockState getBlockState() {
        return signLocation.getBlock().getState();
    }

    @Override
    public @Nullable Location getLocation() {
        return signLocation;
    }
}
