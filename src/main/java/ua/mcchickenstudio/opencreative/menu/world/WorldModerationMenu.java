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
    private final ItemStack CLEAR_NAME = createItem(Material.NAME_TAG,1,"menus.world-moderation.clear-name","clear-name");
    private final ItemStack CLEAR_DESCRIPTION = createItem(Material.BOOK,1,"menus.world-moderation.clear-description","clear-description");
    private final ItemStack CLOSE_WORLD = createItem(Material.BOOK,1,"menus.world-moderation.close-world","close-world");

    public WorldModerationMenu(Planet planet) {
        super(3, MessageUtils.getLocaleMessage("menus.world-moderation.title",false));
        this.planet = planet;
    }

    @Override
    public void fillItems(Player player) {
        setItem(10,CLEAR_NAME);
        setItem(11,CLEAR_DESCRIPTION);
        setItem(12,CLOSE_WORLD);
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
        player.playSound(player.getLocation(), Sound.ENTITY_PANDA_WORRIED_AMBIENT,100,0.1f);
    }
}
