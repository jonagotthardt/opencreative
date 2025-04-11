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
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.BlockMenu;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>MenusCategorySelectionMenu</h1>
 * This class represents a menu for selecting menus category
 * and opening a coding block type selection menu.
 */
public class MenusCategorySelectionMenu extends AbstractMenu implements BlockMenu {

    private final Player player;
    private final Location signLocation;
    private final Material additionalPane;
    private final ItemStack blockItem;
    private final String blockCategory;
    private final ContentWithMenusCategoryMenu<?> menu;

    private final List<MenusCategory> menusCategories = new ArrayList<>();

    public MenusCategorySelectionMenu(@NotNull Player player,
                                      @NotNull Location location,
                                      @NotNull ExecutorCategory category) {
        super(6,ChatColor.stripColor(getLocaleMessage("blocks." + category.name().toLowerCase())));
        this.signLocation = location;
        this.additionalPane = category.getStainedPane();
        this.player = player;
        this.menusCategories.addAll(ExecutorType.getMenusCategories(category));
        this.blockItem = createItem(category.getBlock(),1,"items.developer." + category.name().toLowerCase().replace("_","-"));
        this.blockCategory = "events";
        this.menu = new ExecutorTypeSelectionMenu(
                player, player.getLocation(),
                category
        );
        menu.setCategoriesMenu(this);
    }

    public MenusCategorySelectionMenu(@NotNull Player player,
                                      @NotNull Location location,
                                      @NotNull ActionCategory blockCategory) {
        super(6,ChatColor.stripColor(getLocaleMessage("blocks." + blockCategory.name().toLowerCase())));
        this.signLocation = location;
        this.additionalPane = blockCategory.getStainedPane();
        this.player = player;
        this.menusCategories.addAll(ActionType.getMenusCategories(blockCategory));
        this.blockItem = createItem(blockCategory.getBlock(),1,"items.developer." + blockCategory.name().toLowerCase().replace("_","-"));
        this.blockCategory = blockCategory.isCondition() ? "conditions" : "actions";
        this.menu = new ActionTypeSelectionMenu(
                player, player.getLocation(),
                blockCategory
        );
        menu.setCategoriesMenu(this);
    }

    private void fillRow(int row) {
        if (row < 1) row = 1;
        if (row > 6) row = 6;
        int firstSlot = (row-1)*9;
        setItem(firstSlot,DECORATION_PANE_ITEM);
        setItem(firstSlot+1,DECORATION_PANE_ITEM);
        setItem(firstSlot+2,createItem(additionalPane,1));

        setItem(firstSlot+4,blockItem);

        setItem(firstSlot+6,createItem(additionalPane,1));
        setItem(firstSlot+7,DECORATION_PANE_ITEM);
        setItem(firstSlot+8,DECORATION_PANE_ITEM);
    }

    @Override
    public void fillItems(Player player) {
        switch (menusCategories.size()) {
            case 0 -> {
                setRows(4);
                setItem(13,createItem(Material.BARRIER,1,"items.developer.categories." + blockCategory + ".empty"));
            }
            case 1 -> {
                setRows(4);
                setItem(13,menusCategories.getFirst().getItem(blockCategory));
            }
            case 2 -> {
                setRows(4);
                setItem(11,menusCategories.getFirst().getItem(blockCategory));
                setItem(15,menusCategories.get(1).getItem(blockCategory));
            }
            case 3 -> {
                setRows(4);
                setItem(11,menusCategories.getFirst().getItem(blockCategory));
                setItem(13,menusCategories.get(1).getItem(blockCategory));
                setItem(15,menusCategories.get(2).getItem(blockCategory));
            }
            case 4 -> {
                setRows(4);
                setItem(10,menusCategories.getFirst().getItem(blockCategory));
                setItem(12,menusCategories.get(1).getItem(blockCategory));
                setItem(14,menusCategories.get(2).getItem(blockCategory));
                setItem(16,menusCategories.get(3).getItem(blockCategory));
            }
            case 5 -> {
                setRows(6);
                setItem(10,menusCategories.getFirst().getItem(blockCategory));
                setItem(13,menusCategories.get(1).getItem(blockCategory));
                setItem(16,menusCategories.get(2).getItem(blockCategory));
                setItem(28,menusCategories.get(3).getItem(blockCategory));
                setItem(31,menusCategories.get(4).getItem(blockCategory));
            }
            case 6 -> {
                setRows(6);
                setItem(10,menusCategories.getFirst().getItem(blockCategory));
                setItem(13,menusCategories.get(1).getItem(blockCategory));
                setItem(16,menusCategories.get(2).getItem(blockCategory));
                setItem(28,menusCategories.get(3).getItem(blockCategory));
                setItem(31,menusCategories.get(4).getItem(blockCategory));
                setItem(34,menusCategories.get(5).getItem(blockCategory));
            }
            case 7 -> {
                setRows(6);
                setItem(10,menusCategories.getFirst().getItem(blockCategory));
                setItem(12,menusCategories.get(1).getItem(blockCategory));
                setItem(14,menusCategories.get(2).getItem(blockCategory));
                setItem(16,menusCategories.get(3).getItem(blockCategory));
                setItem(28,menusCategories.get(4).getItem(blockCategory));
                setItem(30,menusCategories.get(5).getItem(blockCategory));
                setItem(32,menusCategories.get(6).getItem(blockCategory));
            }
            case 8 -> {
                setRows(6);
                setItem(10,menusCategories.getFirst().getItem(blockCategory));
                setItem(12,menusCategories.get(1).getItem(blockCategory));
                setItem(14,menusCategories.get(2).getItem(blockCategory));
                setItem(16,menusCategories.get(3).getItem(blockCategory));
                setItem(28,menusCategories.get(4).getItem(blockCategory));
                setItem(30,menusCategories.get(5).getItem(blockCategory));
                setItem(32,menusCategories.get(6).getItem(blockCategory));
                setItem(34,menusCategories.get(7).getItem(blockCategory));
            }
            case 9 -> {
                setRows(6);
                setItem(10,menusCategories.getFirst().getItem(blockCategory));
                setItem(12,menusCategories.get(1).getItem(blockCategory));
                setItem(14,menusCategories.get(2).getItem(blockCategory));
                setItem(16,menusCategories.get(3).getItem(blockCategory));
                setItem(28,menusCategories.get(4).getItem(blockCategory));
                setItem(30,menusCategories.get(5).getItem(blockCategory));
                setItem(32,menusCategories.get(6).getItem(blockCategory));
                setItem(34,menusCategories.get(7).getItem(blockCategory));
                setItem(20,menusCategories.get(8).getItem(blockCategory));
            }
            case 10 -> {
                setRows(6);
                setItem(10,menusCategories.getFirst().getItem(blockCategory));
                setItem(12,menusCategories.get(1).getItem(blockCategory));
                setItem(14,menusCategories.get(2).getItem(blockCategory));
                setItem(16,menusCategories.get(3).getItem(blockCategory));
                setItem(28,menusCategories.get(4).getItem(blockCategory));
                setItem(30,menusCategories.get(5).getItem(blockCategory));
                setItem(32,menusCategories.get(6).getItem(blockCategory));
                setItem(34,menusCategories.get(7).getItem(blockCategory));
                setItem(20,menusCategories.get(8).getItem(blockCategory));
                setItem(22,menusCategories.get(9).getItem(blockCategory));
            }
            default -> {
                setRows(6);
                setItem(10,menusCategories.getFirst().getItem(blockCategory));
                setItem(12,menusCategories.get(1).getItem(blockCategory));
                setItem(14,menusCategories.get(2).getItem(blockCategory));
                setItem(16,menusCategories.get(3).getItem(blockCategory));
                setItem(28,menusCategories.get(4).getItem(blockCategory));
                setItem(30,menusCategories.get(5).getItem(blockCategory));
                setItem(32,menusCategories.get(6).getItem(blockCategory));
                setItem(34,menusCategories.get(7).getItem(blockCategory));
                setItem(20,menusCategories.get(8).getItem(blockCategory));
                setItem(22,menusCategories.get(9).getItem(blockCategory));
                setItem(24,menusCategories.get(10).getItem(blockCategory));
            }
        }
        fillRow(getRows());
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) return;
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        MenusCategory category = MenusCategory.getByIcon(clicked);
        if (category != null) {
            Sounds.DEV_CHANGE_CATEGORY.play(event.getWhoClicked());
            menu.setCurrentCategory(category);
            menu.setSignLocation(signLocation);
            menu.open(player);
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {}

    @Override
    public @Nullable BlockState getBlockState() {
        return signLocation.getBlock().getState();
    }

    @Override
    public @Nullable Location getLocation() {
        return signLocation;
    }
}
