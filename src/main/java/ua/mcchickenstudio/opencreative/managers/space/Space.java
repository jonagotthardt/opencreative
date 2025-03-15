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

package ua.mcchickenstudio.opencreative.managers.space;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.events.planet.PlanetDeletionEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetRegisterEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetSharingChangeEvent;
import ua.mcchickenstudio.opencreative.menus.world.WorldMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.*;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;

import java.time.Duration;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isPlanet;

public class Space implements PlanetsManager {

    private final Set<Planet> planets = new HashSet<>();
    private final Set<Planet> corruptedPlanets = new HashSet<>();
    
    @Override
    public @NotNull Set<Planet> getPlanets() {
        return planets;
    }

    @Override
    public @NotNull Set<Planet> getCorruptedPlanets() {
        return corruptedPlanets;
    }

    @Override
    public void registerPlanet(@NotNull Planet planet) {
        if (planet.isCorrupted()) {
            corruptedPlanets.add(planet);
        } else {
            planets.add(planet);
        }
        new PlanetRegisterEvent(planet).callEvent();
    }

    @Override
    public void unregisterPlanet(@NotNull Planet planet) {
        planets.remove(planet);
        corruptedPlanets.remove(planet);
    }

    @Override
    public void createPlanet(@NotNull Player owner, int id, WorldUtils.@NotNull WorldGenerator generator) {
        createPlanet(owner,id,generator, World.Environment.NORMAL,new Random().nextInt(),false);
    }

    @Override
    public void createPlanet(@NotNull Player owner, int id, WorldUtils.@NotNull WorldGenerator generator, World.@NotNull Environment environment, long seed, boolean generateStructures) {
        owner.showTitle(Title.title(
                toComponent(getLocaleMessage("creating-world.title")), toComponent(getLocaleMessage("creating-world.subtitle")),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(30), Duration.ofSeconds(2))
        ));
        OpenCreative.getPlugin().getLogger().info("Creating new planet " + id + " by " + owner.getName() + "...");

        createWorldSettings(id, owner, environment);
        Planet planet = new Planet(id);

        if (planet.getTerritory().generateWorld(generator,environment,seed,generateStructures) != null) {
            planet.connectPlayer(owner);
        } else {
            sendPlayerErrorMessage(owner,"Failed to create world, world is null.");
        }

    }

    public @NotNull Set<Planet> getPlanetsByOwner(@NotNull Player player) {
        return getPlanetsByOwner(player.getName());
    }

    @Override
    public void deletePlanet(@NotNull Planet planet) {
        OpenCreative.getPlugin().getLogger().info("Deleting planet " + planet.getId());
        new PlanetDeletionEvent(planet).callEvent();
        try {
            for (Player p : planet.getPlayers()) {
                PlayerUtils.teleportToLobby(p);
                if (p.getOpenInventory().getTopInventory().getHolder() instanceof WorldMenu) {
                    p.closeInventory();
                }
            }
            PlanetSharingChangeEvent planetEvent = new PlanetSharingChangeEvent(planet, planet.getSharing(), Planet.Sharing.PUBLIC);
            planetEvent.callEvent();
            if (!planetEvent.isCancelled()) {
                planet.setSharing(Planet.Sharing.CLOSED);
            }
            planets.remove(planet);
            if (planet.isLoaded()) {
                Bukkit.unloadWorld(planet.getWorldName(),false);
                if (planet.getDevPlanet().isLoaded()) {
                    Bukkit.unloadWorld(planet.getDevPlanet().getWorldName(),false);
                }
            }
            FileUtils.deleteFolder(FileUtils.getPlanetFolder(planet));
            FileUtils.deleteFolder(FileUtils.getDevPlanetFolder(planet.getDevPlanet()));
        } catch (Exception error) {
            ErrorUtils.sendCriticalErrorMessage("Error while deleting world " + planet.getId(), error);
        }
    }

    @Override
    public @NotNull List<Planet> getRecommendedPlanets() {
        List<Planet> featuredPlanets = new ArrayList<>();
        Set<Integer> featuredIds = OpenCreative.getSettings().getRecommendedWorldsIDs();
        for (int id : featuredIds) {
            Planet planet = getPlanetById(String.valueOf(id));
            if (planet != null) {
                featuredPlanets.add(planet);
            }
        }
        return featuredPlanets;
    }

    public @NotNull Set<Planet> getPlanetsByPlanetName(@NotNull String worldName) {
        Set<Planet> foundPlanets = new HashSet<>();
        for (Planet planet : planets) {
            if (planet.getInformation().getDisplayName().toLowerCase().contains(worldName.toLowerCase())) {
                foundPlanets.add(planet);
            }
        }
        return foundPlanets;
    }

    @Override
    public @NotNull Set<Planet> getPlanetsByID(@NotNull String worldID) {
        Set<Planet> foundPlanets = new HashSet<>();
        for (Planet planet : planets) {
            if (planet.getInformation().getCustomID().toLowerCase().contains(worldID.toLowerCase())) {
                foundPlanets.add(planet);
            }
        }
        return foundPlanets;
    }

    @Override
    public @NotNull Set<Planet> getPlanetsByOwner(@NotNull String owner) {
        Set<Planet> foundPlanets = new HashSet<>();
        for (Planet planet : planets) {
            if (planet.isOwner(owner)) {
                foundPlanets.add(planet);
            }
        }
        return foundPlanets;
    }

    @Override
    public Planet getPlanetByPlayer(@NotNull Player player) {
        World world = player.getWorld();
        if (!isPlanet(world)) return null;
        String id = world.getName()
                .replace("./planets/planet","")
                .replace("dev","");
        for (Planet planet : planets) {
            if (id.equals(String.valueOf(planet.getId()))) {
                return planet;
            }
        }
        return null;
    }

    @Override
    public DevPlanet getDevPlanet(@NotNull Player player) {
        if (!isDevPlanet(player.getWorld())) return null;
        Planet planet = getPlanetByPlayer(player);
        return planet != null ? planet.getDevPlanet() : null;
    }

    @Override
    public DevPlanet getDevPlanet(@NotNull World world) {
        if (!isDevPlanet(world)) return null;
        for (Planet planet : planets) {
            if (world.equals(planet.getDevPlanet().getWorld())) {
                return planet.getDevPlanet();
            }
        }
        return null;
    }

    @Override
    public Planet getPlanetByWorld(@NotNull World world) {
        if (!isPlanet(world) && !isDevPlanet(world)) return null;
        String id = world.getName()
                .replace("./planets/planet","")
                .replace("dev","");
        for (Planet planet : planets) {
            if (id.equals(String.valueOf(planet.getId()))) {
                return planet;
            }
        }
        return null;
    }

    @Override
    public Planet getPlanetByWorldName(@NotNull String worldName) {
        for (Planet planet : planets) {
            if (planet.getWorldName().equalsIgnoreCase(worldName)) {
                return planet;
            }
        }
        return null;
    }

    @Override
    public Planet getPlanetById(@NotNull String id) {
        for (Planet planet : planets) {
            if (id.equalsIgnoreCase(String.valueOf(planet.getId()))) {
                return planet;
            }
        }
        return null;
    }

    @Override
    public Planet getPlanetByCustomID(@NotNull String customID) {
        for (Planet planet : planets) {
            if (planet.getInformation().getCustomID().equalsIgnoreCase(customID)) {
                return planet;
            }
        }
        return null;
    }

    @Override
    public void init() {}

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Planet Manager";
    }
}
