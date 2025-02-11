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

package ua.mcchickenstudio.opencreative.coding.menus.variables;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ua.mcchickenstudio.opencreative.menus.ListBrowserMenu;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.time.Duration;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public class EventValuesMenu extends ListBrowserMenu<EventValues.Variable> {

    protected MenusCategory currentCategory = MenusCategory.WORLD;

    public EventValuesMenu(Player player) {
        super(player,ChatColor.stripColor(getLocaleMessage("menus.developer.event-values.title",false)));
    }

    @Override
    protected ItemStack getElementIcon(EventValues.Variable variable) {
        ItemStack icon = createItem(variable.getIcon(),1,"menus.developer.event-values.items." + variable.name().toLowerCase().replace("_","-"));
        setPersistentData(icon,getCodingVariableTypeKey(),variable.name());
        return icon;
    }

    @Override
    protected void fillOtherItems() {
        int slot = 0;
        for (MenusCategory category : getMenusCategories()) {
            setItem(getCharmsBarSlots()[slot],category.getItem("event-values"));
            slot++;
        }
    }

    protected Set<MenusCategory> getMenusCategories() {
        return new HashSet<>(List.of(MenusCategory.PLAYER,MenusCategory.ENTITY,MenusCategory.EVENTS,MenusCategory.WORLD));
    }


    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        event.setCancelled(true);
        MenusCategory category = MenusCategory.getByMaterial(clicked.getType());
        if (category != null) {
            Sounds.DEV_CHANGE_CATEGORY.play(event.getWhoClicked());
            currentCategory = category;
            elements.clear();
            elements.addAll(getElements());
            fillElements(1);
            fillArrowsItems(1);
        }
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        ItemStack itemInHand = getPlayer().getInventory().getItemInMainHand();
        event.setCancelled(true);
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        ItemMeta meta = itemInHand.getItemMeta();
        String typeString = getPersistentData(item,getCodingVariableTypeKey());
        meta.displayName(Component.text(item.getItemMeta().getDisplayName()));
        itemInHand.setItemMeta(meta);
        setPersistentData(itemInHand,getCodingValueKey(),"EVENT_VALUE");
        setPersistentData(itemInHand,getCodingVariableTypeKey(),typeString.toUpperCase());
        getPlayer().closeInventory();
        getPlayer().showTitle(Title.title(
                toComponent(getLocaleMessage("world.dev-mode.set-variable")), item.displayName(),
                Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
        ));
        Sounds.DEV_EVENT_VALUE_SET.play(event.getWhoClicked());
    }

    @Override
    protected List<EventValues.Variable> getElements() {
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
    public void onOpen(@NotNull InventoryOpenEvent event) {

    }
}
