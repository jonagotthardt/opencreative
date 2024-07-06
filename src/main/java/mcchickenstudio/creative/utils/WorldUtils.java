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

import mcchickenstudio.creative.plots.Plot;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;

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
        SURVIVAL

    }

    /**
     Create a world for plot. It creates world, plot's settings.yml, sets plot parameters, teleports player.
     **/
    public static boolean generateWorld(Plot plot, Player player, String worldName, WorldGenerator worldGenerator) {

        WorldCreator worldCreator = new WorldCreator(worldName).generateStructures(false);
        worldCreator.type(WorldType.FLAT);

        if (worldGenerator == WorldGenerator.EMPTY) {
            worldCreator.generator(new EmptyChunkGenerator());
        } else if (worldGenerator == WorldGenerator.WATER) {
            worldCreator.generator(new WaterChunkGenerator());
        } else if (worldGenerator == WorldGenerator.SURVIVAL) {
            worldCreator.type(WorldType.NORMAL);
            worldCreator.generateStructures(true);
        }

        World world = Bukkit.createWorld(worldCreator);

        if (world != null) {
            world.getWorldBorder().setSize(plot.worldSize);

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

            createWorldSettings(worldName, player);

            plot.world = Bukkit.getWorld(worldName);
            plot.worldName = worldName;
            plot.worldID = worldName.replace("plot","");
            plot.setPlotCustomID(plot.worldID);
            plot.isLoaded = true;

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
        int newWorldID;
        try {
            newWorldID = plugin.getConfig().getInt("last-world-id");
        } catch (NullPointerException e) {
            newWorldID = 1;
            plugin.getConfig().set("last-world-id",newWorldID);
            plugin.saveConfig();
        }

        while (true) {
            // Будет добавлять по единице и проверять существует ли файл с таким названием
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
        switch (entityType) {
            case PIGLIN:
            case PIGLIN_BRUTE:
            case ZOMBIFIED_PIGLIN:
            case ELDER_GUARDIAN:
            case GUARDIAN:
            case ZOGLIN:
            case ZOMBIE_VILLAGER:
            case ZOMBIE:
            case ZOMBIE_HORSE:
            case SKELETON:
            case SKELETON_HORSE:
            case WITHER_SKELETON:
            case WITHER:
            case SHULKER:
            case SPIDER:
            case CAVE_SPIDER:
            case VINDICATOR:
            case ENDER_DRAGON:
            case ENDERMAN:
            case ENDERMITE:
            case SILVERFISH:
            case VEX:
            case RAVAGER:
            case EVOKER:
            case EVOKER_FANGS:
            case BLAZE:
            case HOGLIN:
            case SLIME:
            case STRAY:
            case WITCH:
            case DROWNED:
            case PILLAGER:
            case CREEPER:
            case GHAST:
            case GIANT:
            case MAGMA_CUBE:
            case PHANTOM:
            case ILLUSIONER:
                return true;
        }
        return false;
    }

}

class EmptyChunkGenerator extends ChunkGenerator {

    @Override
    public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        return createChunkData(world);
    }
}

class WaterChunkGenerator extends ChunkGenerator {

    @Override
    public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunkData.setBlock(i, 0, j, Material.BEDROCK);
                chunkData.setBlock(i, 1, j, Material.SAND);
                chunkData.setBlock(i, 2, j, Material.SAND);
                chunkData.setBlock(i, 3, j, Material.WATER);
                chunkData.setBlock(i, 4, j, Material.WATER);
                chunkData.setBlock(i, 5, j, Material.WATER);
                chunkData.setBlock(i, 6, j, Material.WATER);
                chunkData.setBlock(i, 7, j, Material.WATER);
            }
        }
        return chunkData;
    }

}
