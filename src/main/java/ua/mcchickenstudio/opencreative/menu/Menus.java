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

public class Menus implements Listener {

    private static final List<InventoryMenu> activeMenus = new ArrayList<>();
    public static void addMenu(InventoryMenu menu) {
        activeMenus.add(menu);
    }
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
