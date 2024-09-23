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

package mcchickenstudio.creative.events.entity;

import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.plots.PlotFlags;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import mcchickenstudio.creative.plots.Plot;

import mcchickenstudio.creative.plots.PlotManager;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.MessageUtils.sendMessageOnce;
import static mcchickenstudio.creative.utils.PlayerUtils.isEntityInDevPlot;
import static mcchickenstudio.creative.utils.WorldUtils.isEntityHostile;

public class EntitySpawn implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        World world = event.getLocation().getWorld();
        Plot plot = PlotManager.getInstance().getPlotByWorld(world);
        if (plot != null) {
            int limit = plot.getEntitiesLimit();
            int count = plot.world.getEntityCount();
            if (world.getName().contains("dev")) {
                if (!(event.getEntity() instanceof Item)) {
                    event.setCancelled(true);
                }
            }
            if (plot.devPlot != null && plot.devPlot.world != null) {
                count += plot.devPlot.world.getEntityCount();
            }
            if (count > limit) {
                event.setCancelled(true);
                if (plot.getOnline() < 1) return;
                TextComponent warning = new TextComponent(getLocaleMessage("world.entity-limit").replace("%count%",String.valueOf(limit)));
                warning.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getLocaleMessage("world.entity-limit-hover"))));
                warning.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world deletemobs"));
                sendMessageOnce(plot,warning,3);
            } else {
                EventRaiser.raiseEntitySpawnEvent(event);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityPlace(EntityPlaceEvent event) {
        World world = event.getBlock().getWorld();
        Plot plot = PlotManager.getInstance().getPlotByWorld(world);
        if (plot != null) {
            int limit = plot.getEntitiesLimit();
            if (world.getEntityCount() > limit) {
                event.setCancelled(true);
                if (plot.getOnline() < 1) return;
                TextComponent warning = new TextComponent(getLocaleMessage("world.entity-limit").replace("%count%",String.valueOf(limit)));
                warning.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getLocaleMessage("world.entity-limit-hover"))));
                warning.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world deletemobs"));
                sendMessageOnce(plot,warning,3);
            }
        }
        String worldName = world.getName();
        if (worldName.contains("dev")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        World world = event.getEntity().getWorld();
        Entity entity = event.getEntity();
        Plot plot = PlotManager.getInstance().getPlotByWorld(world);
        if (isEntityInDevPlot(entity) && !(event.getEntity() instanceof Item)) {
            event.setCancelled(true);
        }
        if (plot != null) {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
                switch (plot.getFlagValue(PlotFlags.PlotFlag.MOB_SPAWN)) {
                    case 3:
                        if (entity instanceof Slime) {
                            event.setCancelled(true);
                        }
                        break;
                    case 4:
                        if (isEntityHostile(entity.getType())) {
                            event.setCancelled(true);
                        }
                        break;
                    case 5:
                        if (!isEntityHostile(entity.getType())) {
                            event.setCancelled(true);
                        }
                        break;
                }
                if (world.getEntityCount() >= plot.getEntitiesLimit() /2) {
                    event.setCancelled(true);
                }
            }
            if (plot.getEnvironment() == World.Environment.THE_END) {
                if (event.getEntity() instanceof EnderDragon dragon) {
                    dragon.setHealth(0);
                }
            }

        }
    }

    @EventHandler
    public void onVehicleCreation(VehicleCreateEvent event) {
        Entity entity = event.getVehicle();
        if (isEntityInDevPlot(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        World world = event.getWorld();
        Plot plot = PlotManager.getInstance().getPlotByWorld(world);
        if (plot != null && event.isNewChunk()) {
            if (world.getEntityCount() >= plot.getEntitiesLimit() /2) {
                for (Entity entity : event.getChunk().getEntities()) {
                    entity.remove();
                }
            }
        }
    }


}
