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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.menu.AbstractMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.substring;

public class WorldAccessMenu extends AbstractMenu {

    private final Planet planet;
    private final ItemStack CONNECT = createItem(Material.NETHER_STAR,1,"menus.world-access.items.connect");

    private final ItemStack PLAY_MODE = createItem(Material.DIAMOND_BLOCK,1,"menus.world-access.items.play-mode");
    private final ItemStack BUILD_MODE = createItem(Material.BRICKS,1,"menus.world-access.items.build-mode");
    private final ItemStack ADVERTISEMENT = createItem(Material.BEACON,1,"menus.world-access.items.advertisement");

    private final ItemStack OPENED = createItem(Material.LIME_STAINED_GLASS,1,"menus.world-access.items.opened");
    private final ItemStack CLOSED = createItem(Material.RED_STAINED_GLASS,1,"menus.world-access.items.closed");
    private final ItemStack DELETE = createItem(Material.TNT_MINECART,1,"menus.world-access.items.delete");

    public WorldAccessMenu(Planet planet) {
        super(4, MessageUtils.getLocaleMessage("menus.world-access.title",false).replace("%name%",substring(ChatColor.stripColor(planet.getInformation().getDisplayName()),25)));
        this.planet = planet;
    }

    @Override
    public void fillItems(Player player) {
        setItem(DECORATION_PANE_ITEM,28,34);
        setItem(createItem(Material.BLUE_STAINED_GLASS_PANE,1),29,33);
        setItem(31,planet.getInformation().getIcon());
        setItem(13, CONNECT);
        setItem(10,planet.getMode() == Planet.Mode.PLAYING ? PLAY_MODE : BUILD_MODE);
        setItem(27,ADVERTISEMENT);
        setItem(16,planet.getSharing() == Planet.Sharing.PUBLIC ? OPENED : CLOSED);
        setItem(35,DELETE);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!isPlayerClicked(event)) return;
        Player player = (Player) event.getWhoClicked();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
    }
}
