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

public class WorldAccessMenu extends AbstractMenu {

    private final ItemStack JOIN_WORLD_ITEM = createItem(Material.POTATO,1,"menus.delete-mobs.items.items");

    public WorldAccessMenu() {
        super((byte) 3, MessageUtils.getLocaleMessage("menus.delete-mobs.title",false));
    }

    @Override
    public void fillItems(Player player) {

    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!isPlayerClicked(event)) return;
        Player player = (Player) event.getWhoClicked();
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        player.playSound(player.getLocation(), Sound.ENTITY_PANDA_WORRIED_AMBIENT,100,0.1f);
    }
}
