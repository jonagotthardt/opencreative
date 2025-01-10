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
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;


import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;

public class PlayerRespawn implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!PlayerDeath.deathLocations.containsKey(event.getPlayer())) return;
        Location deathLocation = PlayerDeath.deathLocations.get(event.getPlayer());
        event.setRespawnLocation(deathLocation);
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_BREATH,100,2);
        PlayerDeath.deathLocations.remove(event.getPlayer());
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(event.getPlayer());
        if (planet != null) {
            EventRaiser.raisePlayerRespawnEvent(event.getPlayer(),event);
            if (planet.isOwner(event.getPlayer())) {
                ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
                if (!event.getPlayer().getInventory().contains(worldSettingsItem)) {
                    event.getPlayer().getInventory().setItem(8,worldSettingsItem);
                }
            }
        }
    }

    @EventHandler
    public void onTotemUsing(EntityResurrectEvent event) {
        Planet planet = PlanetManager.getInstance().getPlanetByWorld((event.getEntity().getWorld()));
        if (planet != null) EventRaiser.raisePlayerTotemRespawnEvent(event.getEntity(),event);

    }
}
