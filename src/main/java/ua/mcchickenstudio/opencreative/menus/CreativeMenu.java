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

package ua.mcchickenstudio.opencreative.menus;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class CreativeMenu extends AbstractMenu {

    private final ItemStack RELOAD_ITEM = createItem(Material.STRUCTURE_VOID,1,"menus.creative.items.reload","reload");
    private final ItemStack RESET_LOCALE_ITEM = createItem(Material.BOOKSHELF,1,"menus.creative.items.reset-locale","reset-locale");

    public CreativeMenu() {
        super(6, getLocaleMessage("menus.creative.title",false).replace("%version%", OpenCreative.getVersion()).replace("%codename%", OpenCreative.getCodename()));
    }

    @Override
    public void fillItems(Player player) {
        setItem(49,createItem(Material.BEACON,1,"menus.creative.items.info"));
        setItem(createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,1),47,51);
        setItem(DECORATION_PANE_ITEM,45,46,52,53);
        setItem(10,player.hasPermission("opencreative.reload") ? RELOAD_ITEM : DECORATION_ITEM);
        setItem(12,player.hasPermission("opencreative.resetlocale") ? RESET_LOCALE_ITEM : DECORATION_ITEM);
        setItem(14,player.hasPermission("opencreative.updates.check") ? DECORATION_PANE_ITEM : DECORATION_ITEM);
        setItem(16,player.hasPermission("opencreative.maintenance") ? DECORATION_PANE_ITEM : DECORATION_ITEM);
        setItem(28,player.hasPermission("opencreative.list.loaded") ? DECORATION_PANE_ITEM : DECORATION_ITEM);
        setItem(30,player.hasPermission("opencreative.list.corrupted") ? DECORATION_PANE_ITEM : DECORATION_ITEM);
        setItem(32,player.hasPermission("opencreative.list.deprecated") ? DECORATION_PANE_ITEM : DECORATION_ITEM);
        setItem(34,player.hasPermission("opencreative.debug") ? DECORATION_PANE_ITEM : DECORATION_ITEM);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);
        if (!isClickedInMenuSlots(event)) return;
        Player player = (Player) event.getWhoClicked();
        switch (getItemType(clickedItem)) {
            case "reload" -> player.performCommand("creative reload");
            case "reset-locale" -> player.performCommand("creative resetlocale");
            case "check-updates" -> player.performCommand("creative checkupdates");
            case "maintenance-start" -> player.performCommand("creative maintenance start");
            case "maintenance-end" -> player.performCommand("creative maintenance end");
            case "debug-enable" -> player.performCommand("creative debug enable");
            case "debug-disable" -> player.performCommand("creative debug disable");
            case "list" -> player.performCommand("creative list");
            case "deprecated" -> player.performCommand("creative deprecated");
            case "corrupted" -> player.performCommand("creative corrupted");
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {}
}
