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
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.inventory.ItemStack;


import static mcchickenstudio.creative.utils.ItemUtils.createItem;

public class PlayerRespawn implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!PlayerDeath.deathLocations.containsKey(event.getPlayer())) return;
        Location deathLocation = PlayerDeath.deathLocations.get(event.getPlayer());
        event.setRespawnLocation(deathLocation);
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf("ENTITY_PLAYER_BREATH"),100,2);
        PlayerDeath.deathLocations.remove(event.getPlayer());
        Plot plot = PlotManager.getInstance().getPlotByPlayer(event.getPlayer());
        if (plot != null) {
            EventRaiser.raisePlayerRespawnEvent(event.getPlayer(),event);
            if (plot.isOwner(event.getPlayer())) {
                ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
                if (!event.getPlayer().getInventory().contains(worldSettingsItem)) {
                    event.getPlayer().getInventory().setItem(8,worldSettingsItem);
                }
            }
        }
    }

    @EventHandler
    public void onTotemUsing(EntityResurrectEvent event) {
        Plot plot = PlotManager.getInstance().getPlotByWorld((event.getEntity().getWorld()));
        if (plot != null) EventRaiser.raisePlayerTotemRespawnEvent(event.getEntity(),event);

    }
}
