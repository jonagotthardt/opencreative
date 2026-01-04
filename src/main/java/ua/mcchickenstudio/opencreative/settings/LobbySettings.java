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
 * <h1>LobbySettings</h1>
 * This class represents settings of lobby world,
 * where players will be teleported on server join,
 * or by using /spawn command.
 */
public final class LobbySettings {

    private boolean clearInventory = true;
    private boolean disallowPlacingBlocks = true;
    private boolean disallowDestroyingBlocks = true;
    private boolean disallowSpawningMobs = true;
    private boolean disallowDamagingMobs = true;
    private boolean disallowWorldEdit = true;
    private boolean disableExplosions = true;

    /**
     * Loads settings of lobby from configuration.
     */
    public void load() {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("lobby");

        if (section == null) {
            sendCriticalErrorMessage("Can't load lobby settings, section `lobby` in config.yml is empty.");
            return;
        }

        clearInventory = section.getBoolean("clear-inventory", true);
        disableExplosions = section.getBoolean("disable-explosions", true);
        disallowWorldEdit = section.getBoolean("disallow-world-edit", true);
        disallowDamagingMobs = section.getBoolean("disallow-damaging-mobs", true);
        disallowSpawningMobs = section.getBoolean("disallow-spawning-mobs", true);
        disallowPlacingBlocks = section.getBoolean("disallow-placing-blocks", true);
        disallowDestroyingBlocks = section.getBoolean("disallow-destroying-blocks", true);
    }

    /**
     * Checks whether player's inventory should be
     * cleared after joining a lobby.
     *
     * @return true - will be cleared, false - not.
     */
    public boolean shouldClearInventory() {
        return clearInventory;
    }

    /**
     * Checks whether explosions in lobby world
     * should be cancelled without bypass permission.
     *
     * @return true - disable explosions, false - allow them.
     */
    public boolean areExplosionsDisabled() {
        return disableExplosions;
    }

    /**
     * Checks whether damaging mobs in lobby world
     * should be cancelled without bypass permission.
     *
     * @return true - disable damaging mobs, false - allow it.
     */
    public boolean isDamagingMobsDisallowed() {
        return disallowDamagingMobs;
    }

    /**
     * Checks whether destroying blocks in lobby world
     * should be cancelled without bypass permission.
     *
     * @return true - disable destroying blocks, false - allow it.
     */
    public boolean isDestroyingBlocksDisallowed() {
        return disallowDestroyingBlocks;
    }

    /**
     * Checks whether placing blocks in lobby world
     * should be cancelled without bypass permission.
     *
     * @return true - disable placing blocks, false - allow it.
     */
    public boolean isPlacingBlocksDisallowed() {
        return disallowPlacingBlocks;
    }

    /**
     * Checks whether spawning mobs in lobby world
     * should be cancelled without bypass permission.
     *
     * @return true - disable spawning mobs, false - allow it.
     */
    public boolean isSpawningMobsDisallowed() {
        return disallowSpawningMobs;
    }

    /**
     * Checks whether using WorldEdit in lobby world
     * should be cancelled without bypass permission.
     *
     * @return true - disable spawning mobs, false - allow it.
     */
    public boolean isWorldEditDisallowed() {
        return disallowWorldEdit;
    }

}
