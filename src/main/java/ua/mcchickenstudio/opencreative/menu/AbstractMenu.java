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

package ua.mcchickenstudio.opencreative.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;

/**
 * <h1>AbstractMenu</h1>
 * This class represents a menu with set items. It has
 * methods to check player's click, open and close inventory
 * events.
 */
public abstract class AbstractMenu implements InventoryHolder {

    private byte rows;
    private String title;
    private Map<Byte, ItemStack> items = new HashMap<>();

    protected final byte[] defaultIgnoredSlots = new byte[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,49,50,51,52,53};
    protected final byte[] allowedSlots = new byte[]{10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
    protected final ItemStack AIR_ITEM = new ItemStack(Material.AIR);
    protected final ItemStack NO_PERMS_ITEM = createItem(Material.RED_STAINED_GLASS,1);
    protected final ItemStack DECORATION_ITEM = createItem(Material.LIGHT_GRAY_STAINED_GLASS,1);
    protected final ItemStack DECORATION_PANE_ITEM = createItem(Material.GRAY_STAINED_GLASS_PANE,1);
    protected Inventory inventory;

    public AbstractMenu(byte rows, String title) {
        this.rows = rows;
        this.title = title;
    }

    public boolean addItem(ItemStack item) {
        for (byte i = 0; i < rows*9; i++) {
            if (items.get(i) == null) {
                items.put(i,item);
                return true;
            }
        }
        return false;
    }

    public void setItem(byte slot, ItemStack item) {
        if (item == null) item = new ItemStack(Material.AIR);
        if (!(slot >= rows*9) && !(slot<0)) {
            items.put(slot,item);
        }
    }

    public void updateSlot(byte slot) {
        if (inventory != null && slot < rows*9 && slot >= 0) {
            inventory.setItem(slot,items.get(slot));
        }
    }

    public ItemStack getItem(byte slot) {
        if (slot < 0 || slot >= getItems().size()) return new ItemStack(Material.AIR);
        return getItems().get(slot);
    }

    public void setItems(Map<Byte, ItemStack> items) {
        this.items = items;
    }

    public List<ItemStack> getItems() {
        return new ArrayList<>(items.values());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public @NotNull Inventory getInventory() {
        if (rows > 6 || rows < 1) rows = 6;
        Inventory inventory = Bukkit.createInventory(this, this.rows * 9, Component.text(this.title));
        for (Map.Entry<Byte,ItemStack> item : items.entrySet()) {
            inventory.setItem(item.getKey(),item.getValue());
        }
        return inventory;
    }

    public @NotNull Inventory getCurrentInventory() {
        if (inventory == null) {
            return getInventory();
        }
        return inventory;
    }

    public void open(Player player) {
        Menus.addMenu(this);
        try {
            fillItems(player);
            inventory = getInventory();
            player.openInventory(inventory);
        } catch (Exception e) {
            sendPlayerErrorMessage(player,"Failed to open AbstractMenu with title " + title + ". ",e);
        }

    }

    public abstract void fillItems(Player player);
    public abstract void onClick(InventoryClickEvent event);
    public abstract void onOpen(InventoryOpenEvent event);

    public void onClose(InventoryCloseEvent event) {
        destroy();
    }

    protected final boolean isClickedInMenuSlots(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return false;
        if (event.getInventory().getHolder() == null) return false;
        return event.getInventory().getHolder().equals(event.getClickedInventory().getHolder());
    }

    protected final boolean isPlayerClicked(InventoryClickEvent event) {
        return (event.getWhoClicked() instanceof Player);
    }


    public byte getSize() {
        return (byte) (rows*9);
    }

    public byte getRows() {
        return rows;
    }

    protected void setRows(byte rows) {
        this.rows = rows;
    }

    protected boolean isEmpty(ItemStack item) {
        return (item != null && item.getType() != Material.AIR);
    }

    public void destroy() {
        Menus.removeMenu(this);
    }
}
