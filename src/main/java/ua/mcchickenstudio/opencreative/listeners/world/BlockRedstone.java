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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.sendMessageOnce;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;

public class BlockRedstone implements Listener {


    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Location location = event.getBlock().getLocation();

        Planet planet = PlanetManager.getInstance().getPlanetByWorld(location.getWorld());
        if (planet != null) {
            planet.getLimits().setLastRedstoneOperationsAmount(planet.getLimits().getLastRedstoneOperationsAmount()+1);
            if (planet.getLimits().getLastRedstoneOperationsAmount() > planet.getLimits().getRedstoneOperationsLimit()) {
                    sendMessageOnce(planet,getLocaleMessage("world.redstone-limit").replace("%count%",String.valueOf(planet.getLimits().getRedstoneOperationsLimit())),5);
                    if (location.getBlock().getType() == Material.OBSERVER) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                location.getBlock().setType(Material.AIR);
                            }
                        }.runTaskLater(OpenCreative.getPlugin(),1L);
                    } else {
                        location.getBlock().setType(Material.CAVE_AIR);
                    }
                    planet.getLimits().setLastRedstoneOperationsAmount(0);
            }
            if (planet.getLimits().getLastRedstoneOperationsAmount() > 0) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        planet.getLimits().setLastRedstoneOperationsAmount(planet.getLimits().getLastRedstoneOperationsAmount()-1);
                    }
                }.runTaskLater(OpenCreative.getPlugin(),5L);
            }
        }

    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
        if (isDevPlanet(event.getBlock().getWorld())) {
            event.setCancelled(true);
        }
    }

}
