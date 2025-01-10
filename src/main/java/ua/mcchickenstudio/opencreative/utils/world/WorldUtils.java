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

import ua.mcchickenstudio.opencreative.OpenCreative;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.io.File;

import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;

public class WorldUtils {

    public enum WorldGenerator {

        FLAT,
        EMPTY,
        WATER,
        SURVIVAL,
        LARGE_BIOMES,

    }

    public static boolean isDevPlanet(World world) {
        return world.getName().endsWith("dev");
    }

    /**
     Returns still not used ID for new planet. It gets a value from config.yml and increases it.
     @return Unique ID for new world.
     **/
    public static int generateWorldID() {
        int newWorldID = OpenCreative.getPlugin().getConfig().getInt("last-world-id",1);
        while (true) {
            newWorldID++;
            boolean exists = false;
            for (File folder : getWorldsFolders(true)) {
                if (folder.getName().equalsIgnoreCase("planet" + newWorldID)) {
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

