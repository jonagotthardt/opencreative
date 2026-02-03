/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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

package ua.mcchickenstudio.opencreative.settings;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ua.mcchickenstudio.opencreative.OpenCreative;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;

/**
 * <h1>WorldFixerSettings</h1>
 * This class represents a settings of world fixer.
 */
public final class WorldFixerSettings {

    private boolean fixVehicleCollisions = true;
    private int maxMinecartCollisionsAmount = 100;

    private boolean fixBadEntitiesInAir = true;

    /**
     * Loads settings of world fixer from configuration.
     */
    public void load() {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("world-fixer");
        if (section == null) {
            section = config.createSection("world-fixer");
        }

        fixVehicleCollisions = section.getBoolean("vehicle-collisions.enabled", true);
        maxMinecartCollisionsAmount = section.getInt("vehicle-collisions.max-collisions", 100);

        fixBadEntitiesInAir = section.getBoolean("bad-entities-in-air.enabled", true);
    }

    /**
     * Checks whether vehicles with too many collisions should be destroyed.
     *
     * @return true - will be fixed, false - not.
     */
    public boolean shouldFixVehicleCollisions() {
        return fixVehicleCollisions;
    }

    /**
     * Checks whether bad entities in air should be fixed.
     *
     * @return true - will be fixed, false - not.
     */
    public boolean shouldFixBadEntitiesInAir() {
        return fixBadEntitiesInAir;
    }

    /**
     * Returns maximum amount of vehicle collisions
     * in the last 0.5 seconds before its removal.
     *
     * @return limit of vehicle collisions.
     */
    public int getMaxMinecartCollisionsAmount() {
        return maxMinecartCollisionsAmount;
    }

}
