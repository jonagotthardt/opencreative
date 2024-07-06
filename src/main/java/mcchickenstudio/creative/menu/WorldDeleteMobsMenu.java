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

import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.ItemUtils.itemEquals;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class WorldDeleteMobsMenu extends AbstractMenu {

    private final ItemStack DELETE_ITEMS_ITEM = createItem(Material.POTATO,1,"menus.delete-mobs.items.items");
    private final ItemStack DELETE_ENTITIES_ITEM = createItem(Material.ARMOR_STAND,1,"menus.delete-mobs.items.entities");
    private final ItemStack DELETE_MOBS_ITEM = createItem(Material.PIG_SPAWN_EGG,1,"menus.delete-mobs.items.mobs");

    public WorldDeleteMobsMenu() {
        super((byte) 3, MessageUtils.getLocaleMessage("menus.delete-mobs.title",false));
    }

    @Override
    public void fillItems(Player player) {
        setItem((byte) 10,DELETE_ITEMS_ITEM);
        setItem((byte) 13,DELETE_ENTITIES_ITEM);
        setItem((byte) 16,DELETE_MOBS_ITEM);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!isPlayerClicked(event)) return;
        Plot plot = PlotManager.getInstance().getPlotByPlayer((Player) event.getWhoClicked());
        if (plot == null) return;
        if (!plot.isOwner((Player) event.getWhoClicked())) {
            event.getWhoClicked().closeInventory();
            return;
        }
        int count = 0;
        if (itemEquals(event.getCurrentItem(),DELETE_ITEMS_ITEM)) {
            for (Entity entity : plot.world.getEntities()) {
                if (entity instanceof Item) {
                    entity.remove();
                    count++;
                }
            }
            if (plot.devPlot != null && plot.devPlot.world != null) {
                for (Entity entity : plot.devPlot.world.getEntities()) {
                    if (entity instanceof Item) {
                        entity.remove();
                        count++;
                    }
                }
            }
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(getLocaleMessage("world.delete-mobs.items").replace("%count%", String.valueOf(count)));
        } else if (itemEquals(event.getCurrentItem(),DELETE_ENTITIES_ITEM)) {
            for (Entity entity : plot.world.getEntities()) {
                if (!(entity instanceof LivingEntity)) {
                    entity.remove();
                    count++;
                }
            }
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(getLocaleMessage("world.delete-mobs.entities").replace("%count%", String.valueOf(count)));
        } else if (itemEquals(event.getCurrentItem(),DELETE_MOBS_ITEM)) {
            for (Entity entity : plot.world.getEntities()) {
                if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                    entity.remove();
                    count++;
                }
            }
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(getLocaleMessage("world.delete-mobs.mobs").replace("%count%", String.valueOf(count)));
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}
}
