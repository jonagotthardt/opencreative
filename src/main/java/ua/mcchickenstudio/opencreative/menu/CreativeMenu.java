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

package ua.mcchickenstudio.opencreative.menu;

import ua.mcchickenstudio.opencreative.OpenCreative;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.itemEquals;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class CreativeMenu extends AbstractMenu {

    private final ItemStack RELOAD_ITEM = createItem(Material.STRUCTURE_VOID,1,"menus.creative.items.reload");
    private final ItemStack RESET_LOCALE_ITEM = createItem(Material.BOOKSHELF,1,"menus.creative.items.reset-locale");

    public CreativeMenu() {
        super((byte) 3, getLocaleMessage("menus.creative.title",false).replace("%version%", OpenCreative.getVersion()).replace("%codename%", OpenCreative.getCodename()));
    }

    @Override
    public void fillItems(Player player) {
        setItem((byte) 13,createItem(Material.BEACON,1,"menus.creative.items.info"));
        setItem((byte) 0,DECORATION_PANE_ITEM);
        setItem((byte) 8,DECORATION_PANE_ITEM);
        setItem((byte) 9,DECORATION_PANE_ITEM);
        setItem((byte) 10,DECORATION_ITEM);
        setItem((byte) 16,DECORATION_ITEM);
        setItem((byte) 17,DECORATION_PANE_ITEM);
        setItem((byte) 29,DECORATION_ITEM);
        setItem((byte) 18,DECORATION_PANE_ITEM);
        setItem((byte) 19,DECORATION_ITEM);
        setItem((byte) 25,DECORATION_ITEM);
        setItem((byte) 26,DECORATION_PANE_ITEM);
        setItem((byte) 1,player.hasPermission("opencreative.reload") ? RELOAD_ITEM : DECORATION_ITEM);
        setItem((byte) 7,player.hasPermission("opencreative.resetlocale") ? RESET_LOCALE_ITEM : DECORATION_ITEM);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);
        if (event.getClickedInventory() != event.getInventory()) return;
        Player player = (Player) event.getWhoClicked();
        if (itemEquals(clickedItem,RELOAD_ITEM)) {
            player.performCommand("creative reload");
            player.closeInventory();
        } else if (itemEquals(clickedItem, RESET_LOCALE_ITEM)) {
            player.performCommand("creative resetlocale");
            player.closeInventory();
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {}
}
