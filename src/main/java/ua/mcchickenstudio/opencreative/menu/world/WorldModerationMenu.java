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

package ua.mcchickenstudio.opencreative.menu.world;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.menu.AbstractMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.getItemType;

public class WorldModerationMenu extends AbstractMenu {

    private final Planet planet;
    private final ItemStack CLEAR_NAME = createItem(Material.NAME_TAG,1,"menus.world-moderation.items.clear-name","clear-name");
    private final ItemStack CLEAR_DESCRIPTION = createItem(Material.BOOK,1,"menus.world-moderation.items.clear-description","clear-description");
    private final ItemStack CLEAR_ICON = createItem(Material.DIAMOND,1,"menus.world-moderation.items.clear-icon","clear-icon");

    private final ItemStack CONNECT_SILENT = createItem(Material.ENDER_PEARL,1,"menus.world-moderation.items.connect-silent","connect-silent");
    private final ItemStack CONNECT_DEV_SILENT = createItem(Material.ENDER_EYE,1,"menus.world-moderation.items.connect-dev-silent","connect-dev-silent");
    private final ItemStack LOAD = createItem(Material.CHERRY_CHEST_BOAT,1,"menus.world-moderation.items.load","load");
    private final ItemStack UNLOAD = createItem(Material.CHEST_MINECART,1,"menus.world-moderation.items.load","load");

    private final ItemStack CLOSE_WORLD = createItem(Material.BARRIER,1,"menus.world-moderation.items.close-world","close-world");

    public WorldModerationMenu(Planet planet) {
        super(6, MessageUtils.getLocaleMessage("menus.world-moderation.title",false));
        this.planet = planet;
    }

    @Override
    public void fillItems(Player player) {
        setItem(DECORATION_PANE_ITEM,45,46,52,53);
        setItem(createItem(Material.YELLOW_STAINED_GLASS_PANE,1),47,51);
        setItem(49,planet.getInformation().getIcon());
        setItem(10,CLEAR_NAME);
        setItem(19,CLEAR_DESCRIPTION);
        setItem(28,CLEAR_ICON);
        setItem(12,CONNECT_SILENT);
        setItem(13,CONNECT_DEV_SILENT);
        setItem(16,planet.isLoaded() ? UNLOAD : LOAD);
        setItem(34,CLOSE_WORLD);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (!isPlayerClicked(event) || !isClickedInMenuSlots(event)) return;
        if (item == null) return;
        switch (getItemType(item)) {
            case "clear-name" -> {
                planet.getInformation().setDisplayName("[content deleted]");
                event.getWhoClicked().closeInventory();
            }
            case "clear-description" -> {
                planet.getInformation().setDescription("[content deleted]");
                event.getWhoClicked().closeInventory();
            }
            case "close-world" -> {
                planet.setSharing(Planet.Sharing.PRIVATE);
                event.getWhoClicked().closeInventory();
            }
        };
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
    }
}
