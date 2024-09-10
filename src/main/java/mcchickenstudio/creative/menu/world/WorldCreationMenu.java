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

package mcchickenstudio.creative.menu.world;

import mcchickenstudio.creative.menu.AbstractMenu;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.utils.MessageUtils;
import mcchickenstudio.creative.utils.WorldUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.ItemUtils.itemEquals;

public class WorldCreationMenu extends AbstractMenu {

    private final ItemStack FLAT_WORLD_ITEM = createItem(Material.GRASS_BLOCK,1,"menus.world-creation.items.flat");
    private final ItemStack EMPTY_WORLD_ITEM = createItem(Material.GLASS,1,"menus.world-creation.items.empty");
    private final ItemStack OCEAN_WORLD_ITEM = createItem(Material.WATER_BUCKET,1,"menus.world-creation.items.water");
    private final ItemStack PLAINS_WORLD_ITEM = createItem(Material.SHORT_GRASS,1,"menus.world-creation.items.survival");

    public WorldCreationMenu() {
        super((byte) 3, MessageUtils.getLocaleMessage("menus.world-creation.title",false));
    }

    @Override
    public void fillItems(Player player) {
        setItem((byte) 10,FLAT_WORLD_ITEM);
        setItem((byte) 12,EMPTY_WORLD_ITEM);
        setItem((byte) 14,OCEAN_WORLD_ITEM);
        setItem((byte) 16,PLAINS_WORLD_ITEM);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (itemEquals(event.getCurrentItem(), FLAT_WORLD_ITEM)) {
            new Plot((Player) event.getWhoClicked(), WorldUtils.WorldGenerator.FLAT);
        } else if (itemEquals(event.getCurrentItem(), EMPTY_WORLD_ITEM)) {
            new Plot((Player) event.getWhoClicked(), WorldUtils.WorldGenerator.EMPTY);
        } else if (itemEquals(event.getCurrentItem(), OCEAN_WORLD_ITEM)) {
            new Plot((Player) event.getWhoClicked(), WorldUtils.WorldGenerator.WATER);
        } else if (itemEquals(event.getCurrentItem(), PLAINS_WORLD_ITEM)) {
            new Plot((Player) event.getWhoClicked(), WorldUtils.WorldGenerator.SURVIVAL);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {}
}
