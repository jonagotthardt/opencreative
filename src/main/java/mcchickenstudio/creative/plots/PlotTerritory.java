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

package mcchickenstudio.creative.plots;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.CodeScript;
import mcchickenstudio.creative.events.plot.PlotLoadEvent;
import mcchickenstudio.creative.events.plot.PlotUnloadEvent;
import mcchickenstudio.creative.utils.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcchickenstudio.creative.utils.FileUtils.getPlotConfig;
import static mcchickenstudio.creative.utils.FileUtils.getPlotScriptFile;
import static mcchickenstudio.creative.utils.PlayerUtils.getPlayerPlotSize;
import static mcchickenstudio.creative.utils.PlayerUtils.teleportToLobby;

/**
 * <h1>PlotTerritory</h1>
 * This class represents a territory, where plot players can build or play.
 * It stores one-time information that will be removed on world unloading.
 */
public class PlotTerritory {

    public final Plot plot;
    private final CodeScript script;

    private final Map<String, BossBar> bossBars = new HashMap<>();
    private final Map<String, Scoreboard> scoreboards = new HashMap<>();
    private final List<BukkitRunnable> runningBukkitRunnables = new ArrayList<>();

    private World world;
    private int worldSize = 25;
    private World.Environment environment;

    public PlotTerritory(Plot plot) {
        this.plot = plot;
        script = new CodeScript(plot,getPlotScriptFile(plot));
        loadInformation();
    }

    private void loadInformation() {
        worldSize = PlayerUtils.getPlayerLimitValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_SIZE);
        FileConfiguration config = getPlotConfig(plot);
        World.Environment environment = World.Environment.NORMAL;
        if (config != null) {
            if (config.getString("environment") != null) {
                try {
                    environment = World.Environment.valueOf(config.getString("environment"));
                } catch (Exception ignored) {}
            }
        }
        this.environment = environment;
    }

    /*
     * Loads plot, for example if player tries to join it. It loads world folder, world and code script.
     */
    public void load() {
        FileUtils.loadWorldFolder(plot.getWorldName(),true);
        World world = new WorldCreator(plot.getWorldName()).environment(plot.getTerritory().getEnvironment()).keepSpawnLoaded(TriState.FALSE).createWorld();
        if (world == null) return;
        this.world = world;
        world.setAutoSave(true);
        world.setKeepSpawnInMemory(false);
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
            }.runTaskLater(Main.getPlugin(),10L);
        }
        FileUtils.setPlotConfigParameter(plot,"last-activity-time",System.currentTimeMillis());
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
        world.getWorldBorder().setSize(getPlayerPlotSize(plot.getOwnerGroup()));
        plot.getVariables().load();
        new PlotLoadEvent(plot).callEvent();
    }

    /*
     * Unloads plot, for example if no players playing in plot.
     */
    public void unload() {
        FileUtils.setPlotConfigParameter(plot,"last-activity-time",System.currentTimeMillis());
        FileUtils.setPlotConfigParameter(plot,"environment", plot.getTerritory().getEnvironment().name());
        plot.getVariables().save();
        clearData();
        for (Player player : plot.getPlayers()) {
            teleportToLobby(player);
        }
        if (Bukkit.unloadWorld(plot.getWorldName(),true)) {
            FileUtils.unloadWorldFolder(plot.getWorldName(),true);
            if (Bukkit.getWorld(plot.getDevPlot().worldName) != null) {
                for (Player player : plot.getDevPlot().world.getPlayers()) {
                    teleportToLobby(player);
                }
                plot.getDevPlot().setLoaded(false);
                if (Bukkit.unloadWorld(plot.getDevPlot().worldName,true)) {
                    FileUtils.unloadWorldFolder(plot.getDevPlot().worldName,true);
                }
            }
        }
        this.world = null;
        new PlotUnloadEvent(plot).callEvent();
    }

    public void clearData() {
        bossBars.clear();
        scoreboards.clear();
        stopBukkitRunnables();
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
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public int getWorldSize() {
        return worldSize;
    }

    public CodeScript getScript() {
        return script;
    }

    public World generateWorld(WorldUtils.WorldGenerator worldGenerator, World.Environment environment, long seed, boolean generateStructures) {

        WorldCreator worldCreator = new WorldCreator(plot.getWorldName());
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

            setWorld(world);

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
            runnable.runTaskLater(Main.getPlugin(),10L);

            return world;
        }
        return null;
    }
}
