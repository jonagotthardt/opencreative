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

import static mcchickenstudio.creative.utils.FileUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.getPlayerPlotSize;
import static mcchickenstudio.creative.utils.PlayerUtils.teleportToLobby;

/**
 * <h1>PlotTerritory</h1>
 * This class represents a territory, where plot players can build or play.
 * It stores one-time information that will be removed on world unloading.
 */
public class PlotTerritory {

    private final Plot plot;

    private final Map<String, BossBar> bossBars = new HashMap<>();
    private final Map<String, Scoreboard> scoreboards = new HashMap<>();
    private final List<BukkitRunnable> runningBukkitRunnables = new ArrayList<>();

    private World world;
    private CodeScript script;
    private int worldSize = 25;
    private World.Environment environment;
    private boolean autoSave = true;

    public PlotTerritory(Plot plot) {
        this.plot = plot;
        script = new CodeScript(plot,getPlotScriptFile(plot));
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
        for (Player plotPlayer : plot.getPlayers()) {
            plotPlayer.sendMessage(getLocaleMessage("settings.autosave." + (autoSave ? "enabled" : "disabled")));
        }
        if (world != null) {
            world.setAutoSave(autoSave);
        }
        FileUtils.setPlotConfigParameter(plot,"autosave",autoSave);
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
            autoSave = config.getBoolean("autosave",true);
        }
        this.environment = environment;
    }

    /*
     * Loads plot, for example if player tries to join it. It loads world folder, world and code script.
     */
    public void load() {
        loadInformation();
        FileUtils.loadWorldFolder(plot.getWorldName(),true);
        World world = new WorldCreator(plot.getWorldName()).environment(plot.getTerritory().getEnvironment()).keepSpawnLoaded(TriState.FALSE).createWorld();
        if (world == null) return;
        this.world = world;
        script = new CodeScript(plot,getPlotScriptFile(plot));
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
        for (Player player : plot.getPlayers()) {
            teleportToLobby(player);
        }
        clearData();
        if (Bukkit.unloadWorld(plot.getWorldName(),autoSave)) {
            FileUtils.unloadWorldFolder(plot.getWorldName(),true);
            if (Bukkit.getWorld(plot.getDevPlot().getWorldName()) != null) {
                for (Player player : plot.getDevPlot().world.getPlayers()) {
                    teleportToLobby(player);
                }
                if (Bukkit.unloadWorld(plot.getDevPlot().getWorldName(),true)) {
                    FileUtils.unloadWorldFolder(plot.getDevPlot().getWorldName(),true);
                }
            }
        }
        this.world = null;
        new PlotUnloadEvent(plot).callEvent();
    }

    public void clearData() {
        stopBukkitRunnables();
        bossBars.clear();
        scoreboards.clear();
        script.getExecutors().getExecutorsList().clear();
        plot.getVariables().clearVariables();
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

            this.world = world;
            script = new CodeScript(plot,getPlotScriptFile(plot));

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

    public boolean isAutoSave() {
        return autoSave;
    }
}
