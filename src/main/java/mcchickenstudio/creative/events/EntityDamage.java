/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package mcchickenstudio.creative.events;

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



public class EntityDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player) {

            Player victim = ((Player) event.getEntity()).getPlayer();

            if (victim != null) {

                Plot plot = PlotManager.getInstance().getPlotByPlayer(victim);
                if (plot != null) {
                    if (plot.plotMode == Plot.Mode.BUILD) event.setCancelled(true);

                    if (victim.getLocation().distance(victim.getWorld().getSpawnLocation()) < 5) event.setCancelled(true);

                    byte playerDamageFlag = plot.getFlagValue(PlotFlags.PlotFlag.PLAYER_DAMAGE);
                    if (playerDamageFlag == 2) event.setCancelled(true);
                    if (playerDamageFlag == 3 && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) event.setCancelled(true);
                    if (playerDamageFlag == 4 && event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
                    if (playerDamageFlag == 5 && (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.FALL)) event.setCancelled(true);
                    if (event.getCause() == EntityDamageEvent.DamageCause.VOID) ((Player) event.getEntity()).setHealth(0);

                    EventRaiser.raisePlayerDamagedEvent(victim,event);

                } else if (victim.getWorld().getName().equalsIgnoreCase("world")) {
                    event.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!(event.getDamager() instanceof Player)) {
                Plot plot = PlotManager.getInstance().getPlotByPlayer((Player) event.getEntity());
                if (plot != null) {
                    EventRaiser.raiseMobDamagesPlayerEvent((Player) event.getEntity(),event);;
                }
            }
        }
        if (!(event.getEntity() instanceof Player)) {
            if (event.getDamager() instanceof Player) {
                Plot plot = PlotManager.getInstance().getPlotByPlayer((Player) event.getDamager());
                if (plot != null) {
                    EventRaiser.raisePlayerDamagedMobEvent((Player) event.getDamager(),event);
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
