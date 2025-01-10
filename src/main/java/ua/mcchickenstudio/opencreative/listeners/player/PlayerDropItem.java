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

package ua.mcchickenstudio.opencreative.listeners.player;

import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.inventory.ItemStack;


import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleItemName;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class PlayerDropItem implements Listener {

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        if (item.hasItemMeta() && item.getItemMeta() != null) {
            Component name = item.getItemMeta().displayName();
            if (name != null) {
                String displayName = item.getItemMeta().getDisplayName();
                if (!player.getWorld().getName().startsWith("planet")) {
                    if (displayName.equals(getLocaleItemName("items.lobby.games.name")) || displayName.equals(getLocaleMessage("items.lobby.own.name"))) {
                        event.setCancelled(true);
                    }
                } else {
                    if (displayName.equals(getLocaleItemName("items.developer.world-settings.name"))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
        if (planet != null) EventRaiser.raiseItemDropEvent(event.getPlayer(),event);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
        if (planet != null)  EventRaiser.raiseItemPickupEvent(player,event);
    }

}
