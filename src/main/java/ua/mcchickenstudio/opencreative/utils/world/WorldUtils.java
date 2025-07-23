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

package ua.mcchickenstudio.opencreative.utils.world;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import org.bukkit.*;
import org.bukkit.entity.*;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;

import java.io.File;

import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;

/**
 * <h1>WorldUtils</h1>
 * This class contains methods for changing
 * or manipulating worlds, like checking if
 * world is a planet.
 */
public class WorldUtils {

    /**
     * Returns planet id from world's name
     * by splitting it and removing path to
     * planets folder.
     * @param world world to get id.
     * @return planet id.
     */
    public static @NotNull String getPlanetIdFromName(@NotNull World world) {
        return world.getName()
                .replace(Bukkit.getServer().getWorldContainer() + "/","")
                .replace("planets/planet","");
    }

    /**
     * Checks if the world is a planet by
     * checking whether it's name contains
     * a typical planets folder path.
     * @param world world to check.
     * @return true - should be a planet, false - not a planet.
     */
    public static boolean isPlanet(@NotNull World world) {
        return world.getName().contains("planets/planet");
    }

    /**
     * Checks if the world is a planet, or
     * dev planet, or lobby world.
     * @param world world to check.
     * @return true - it's spawn, planet, dev planet world, false - not plugin's world.
     */
    public static boolean isOpenCreativeWorld(@NotNull World world) {
        return isLobbyWorld(world) || isPlanet(world) || isDevPlanet(world);
    }

    /**
     * Checks if entity is a hostile one,
     * that can attack player.
     * @param entity entity to check.
     * @return true - entity is angry, false - entity is friendly or null.
     */
    public static boolean isEntityHostile(@Nullable Entity entity) {
        return entity instanceof Enemy || entity instanceof Boss;
    }

    /**
     * Checks if the world is a dev planet
     * by checking whether it's name ends
     * with "dev" word.
     * @param world world to check.
     * @return true - should be a dev planet, false - not a dev planet.
     */
    public static boolean isDevPlanet(World world) {
        return world.getName().endsWith("dev");
    }

    /**
     * Checks if the world is a lobby world,
     * where players will be teleported on
     * server connection or by /spawn command.
     * @param world world to check.
     * @return true - it's lobby world, false - not lobby world.
     */
    public static boolean isLobbyWorld(@NotNull World world) {
        return world.equals(PlayerUtils.getLobbyWorld());
    }

    /**
     * Returns a unique numeric ID for new planet.
     * It gets a last numeric ID from config and
     * increases it while checking is number free.
     * Immediately will replace last numeric ID
     * with result.
     * @return unique numeric ID.
     */
    public static int generateWorldID() {
        int newWorldID = OpenCreative.getPlugin().getConfig().getInt("last-world-id",1);
        while (true) {
            newWorldID++;
            boolean exists = false;
            for (File folder : getWorldsFolders()) {
                if (folder.getName().equalsIgnoreCase("planet" + newWorldID)) {
                    exists = true;
                    break;
                } else if (folder.getName().equalsIgnoreCase("planet" + newWorldID + "dev")) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                OpenCreative.getPlugin().getConfig().set("last-world-id",newWorldID);
                OpenCreative.getPlugin().saveConfig();
                return newWorldID;
            }
        }
    }

}

