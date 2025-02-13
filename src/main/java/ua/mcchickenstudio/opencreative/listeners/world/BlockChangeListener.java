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

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.*;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
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
        }
        if (isDevPlanet(world)) {
            event.blockList().clear();
        }
    }

}
