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

package mcchickenstudio.creative.utils;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.plots.Plot;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Random;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.PlayerUtils.clearPlayer;
import static mcchickenstudio.creative.utils.FileUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class WorldUtils {

    public enum WorldGenerator {

        FLAT,
        EMPTY,
        WATER,
        SURVIVAL,
        LARGE_BIOMES,

    }

    public static boolean isDevPlot(World world) {
        return world.getName().endsWith("dev");
    }

    /**
     Create a world for plot. It creates world, plot's settings.yml, sets plot parameters, teleports player.
     **/
    public static boolean generateWorld(Plot plot, Player player, String worldName, WorldGenerator worldGenerator, World.Environment environment, long seed, boolean generateStructures) {

        WorldCreator worldCreator = new WorldCreator(worldName).generateStructures(false);
        worldCreator.type(WorldType.FLAT);
        worldCreator.environment(environment);
        worldCreator.generateStructures(generateStructures);
        worldCreator.seed(seed);

        if (worldGenerator == WorldGenerator.EMPTY) {
            worldCreator.generator(new EmptyChunkGenerator());
        } else if (worldGenerator == WorldGenerator.WATER) {
            worldCreator.generator(new WaterChunkGenerator());
        } else if (worldGenerator == WorldGenerator.SURVIVAL) {
            worldCreator.type(WorldType.NORMAL);
        } else if (worldGenerator == WorldGenerator.LARGE_BIOMES) {
            worldCreator.type(WorldType.LARGE_BIOMES);
        }

        worldCreator.keepSpawnLoaded(TriState.FALSE);
        World world = Bukkit.createWorld(worldCreator);

        if (world != null) {
            world.setGameRule(GameRule.SPAWN_CHUNK_RADIUS,1);
            world.getWorldBorder().setSize(plot.getWorldSize());

            world.setGameRule(GameRule.DO_MOB_LOOT,true);
            world.setGameRule(GameRule.DO_MOB_SPAWNING,false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE,false);
            world.setGameRule(GameRule.KEEP_INVENTORY,false);
            world.setGameRule(GameRule.MOB_GRIEFING,true);
            world.setGameRule(GameRule.NATURAL_REGENERATION,true);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN,false);
            world.setGameRule(GameRule.DO_FIRE_TICK,true);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES,false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);

            world.setTime(0);

            if (worldGenerator == WorldGenerator.EMPTY) {
                world.setSpawnLocation(0,5,0);

                for (int x = 1; x >= -1; x--) {
                    for (int z = 1; z >= -1; z--) {
                        world.getBlockAt(x,4,z).setType(Material.STONE);
                    }
                }

            } else if (worldGenerator == WorldGenerator.WATER) {
                world.setSpawnLocation(0,8,0);
            }

            createWorldSettings(worldName, player, environment);

            plot.setWorld(Bukkit.getWorld(worldName));
            plot.setLoaded(true);

            // Для игрока сообщение и телепортация
            world.getSpawnLocation().getChunk().load(true);
            player.teleport(world.getSpawnLocation());
            clearPlayer(player);
            player.sendTitle(getLocaleMessage("creating-world.welcome-title",player),getLocaleMessage("creating-world.welcome-subtitle",player),15,180,45);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE,100,0.1f);
            player.sendMessage(getLocaleMessage("creating-world.welcome",player));
            player.setGameMode(GameMode.CREATIVE);
            ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
            player.getInventory().setItem(8,worldSettingsItem);

            // Попытка удалить все сущности с мира
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
            return true;

        } else {
            ErrorUtils.sendCriticalErrorMessage("При попытке создать мир для плота " + worldName + ", мир оказался null");
            return false;
        }

    }

    /**
     Returns still not used ID for new plot. It gets a value from config.yml and increases it.
     @return Unique ID for new world.
     **/
    public static int generateWorldID() {
        int newWorldID = plugin.getConfig().getInt("last-world-id",1);
        while (true) {
            newWorldID++;
            boolean exists = false;
            for (File folder : getWorldsFolders(true)) {
                if (folder.getName().equalsIgnoreCase("plot" + newWorldID)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                plugin.getConfig().set("last-world-id",newWorldID);
                plugin.saveConfig();
                return newWorldID;
            }
        }
    }

    /**
     Returns boolean if entity is hostile mob.
     **/
    public static boolean isEntityHostile(EntityType entityType) {
        return switch (entityType) {
            case PIGLIN, PIGLIN_BRUTE, ZOMBIFIED_PIGLIN, ELDER_GUARDIAN, GUARDIAN, ZOGLIN, ZOMBIE_VILLAGER, ZOMBIE,
                 ZOMBIE_HORSE, SKELETON, SKELETON_HORSE, WITHER_SKELETON, WITHER, SHULKER, SPIDER, CAVE_SPIDER,
                 VINDICATOR, ENDER_DRAGON, ENDERMAN, ENDERMITE, SILVERFISH, VEX, RAVAGER, EVOKER, EVOKER_FANGS, BLAZE,
                 HOGLIN, SLIME, STRAY, WITCH, DROWNED, PILLAGER, CREEPER, GHAST, GIANT, MAGMA_CUBE, PHANTOM,
                 ILLUSIONER, WARDEN, BREEZE, HUSK, BOGGED -> true;
            default -> false;
        };
    }

}

