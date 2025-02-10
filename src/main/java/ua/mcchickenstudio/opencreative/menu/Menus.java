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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Menus</h1>
 * This class represents a menus manager, that
 * stores current opened menus for handling
 * inventory open, click and close events.
 * <p>
 * To make {@link InventoryMenu} usable for handling inventory events, register this menu with
 * {@link Menus#addMenu(InventoryMenu)} method. To avoid memory leaks, please unregister it
 * on inventory close event with {@link Menus#removeMenu(InventoryMenu)}
 */
public class Menus implements Listener {

    private static final List<InventoryMenu> activeMenus = new ArrayList<>();

    /**
     * Registers menu in menus manager for handling inventory events.
     * @param menu menu to add.
     */
    public static void addMenu(InventoryMenu menu) {
        activeMenus.add(menu);
    }

    /**
     * Unregisters menu from menus event listeners, required if menu is not more
     * useful because player closed it.
     * @param menu menu to remove.
     */
    public static void removeMenu(InventoryMenu menu) {
        activeMenus.remove(menu);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        for (InventoryMenu menu : activeMenus) {
            if (event.getInventory().getHolder() == menu.getInventory().getHolder()) {
                menu.onClick(event);
                return;
            }
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        for (InventoryMenu menu : activeMenus) {
            if (event.getInventory().getHolder() == menu.getInventory().getHolder()) {
                menu.onOpen(event);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        for (InventoryMenu menu : activeMenus) {
            if (event.getInventory().getHolder() == menu.getInventory().getHolder()) {
                menu.onClose(event);
                return;
            }
        }
    }

}
