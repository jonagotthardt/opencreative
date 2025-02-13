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

package ua.mcchickenstudio.opencreative.listeners.entity;

import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetFlags;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInLobby;

public final class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player victim) {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(victim);
            if (planet != null) {
                if (planet.getMode() == Planet.Mode.BUILD) {
                    event.setCancelled(true);
                    if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        BukkitRunnable runnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (planet.isLoaded() && planet.getTerritory().getWorld().equals(victim.getWorld()) && planet.getMode() == Planet.Mode.BUILD) {
                                    victim.teleport(victim.getWorld().getSpawnLocation().add(0,0.5,0));
                                }
                                planet.getTerritory().removeBukkitRunnable(this);
                            }
                        };
                        planet.getTerritory().addBukkitRunnable(runnable);
                        runnable.runTaskLater(OpenCreative.getPlugin(),1L);
                    }
                }
                if (OpenCreative.getPlanetsManager().getDevPlanet(victim) != null) {
                    event.setCancelled(true);
                }

                if (victim.getLocation().distance(victim.getWorld().getSpawnLocation()) < 5) event.setCancelled(true);

                byte playerDamageFlag = planet.getFlagValue(PlanetFlags.PlanetFlag.PLAYER_DAMAGE);
                if (playerDamageFlag == 2) event.setCancelled(true);
                if (playerDamageFlag == 3 && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) event.setCancelled(true);
                if (playerDamageFlag == 4 && event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
                if (playerDamageFlag == 5 && (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.FALL)) event.setCancelled(true);

                EventRaiser.raisePlayerDamagedEvent(victim,event);

            } else if (isEntityInLobby(victim)) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof Player victim) {
            // Player damages player
            if (event.getDamager() instanceof Player damager) {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(damager);
                if (planet != null) {
                    EventRaiser.raisePlayerDamagesPlayerEvent(damager,victim,event);
                }
            // Mob damages player
            } else {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(victim);
                if (planet != null) {
                    EventRaiser.raiseMobDamagesPlayerEvent(victim,event);
                }
            }
        } else {
            // Player damages mob
            if (event.getDamager() instanceof Player damager) {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(damager);
                if (planet != null) {
                    EventRaiser.raisePlayerDamagedMobEvent(damager,event);
                }
            }
        }
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer((Player) event.getEntity());
        if (isEntityInLobby(event.getEntity())) {
            event.setCancelled(true);
        }
        if (planet != null) {
            if (planet.getMode() == Planet.Mode.BUILD) {
                event.setCancelled(true);
            } else {
                EventRaiser.raiseHungerChangeEvent((Player) event.getEntity(),event);
            }
        }
    }
}
