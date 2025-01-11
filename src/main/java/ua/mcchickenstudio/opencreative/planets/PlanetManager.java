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

package ua.mcchickenstudio.opencreative.planets;

import ua.mcchickenstudio.opencreative.events.planet.PlanetDeletionEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetRegisterEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetSharingChangeEvent;
import ua.mcchickenstudio.opencreative.utils.*;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.OpenCreative;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;

import java.time.Duration;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;

public class PlanetManager {

    private static PlanetManager planetManager;

    private PlanetManager() {}
    public static PlanetManager getInstance() {
        if (planetManager == null) {
            planetManager = new PlanetManager();
        }
        return planetManager;
    }

    private final Set<Planet> planets = new HashSet<>();
    private final Set<Planet> corruptedPlanets = new HashSet<>();

    public Set<Planet> getPlanets() {
        return planets;
    }

    public Set<Planet> getCorruptedPlanets() {
        return corruptedPlanets;
    }

    public void registerPlanet(Planet planet) {
        if (planet.isCorrupted()) {
            corruptedPlanets.add(planet);
        } else {
            planets.add(planet);
        }
        new PlanetRegisterEvent(planet).callEvent();
    }

    /**
     * Creates and loads a new planet for player with specified world generator.
     * @param owner Owner of planet.
     * @param id Id of planet.
     * @param generator Generator of world.
     */
    public void createPlanet(Player owner, int id, WorldUtils.WorldGenerator generator) {
        createPlanet(owner,id,generator, World.Environment.NORMAL,new Random().nextInt(),false);
    }

    /**
     * Creates and loads a new planet for player with specified world generator, environment, seed and generate sturctures option.
     * @param owner Owner of planet.
     * @param id Id of planet.
     * @param generator Generator of world.
     * @param environment Environment of world.
     * @param seed Seed for generation.
     * @param generateStructures Generate or not generate structures.
     */
    public void createPlanet(Player owner, int id, WorldUtils.WorldGenerator generator, World.Environment environment, long seed, boolean generateStructures) {
        owner.showTitle(Title.title(
                toComponent(getLocaleMessage("creating-world.title")), toComponent(getLocaleMessage("creating-world.subtitle")),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(30), Duration.ofSeconds(2))
        ));
        OpenCreative.getPlugin().getLogger().info("Creating new planet " + id + " by " + owner.getName() + "...");

        createWorldSettings(id, owner, environment);
        Planet planet = new Planet(id);

        if (planet.getTerritory().generateWorld(generator,environment,seed,generateStructures) != null) {
            planet.connectPlayer(owner);
            planet.getTerritory().getWorld().getSpawnLocation().getChunk().load(true);
            owner.showTitle(Title.title(
                    toComponent(getLocaleMessage("creating-world.welcome-title",owner)), toComponent(getLocaleMessage("creating-world.welcome-subtitle",owner)),
                    Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(9), Duration.ofSeconds(2))
            ));
            owner.sendMessage(getLocaleMessage("creating-world.welcome"));
            owner.playSound(owner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE,100,0.1f);
            owner.setGameMode(GameMode.CREATIVE);
            ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
            owner.getInventory().setItem(8,worldSettingsItem);
        } else {
            sendPlayerErrorMessage(owner,"Failed to create world, world is null.");
        }

    }

    public void clearPlanets() {
        planets.clear();
    }

    /**
     Returns planets, these player owns.
     **/
    public List<Planet> getPlayerPlanets(Player player) {
        List<Planet> playerPlanets = new ArrayList<>();
        for (Planet planet : PlanetManager.getInstance().getPlanets()) {
            if (planet.getOwner().equalsIgnoreCase(player.getName())) {
                playerPlanets.add(planet);
            }
        }
        return playerPlanets;
    }

    /**
     Delete planet on player request. It teleports planet players to spawn, closes planet, unloads world and deletes world folder.
     **/
    public void deletePlanet(Planet planet, Player player) {
        new PlanetDeletionEvent(planet).callEvent();
        try {
            for (Player p : planet.getPlayers()) {
                PlayerUtils.teleportToLobby(p);
            }
            PlanetSharingChangeEvent planetEvent = new PlanetSharingChangeEvent(planet, planet.getSharing(), Planet.Sharing.PUBLIC);
            planetEvent.callEvent();
            if (!planetEvent.isCancelled()) {
                planet.setSharing(Planet.Sharing.CLOSED);
            }
            planets.remove(planet);
            FileUtils.deleteFolder(FileUtils.getPlanetFolder(planet));
            FileUtils.deleteFolder(FileUtils.getDevPlanetFolder(planet.getDevPlanet()));
            Bukkit.getServer().getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
                Bukkit.unloadWorld(planet.getWorldName(),false);
                player.sendMessage(MessageUtils.getLocaleMessage("deleting-world.message"));
            }, 60);
        } catch (NullPointerException error) {
            ErrorUtils.sendCriticalErrorMessage("Error while deleting world " + planet.getId(), error);
        }
    }

    public List<Planet> getRecommendedPlanets() {
        List<Planet> featuredPlanets = new ArrayList<>();
        Set<Integer> featuredIds = OpenCreative.getSettings().getRecommendedWorldsIDs();
        for (int id : featuredIds) {
            Planet planet = getPlanetByWorldName("planet"+id);
            if (planet != null) {
                featuredPlanets.add(planet);
            }
        }
        return featuredPlanets;
    }


    /**
     Returns planets that contains specified name.
     **/
    public Set<Planet> getPlanetsByPlanetName(String worldName) {
        Set<Planet> foundPlanets = new HashSet<>();
        for (Planet planet : planets) {
            if (planet.getInformation().getDisplayName().toLowerCase().contains(worldName.toLowerCase())) {
                foundPlanets.add(planet);
            }
        }
        return foundPlanets;
    }

    /**
     Returns planets that contains specified ID.
     **/
    public Set<Planet> getPlanetsByID(String worldID) {
        Set<Planet> foundPlanets = new HashSet<>();
        for (Planet planet : planets) {
            if (planet.getInformation().getCustomID().toLowerCase().contains(worldID.toLowerCase())) {
                foundPlanets.add(planet);
            }
        }
        return foundPlanets;
    }

    /**
     Returns planets that are owned by specified player.
     **/
    public Set<Planet> getPlanetsByOwner(String owner) {
        Set<Planet> foundPlanets = new HashSet<>();
        for (Planet planet : planets) {
            if (planet.isOwner(owner)) {
                foundPlanets.add(planet);
            }
        }
        return foundPlanets;
    }

    /**
     Returns planets that has specified category.
     **/
    public Set<Planet> getPlanetsByCategory(PlanetInfo.Category category) {
        Set<Planet> foundPlanets = new HashSet<>();
        for (Planet planet : planets) {
            if (planet.getInformation().getCategory() == category) {
                foundPlanets.add(planet);
            }
        }
        return foundPlanets;
    }

    /**
     Returns planet where specified player in.
     **/
    public Planet getPlanetByPlayer(Player player) {
        for (Planet planet : planets) {
            if (planet.getPlayers().contains(player)) {
                return planet;
            }
        }
        return null;
    }

    /**
     Returns developer planet where specified player in.
     **/
    public DevPlanet getDevPlanet(Player player) {
        for (Planet planet : planets) {
            if (planet.getDevPlanet() != null && planet.getDevPlanet().getWorld() != null) {
                if (planet.getPlayers().contains(player)) {
                    if (planet.getDevPlanet().getWorld().getPlayers().contains(player)) {
                        return planet.getDevPlanet();
                    }
                }
            }
        }
        return null;
    }

    public DevPlanet getDevPlanet(World world) {
        for (Planet planet : planets) {
            if (planet.getDevPlanet() != null && world.equals(planet.getDevPlanet().getWorld())) {
                return planet.getDevPlanet();
            }
        }
        return null;
    }

    /**
     Returns planet that has same specified world.
     **/
    public Planet getPlanetByWorld(World world) {
        for (Planet planet : planets) {
            if (world.equals(planet.getTerritory().getWorld())) {
                return planet;
            }
            if (world.equals(planet.getDevPlanet().getWorld())) {
                return planet;
            }
        }
        return null;
    }

    /**
     Returns planet that has same specified world name (planet60, planet21)).
     **/
    public Planet getPlanetByWorldName(String worldName) {
        for (Planet planet : planets) {
            if (planet.getWorldName().equalsIgnoreCase(worldName)) {
                return planet;
            }
        }
        return null;
    }

    /**
     Returns planet that has same specified ID.
     **/
    public Planet getPlanetById(String id) {
        for (Planet planet : planets) {
            if (id.equalsIgnoreCase(String.valueOf(planet.getId()))) {
                return planet;
            }
        }
        return null;
    }

    /**
     Returns planet that has same specified ID.
     **/
    public Planet getPlanetByCustomID(String customID) {
        for (Planet planet : planets) {
            if (planet.getInformation().getCustomID().equalsIgnoreCase(customID)) {
                return planet;
            }
        }
        return null;
    }

}
