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

package mcchickenstudio.creative.listeners.entity;

import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.plots.PlotFlags;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;

import static mcchickenstudio.creative.utils.PlayerUtils.isEntityInLobby;

public class EntityDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player victim) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(victim);
            if (plot != null) {
                if (plot.getMode() == Plot.Mode.BUILD) {
                    event.setCancelled(true);
                    if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        victim.teleport(victim.getWorld().getSpawnLocation().add(0,0.5,0));
                    }
                }
                if (PlotManager.getInstance().getDevPlot(victim) != null) {
                    event.setCancelled(true);
                }

                if (victim.getLocation().distance(victim.getWorld().getSpawnLocation()) < 5) event.setCancelled(true);

                byte playerDamageFlag = plot.getFlagValue(PlotFlags.PlotFlag.PLAYER_DAMAGE);
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
                Plot plot = PlotManager.getInstance().getPlotByPlayer(damager);
                if (plot != null) {
                    EventRaiser.raisePlayerDamagesPlayerEvent(damager,victim,event);
                }
            // Mob damages player
            } else {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(victim);
                if (plot != null) {
                    EventRaiser.raiseMobDamagesPlayerEvent(victim,event);
                }
            }
        } else {
            // Player damages mob
            if (event.getDamager() instanceof Player damager) {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(damager);
                if (plot != null) {
                    EventRaiser.raisePlayerDamagedMobEvent(damager,event);
                }
            }
        }
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer((Player) event.getEntity());
        if (plot != null)  EventRaiser.raiseHungerChangeEvent((Player) event.getEntity(),event);
    }
}
