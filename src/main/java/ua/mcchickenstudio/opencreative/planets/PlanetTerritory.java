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

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodeScript;
import ua.mcchickenstudio.opencreative.events.planet.PlanetLoadEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetUnloadEvent;
import ua.mcchickenstudio.opencreative.utils.*;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import ua.mcchickenstudio.opencreative.utils.world.EmptyChunkGenerator;
import ua.mcchickenstudio.opencreative.utils.world.WaterChunkGenerator;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser.raiseQuitEvent;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;

/**
 * <h1>PlanetTerritory</h1>
 * This class represents a territory, where planet players can build or play.
 * It stores one-time information that will be removed on world unloading.
 */
public class PlanetTerritory {

    private final Planet planet;
    private final PlanetFlags flags;

    private final Map<String, BossBar> bossBars = new HashMap<>();
    private final Map<String, Scoreboard> scoreboards = new HashMap<>();
    private final List<BukkitRunnable> runningBukkitRunnables = new ArrayList<>();

    private CodeScript script;
    private int worldSize = 25;
    private World.Environment environment;
    private boolean autoSave = true;

    public PlanetTerritory(Planet planet) {
        this.planet = planet;
        flags = new PlanetFlags(planet);
        script = new CodeScript(planet,getPlanetScriptFile(planet));
        loadInformation();
    }

    /**
     * Sets auto-save option to specified value.
     * @param autoSave true - build world changes will be saved.
     * <p>false - build world changes will be not saved.</p>
     */
    public void setAutoSave(boolean autoSave) {
        if (this.autoSave == autoSave) return;
        this.autoSave = autoSave;
        for (Player planetPlayer : planet.getPlayers()) {
            planetPlayer.sendMessage(getLocaleMessage("settings.autosave." + (autoSave ? "enabled" : "disabled")));
        }
        if (getWorld() != null) {
            getWorld().setAutoSave(autoSave);
        }
        FileUtils.setPlanetConfigParameter(planet,"autosave",!autoSave ? false : null);
    }

    private void loadInformation() {
        worldSize = OpenCreative.getSettings().getGroups().getGroup(planet.getOwnerGroup()).getWorldSize();
        FileConfiguration config = getPlanetConfig(planet);
        World.Environment environment = World.Environment.NORMAL;
        if (config.getString("environment") != null) {
            try {
                environment = World.Environment.valueOf(config.getString("environment"));
            } catch (Exception ignored) {
            }
        }
        autoSave = config.getBoolean("autosave",true);
        this.environment = environment;
    }

    /**
     * Loads planet's files into worlds directory, loads and setups build world, loads script and variables.
     */
    public synchronized void load() {
        loadInformation();
        flags.loadFlags();
        planet.getWorldPlayers().loadPlayers();
        World world = new WorldCreator(planet.getWorldName()).environment(planet.getTerritory().getEnvironment()).keepSpawnLoaded(TriState.FALSE).createWorld();
        if (world == null) return;
        script = new CodeScript(planet,getPlanetScriptFile(planet));
        world.setAutoSave(autoSave);
        world.setGameRule(GameRule.SPAWN_CHUNK_RADIUS,1);
        if (world.getEnvironment() == World.Environment.THE_END) {
            if (world.getEnderDragonBattle() != null) {
                world.getEnderDragonBattle().setPreviouslyKilled(true);
                world.getEnderDragonBattle().getBossBar().setVisible(false);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof EnderDragon dragon) {
                            dragon.setHealth(0);
                        }
                    }
                }
            }.runTaskLater(OpenCreative.getPlugin(),10L);
        }
        FileUtils.setPlanetConfigParameter(planet,"last-activity-time",System.currentTimeMillis());
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
        world.getWorldBorder().setSize(worldSize);
        planet.getVariables().load();
        new PlanetLoadEvent(planet).callEvent();
    }

    /**
     * Saves planet's data and unloads planet's build and dev world.
     */
    public synchronized void unload() {
        for (Player player : planet.getPlayers()) {
            raiseQuitEvent(player);
        }
        FileUtils.setPlanetConfigParameter(planet,"last-activity-time",System.currentTimeMillis());
        FileUtils.setPlanetConfigParameter(planet,"environment", planet.getTerritory().getEnvironment().name());
        planet.getVariables().save();
        for (Player player : planet.getPlayers()) {
            teleportToLobby(player);
        }
        clearData();
        Bukkit.unloadWorld(planet.getWorldName(),autoSave);
        if (planet.getDevPlanet().isLoaded()) {
            for (Player player : planet.getDevPlanet().getWorld().getPlayers()) {
                teleportToLobby(player);
            }
            Bukkit.unloadWorld(planet.getDevPlanet().getWorldName(),true);
        }
        new PlanetUnloadEvent(planet).callEvent();
    }

    public void clearData() {
        stopBukkitRunnables();
        bossBars.clear();
        scoreboards.clear();
        flags.clear();
        script.getExecutors().getExecutorsList().clear();
        planet.getVariables().clearVariables();
        planet.getWorldPlayers().clear();
    }

    public void addBukkitRunnable(BukkitRunnable runnable) {
        runningBukkitRunnables.add(runnable);
    }

    public void removeBukkitRunnable(BukkitRunnable runnable) {
        runningBukkitRunnables.remove(runnable);
    }

    public void stopBukkitRunnables() {
        for (BukkitRunnable runnable : runningBukkitRunnables) {
            try {
                runnable.cancel();
            } catch (IllegalStateException ignored) {}
        }
        runningBukkitRunnables.clear();
    }

    public PlanetFlags getFlags() {
        return flags;
    }

    public Map<String, Scoreboard> getScoreboards() {
        return scoreboards;
    }

    public Map<String, BossBar> getBossBars() {
        return bossBars;
    }

    public World.Environment getEnvironment() {
        return environment;
    }

    public World getWorld() {
        return Bukkit.getWorld(planet.getWorldName());
    }

    public int getWorldSize() {
        return worldSize;
    }

    public CodeScript getScript() {
        return script;
    }

    public World generateWorld(WorldUtils.WorldGenerator worldGenerator, World.Environment environment, long seed, boolean generateStructures) {

        WorldCreator worldCreator = new WorldCreator(planet.getWorldName());
        worldCreator.generateStructures(generateStructures);
        worldCreator.type(WorldType.FLAT);
        worldCreator.environment(environment);
        worldCreator.generateStructures(generateStructures);
        worldCreator.seed(seed);

        switch (worldGenerator) {
            case EMPTY -> worldCreator.generator(new EmptyChunkGenerator());
            case WATER -> worldCreator.generator(new WaterChunkGenerator());
            case SURVIVAL -> worldCreator.type(WorldType.NORMAL);
            case LARGE_BIOMES ->  worldCreator.type(WorldType.LARGE_BIOMES);
        }

        worldCreator.keepSpawnLoaded(TriState.FALSE);
        World world = Bukkit.createWorld(worldCreator);

        if (world != null) {
            world.setAutoSave(true);
            world.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 1);
            world.getWorldBorder().setSize(getWorldSize());

            world.setGameRule(GameRule.DO_MOB_LOOT, true);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.KEEP_INVENTORY, false);
            world.setGameRule(GameRule.MOB_GRIEFING, true);
            world.setGameRule(GameRule.NATURAL_REGENERATION, true);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, true);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

            world.setTime(0);

            if (worldGenerator == WorldUtils.WorldGenerator.EMPTY) {
                world.setSpawnLocation(0, 5, 0);

                for (int x = 1; x >= -1; x--) {
                    for (int z = 1; z >= -1; z--) {
                        world.getBlockAt(x, 4, z).setType(Material.STONE);
                    }
                }

            } else if (worldGenerator == WorldUtils.WorldGenerator.WATER) {
                world.setSpawnLocation(0, 8, 0);
            }

            script = new CodeScript(planet,getPlanetScriptFile(planet));

            for (Entity entity : world.getEntities()) {
                if (entity.getType() != EntityType.PLAYER) entity.remove();
            }

            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof EnderDragon dragon) {
                            dragon.setHealth(0);
                        }
                    }
                }
            };
            runnable.runTaskLater(OpenCreative.getPlugin(),10L);

            return world;
        }
        return null;
    }

    public boolean isAutoSave() {
        return autoSave;
    }
}
