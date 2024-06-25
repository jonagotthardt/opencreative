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

import mcchickenstudio.creative.menu.AbstractMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.ItemUtils.itemEquals;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class VariablesMenu extends AbstractMenu  {

    private final ItemStack TEXT_ITEM = createItem(Material.BOOK,1,"menus.developer.variables.items.text");
    private final ItemStack NUMBER_ITEM = createItem(Material.SLIME_BALL,1,"menus.developer.variables.items.number");
    private final ItemStack BOOLEAN_ITEM = createItem(Material.CLOCK,1,"menus.developer.variables.items.boolean");
    private final ItemStack LOCATION_ITEM = createItem(Material.PAPER,1,"menus.developer.variables.items.location");
    //private final ItemStack ARRAY_ITEM = createItem(Material.SLIME_BALL,1,"menus.developer.variables.items.array");
    //private final ItemStack VARIABLE_ITEM = createItem(Material.SLIME_BALL,1,"menus.developer.variables.items.variable");
    //private final ItemStack POTION_ITEM = createItem(Material.GLASS_BOTTLE,1,"menus.developer.variables.items.potion");
    //private final ItemStack PARTICLE_ITEM = createItem(Material.SLIME_BALL,1,"menus.developer.variables.items.particle");
    //private final ItemStack ENTITY_TYPE_ITEM = createItem(Material.SLIME_BALL,1,"menus.developer.variables.items.entity_type");
    //private final ItemStack MAP_ITEM = createItem(Material.SLIME_BALL,1,"menus.developer.variables.items.map");


    public VariablesMenu() {
        super((byte) 1, getLocaleMessage("menus.developer.variables.title"));
    }

    @Override
    public void fillItems(Player player) {
        setItem((byte) 0,TEXT_ITEM);
        setItem((byte) 1,NUMBER_ITEM);
        setItem((byte) 2,BOOLEAN_ITEM);
        setItem((byte) 3,LOCATION_ITEM);
        setItem((byte) 4,DECORATION_ITEM);
        setItem((byte) 5,DECORATION_ITEM);
        setItem((byte) 6,DECORATION_ITEM);
        setItem((byte) 7,DECORATION_ITEM);
        setItem((byte) 8,DECORATION_ITEM);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null || itemEquals(event.getCurrentItem(), DECORATION_ITEM)) return;
        event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),Sound.ENTITY_ALLAY_ITEM_THROWN,100f,2f);
        event.setCursor(null);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 100f, 1f);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {}
}
