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

package mcchickenstudio.creative.coding.menus.variables;

import mcchickenstudio.creative.coding.blocks.events.EventValues;
import mcchickenstudio.creative.coding.menus.MenusCategory;
import mcchickenstudio.creative.menu.AbstractListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.MessageUtils.getPathFromMessage;

public class EventValuesMenu extends AbstractListMenu {

    protected MenusCategory currentCategory = MenusCategory.WORLD;

    public EventValuesMenu(Player player) {
        super(ChatColor.stripColor(getLocaleMessage("menus.developer.event-values.title",false)), player);
    }

    @Override
    protected ItemStack getElementIcon(Object object) {
        if (object instanceof EventValues.Variable variable) {
            return createItem(variable.getIcon(),1,"menus.developer.event-values.items." + variable.name().toLowerCase().replace("_","-"));
        }
        return ItemStack.empty();
    }

    @Override
    protected void fillOtherItems() {
        byte slot = 0;
        for (MenusCategory category : getMenusCategories()) {
            setItem(charmsBarSlots[slot],category.getItem("event-values"));
            slot++;
        }
    }

    protected Set<MenusCategory> getMenusCategories() {
        return new HashSet<>(List.of(MenusCategory.ENTITY,MenusCategory.EVENTS,MenusCategory.WORLD));
    }


    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        event.setCancelled(true);
        MenusCategory category = MenusCategory.getByMaterial(clicked.getType());
        if (category != null) {
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ITEM_BOOK_PAGE_TURN,100f,0.5f);
            currentCategory = category;
            elements.clear();
            elements.addAll(getElements());
            fillElements((byte) 1);
            fillArrowsItems((byte) 1);
        }
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        event.setCancelled(true);
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        ItemMeta meta = itemInHand.getItemMeta();
        String beginLocalizationPath = "menus.developer.event-values.items.";
        String path = getPathFromMessage(beginLocalizationPath, item.getItemMeta().getDisplayName());
        if (path == null || !path.endsWith(".name")) {
            return;
        }
        String typeString = path.replace(beginLocalizationPath,"").replace(".name","").replace("-","_");
        meta.displayName(Component.text(item.getItemMeta().getDisplayName()));
        itemInHand.setItemMeta(meta);
        setPersistentData(itemInHand,getCodingValueKey(),"EVENT_VALUE");
        setPersistentData(itemInHand,getCodingVariableTypeKey(),typeString.toUpperCase());
        player.closeInventory();
        player.sendTitle(getLocaleMessage("world.dev-mode.set-variable",false),item.getItemMeta().getDisplayName(),0,40,20);
        player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,100,1.7f);
    }

    @Override
    protected List<Object> getElements() {
        if (currentCategory == null) currentCategory = MenusCategory.ENTITY;
        return new ArrayList<>(EventValues.Variable.getByCategories(currentCategory));
    }

    @Override
    protected ItemStack getNextPageButton() {
        return createItem(Material.SPECTRAL_ARROW,1,"menus.developer.event-values.items.next-page");
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return createItem(Material.SPECTRAL_ARROW,1,"menus.developer.event-values.items.previous-page");
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.SPECTRAL_ARROW,1,"menus.developer.event-values.items.no-elements");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

    }
}
