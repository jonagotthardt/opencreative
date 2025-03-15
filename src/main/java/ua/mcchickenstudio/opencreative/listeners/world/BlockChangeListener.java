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

package ua.mcchickenstudio.opencreative.listeners.world;

import io.papermc.paper.event.block.BeaconActivatedEvent;
import io.papermc.paper.event.block.BeaconDeactivatedEvent;
import org.bukkit.event.block.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.planets.*;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;

public final class BlockChangeListener implements Listener {

    @EventHandler
    public void onBlockChanged(BlockFadeEvent event) {
        World world = event.getBlock().getWorld();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(world);
        if (planet != null) {
            if (planet.getFlagValue(PlanetFlags.PlanetFlag.BLOCK_CHANGING) == 2) {
                event.setCancelled(true);
            } else {
                EventRaiser.raiseBlockFadedEvent(planet,event);
            }
        }
    }

    @EventHandler
    public void onBlockChanged(BlockFormEvent event) {
        World world = event.getBlock().getWorld();
        if (isDevPlanet(world)) {
            DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(world);
            if (devPlanet != null) {
                DevPlatform platform = devPlanet.getPlatformInLocation(event.getBlock().getLocation());
                if (platform == null) return;
                if (platform.getEventMaterial() == event.getBlock().getRelative(BlockFace.DOWN).getType() || platform.getActionMaterial() == event.getBlock().getRelative(BlockFace.DOWN).getType()) {
                    event.setCancelled(true);
                }
            }
        } else {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(world);
            if (planet != null) {
                if (planet.getFlagValue(PlanetFlags.PlanetFlag.BLOCK_CHANGING) == 2) {
                    event.setCancelled(true);
                } else {
                    EventRaiser.raiseBlockFormedEvent(planet,event);
                }
            }
        }


    }

    @EventHandler
    public void onEntityExplosion(EntityExplodeEvent event) {
        World world = event.getLocation().getWorld();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(world);
        if (planet != null) {
            if (planet.getFlagValue(PlanetFlags.PlanetFlag.BLOCK_EXPLOSION) == 2) {
                event.blockList().clear();
            }
        }
        if (isDevPlanet(world)) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onBlockExplosion(BlockExplodeEvent event) {
        World world = event.getBlock().getLocation().getWorld();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(world);
        if (planet != null) {
            if (planet.getFlagValue(PlanetFlags.PlanetFlag.BLOCK_EXPLOSION) == 2) {
                event.blockList().clear();
            }
            EventRaiser.raiseBlockExplodedEvent(planet,event);
        }
        if (isDevPlanet(world)) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onBeacon(BeaconActivatedEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) EventRaiser.raiseBeaconActivatedEvent(planet,event);
    }

    @EventHandler
    public void onBeacon(BeaconDeactivatedEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) EventRaiser.raiseBeaconDeactivatedEvent(planet,event);
    }

    @EventHandler
    public void onPortal(PortalCreateEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getWorld());
        if (planet != null) EventRaiser.raisePortalCreatedEvent(planet,event);
    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) EventRaiser.raiseBlockPistonExtendedEvent(planet,event);
    }

    @EventHandler
    public void onPiston(BlockPistonRetractEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) EventRaiser.raiseBlockPistonRetractedEvent(planet,event);
    }

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) EventRaiser.raiseBlockPhysicsEvent(planet,event);
    }

    @EventHandler
    public void onBlock(BlockGrowEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) EventRaiser.raiseBlockGrownEvent(planet,event);
    }

    @EventHandler
    public void onBlock(BlockIgniteEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) EventRaiser.raiseBlockIgnitedEvent(planet,event);
    }

    @EventHandler
    public void onBlock(TNTPrimeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) EventRaiser.raiseBlockTntPrimeEvent(planet,event);
    }

    @EventHandler
    public void onBlock(BlockDispenseEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) EventRaiser.raiseBlockDispensedEvent(planet,event);
    }

    @EventHandler
    public void onBlock(BlockBurnEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) EventRaiser.raiseBlockBurnedEvent(planet,event);
    }

    @EventHandler
    public void onBlock(BlockCookEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) EventRaiser.raiseBlockCookedEvent(planet,event);
    }

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getWorld());
        if (planet != null) EventRaiser.raiseLightningStrikeEvent(planet,event);
    }

}
