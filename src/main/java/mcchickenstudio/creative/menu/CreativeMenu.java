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

package mcchickenstudio.creative.menu;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.utils.FileUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static mcchickenstudio.creative.utils.FileUtils.loadLocales;
import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.ItemUtils.itemEquals;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class CreativeMenu extends AbstractMenu {

    private final ItemStack RELOAD_ITEM = createItem(Material.STRUCTURE_VOID,1,"menus.creative.items.reload");
    private final ItemStack RESET_LOCALE_ITEM = createItem(Material.BOOKSHELF,1,"menus.creative.items.reset-locale");

    public CreativeMenu() {
        super((byte) 3, getLocaleMessage("menus.creative.title",false).replace("%version%", Main.version).replace("%codename%",Main.codename));
    }

    @Override
    public void fillItems(Player player) {
        setItem((byte) 10,createItem(Material.BEACON,1,"menus.creative.items.info"));
        if (player.hasPermission("creative.reload")) setItem((byte) 15,RELOAD_ITEM);
        if (player.hasPermission("creative.resetlocale")) setItem((byte) 16,RESET_LOCALE_ITEM);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);
        if (event.getClickedInventory() != event.getInventory()) return;
        if (itemEquals(clickedItem,RELOAD_ITEM)) {
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage("§fCreative§b+ §8| §7Reloading Creative's config and localization...");
            ((Player) event.getWhoClicked()).sendTitle("§f§lCREATIVE§b§l+", "§fReloading...", 0, 60, 20);
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
            Main.getPlugin().reloadConfig();
            loadLocales();
            event.getWhoClicked().sendMessage("§fCreative§b+ §8| §7Reloaded §asuccessfully!");
            ((Player) event.getWhoClicked()).sendTitle("§f§lCREATIVE§b§l+", "§fReloaded successfully!", 0, 60, 20);
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 2);
        } else if (itemEquals(clickedItem, RESET_LOCALE_ITEM)) {
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage("§fCreative§b+ §8| §fResetting localization file...");
            FileUtils.resetLocales();
            event.getWhoClicked().sendMessage("§fCreative§b+ §8| §fLocalization file §6successfully§f reset.");
            ((Player) event.getWhoClicked()).playSound((event.getWhoClicked()).getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}
}
