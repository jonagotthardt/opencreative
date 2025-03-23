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

import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.OpenCreative;

import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerTotemRespawnEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.settings.Sounds;


import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;

public final class PlayerRespawn implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!DeathListener.deathLocations.containsKey(event.getPlayer())) return;
        Location deathLocation = DeathListener.deathLocations.get(event.getPlayer());
        event.setRespawnLocation(deathLocation);
        Sounds.PLAYER_RESPAWN.play(event.getPlayer());
        DeathListener.deathLocations.remove(event.getPlayer());
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) {
            new ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerRespawnEvent(event.getPlayer()).callEvent();
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
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld((event.getEntity().getWorld()));
        if (event.getEntity() instanceof Player player) {
            if (planet != null) {
                new PlayerTotemRespawnEvent(player).callEvent();
            }
        }

    }
}
