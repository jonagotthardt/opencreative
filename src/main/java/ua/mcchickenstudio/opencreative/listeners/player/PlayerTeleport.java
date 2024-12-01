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

package ua.mcchickenstudio.opencreative.listeners.player;

import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.plots.PlotManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import ua.mcchickenstudio.opencreative.plots.Plot;


import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.stopPlotCode;

public class PlayerTeleport implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL
                || event.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY
                || event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            event.setCancelled(true);
        }
        Plot plot = PlotManager.getInstance().getPlotByWorld(event.getFrom().getWorld());
        if (plot != null) {
            if (event.getTo().getWorld().equals(event.getFrom().getWorld())) {
                EventRaiser.raiseTeleportEvent(event.getPlayer(),event);
            } else if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPortalTeleport(PlayerPortalEvent event) {
        event.setCancelled(true);
        event.setCanCreatePortal(false);
    }

    @EventHandler
    public void onPortalTeleport(EntityPortalEvent event) {
        event.setCancelled(true);
        event.setCanCreatePortal(false);
    }

    @EventHandler
    public void onPortalTeleport(EntityPortalEnterEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPortalTeleport(EntityInsideBlockEvent event) {
        if (event.getBlock().getType() == Material.END_PORTAL || event.getBlock().getType() == Material.END_GATEWAY) {
            event.setCancelled(true);
        }
    }

}
