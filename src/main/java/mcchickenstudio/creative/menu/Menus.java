package mcchickenstudio.creative.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.List;

public class Menus implements Listener {

    private static final List<AbstractMenu> activeMenus = new ArrayList<>();

    public static void addMenu(AbstractMenu menu) {
        activeMenus.add(menu);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        for (AbstractMenu menu : activeMenus) {
            if (event.getInventory().getHolder() == menu.getInventory().getHolder()) {
                menu.onClick(event);
                return;
            }
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        for (AbstractMenu menu : activeMenus) {
            if (event.getInventory().getHolder() == menu.getInventory().getHolder()) {
                menu.onOpen(event);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        for (AbstractMenu menu : activeMenus) {
            if (event.getInventory().getHolder() == menu.getInventory().getHolder()) {
                menu.onClose(event);
                return;
            }
        }
    }

}
