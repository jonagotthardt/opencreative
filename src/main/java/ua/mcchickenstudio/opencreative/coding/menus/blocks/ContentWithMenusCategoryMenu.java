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

package ua.mcchickenstudio.opencreative.coding.menus.blocks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.menus.BlockMenu;
import ua.mcchickenstudio.opencreative.menus.ListBrowserMenu;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * This class represents a menus where player can select type of coding block.
 * Every category of coding blocks has this menus.
 */
public abstract class ContentWithMenusCategoryMenu<T> extends ListBrowserMenu<T> implements BlockMenu {

    private final Player player;
    private final String codingBlockName;
    private final Material stainedPane;

    protected Location signLocation;
    protected MenusCategory currentCategory;

    private final ItemStack BACK_TO_CATEGORIES;
    private MenusCategorySelectionMenu categoriesMenu;

    public ContentWithMenusCategoryMenu(@NotNull Player player,
                                        @NotNull Location location,
                                        @NotNull String codingBlockName,
                                        @NotNull String titleName,
                                        @NotNull Material stainedPane,
                                        @NotNull MenusCategory defaultCategory) {
        super(player,ChatColor.stripColor(getLocaleMessage("blocks." + titleName,false)),
                PlacementLayout.BOTTOM_NO_DECORATION, new int[]{45},new int[]{45,46,52,53});
        this.codingBlockName = codingBlockName;
        this.player = player;
        this.signLocation = location;
        this.stainedPane = stainedPane;
        this.currentCategory = defaultCategory;
        this.BACK_TO_CATEGORIES = createItem(Material.SPECTRAL_ARROW, 1, "items.developer.categories." + codingBlockName + ".back-to-categories","categories");
    }

    public void setCategoriesMenu(MenusCategorySelectionMenu categoriesMenu) {
        this.categoriesMenu = categoriesMenu;
    }

    public void setCurrentCategory(MenusCategory currentCategory) {
        this.currentCategory = currentCategory;
    }

    public void setSignLocation(Location signLocation) {
        this.signLocation = signLocation;
    }

    @Override
    protected void fillArrowsItems(int currentPage) {
        if (elements.isEmpty()) {
            setItem(getNoElementsPageButtonSlot(), getNoElementsButton());
            setItem(getPreviousPageButtonSlot(), BACK_TO_CATEGORIES);
            setItem(getNextPageButtonSlot(), DECORATION_ITEM);
        } else {
            int maxPagesAmount = getPages();
            if (currentPage > maxPagesAmount || currentPage < 1) {
                currentPage = 1;
            }
            setItem(getPreviousPageButtonSlot(),currentPage > 1 ? getPreviousPageButton() : BACK_TO_CATEGORIES);
            setItem(getNextPageButtonSlot(),currentPage < maxPagesAmount ? getNextPageButton() : DECORATION_ITEM);
        }
    }

    @Override
    protected void fillOtherItems() {
        setItem(BACK_TO_CATEGORIES,45);
        setItem(createItem(stainedPane,1),47,51);
        setItem(currentCategory.getItem(codingBlockName),49);
    }

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        event.setCancelled(true);
        if (getItemType(clicked).equalsIgnoreCase("categories")) {
            if (categoriesMenu != null) {
                categoriesMenu.open(player);
            } else {
                player.closeInventory();
            }
        }
    }

    @Override
    protected ItemStack getNextPageButton() {
        return replacePlaceholderInName(createItem(Material.SPECTRAL_ARROW,1,"items.developer.categories." + codingBlockName + ".next-page"),"%page%", getCurrentPage() +1);
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return replacePlaceholderInName(createItem(Material.SPECTRAL_ARROW,1,"items.developer.categories." + codingBlockName + ".previous-page"),"%page%", getCurrentPage() -1);
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER,1,"items.developer.categories." + codingBlockName + ".empty");
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {

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
