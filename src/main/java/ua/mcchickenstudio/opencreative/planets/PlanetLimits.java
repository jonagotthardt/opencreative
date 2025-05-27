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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.settings.groups.LimitType;

import java.util.*;

/**
 * <h1>PlanetLimits</h1>
 * This class represents all limits of the planet.
 * Use it to check if the limit is reached or not.
 */
public class PlanetLimits {

    private final Planet planet;

    private int lastModifiedBlocksAmount;
    private int lastRedstoneOperationsAmount;
    private int lastListElementsChangesAmount;

    private final LinkedList<Long> lastLightningsStrikes = new LinkedList<>();
    private final Map<UUID, Deque<Long>> lastPlayerMenuOpens = new HashMap<>();

    public PlanetLimits(Planet planet) {
        this.planet = planet;
    }

    /**
     * Returns limit of all variables' size in the planet.
     * If the value of a variable is map, keys' amount will be added to size.
     * If the value of a variable is list, elements' amount will be added to size.
     * @return limit of variables size.
     */
    public int getVariablesAmountLimit() {
        return planet.getGroup().getLimit(LimitType.VARIABLES).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Returns modifying blocks limit.
     * @return limit of changing blocks.
     */
    public int getModifyingBlocksLimit() {
        return planet.getGroup().getLimit(LimitType.MODIFYING_BLOCKS).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Returns maximum builders amount in the planet.
     * @return limit of builders amount.
     */
    public int getBuildersLimit() {
        return planet.getGroup().getLimit(LimitType.BUILDERS_AMOUNT).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Returns maximum developers amount in the planet.
     * @return limit of developers amount.
     */
    public int getDevelopersLimit() {
        return planet.getGroup().getLimit(LimitType.DEVELOPERS_AMOUNT).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Returns maximum blacklisted players amount in the planet.
     * @return limit of banned players amount.
     */
    public int getBlacklistedLimit() {
        return planet.getGroup().getLimit(LimitType.BLACKLISTED_AMOUNT).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Returns maximum whitelisted players amount in the planet.
     * @return limit of whitelisted players amount.
     */
    public int getWhitelistedLimit() {
        return planet.getGroup().getLimit(LimitType.WHITELISTED_AMOUNT).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Returns maximum redstone operations amount in the planet.
     * @return limit of redstone operations.
     */
    public int getRedstoneOperationsLimit() {
        return planet.getGroup().getLimit(LimitType.REDSTONE_OPERATIONS).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Returns maximum code operations amount in the planet.
     * @return limit of executor calls.
     */
    public int getCodeOperationsLimit() {
        return planet.getGroup().getLimit(LimitType.CODE_OPERATIONS).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Returns maximum entities amount in the planet.
     * @return limit of entities.
     */
    public int getEntitiesLimit() {
        return planet.getGroup().getLimit(LimitType.ENTITIES).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Returns maximum scoreboards amount in the planet.
     * @return limit of scoreboards.
     */
    public int getScoreboardsLimit() {
        return planet.getGroup().getLimit(LimitType.SCOREBOARDS).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Returns maximum elements changes amount in the planet.
     * @return limit of scoreboards.
     */
    public int getVariableElementsChangesLimit() {
        return planet.getGroup().getLimit(LimitType.SCOREBOARDS).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Returns maximum bossbars amount in the planet.
     * @return limit of bossbars.
     */
    public int getBossBarsLimit() {
        return planet.getGroup().getLimit(LimitType.BOSSBARS).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Returns maximum physical objects limit in the planet.
     * It's not related to sand, gravel, anvil and other blocks,
     * they are related to entities.
     * @return limit of physical objects.
     */
    public int getPhysicalObjectsLimit() {
        return planet.getGroup().getLimit(LimitType.PHYSICAL_OBJECTS).calculateLimit(planet.getPlayers().size());
    }

    /**
     * Sets last amount of changed elements in lists or maps.
     * @param changedElementsAmount number of changed elements.
     */
    public void setLastVariableElementsChangesAmount(int changedElementsAmount) {
        this.lastListElementsChangesAmount = changedElementsAmount;
    }

    /**
     * Sets last modified blocks amount.
     * @param lastModifiedBlocksAmount number of changed blocks.
     */
    public void setLastModifiedBlocksAmount(int lastModifiedBlocksAmount) {
        this.lastModifiedBlocksAmount = lastModifiedBlocksAmount;
    }

    /**
     * Sets last redstone operations amount.
     * @param lastRedstoneOperationsAmount number of redstone operations.
     */
    public void setLastRedstoneOperationsAmount(int lastRedstoneOperationsAmount) {
        this.lastRedstoneOperationsAmount = lastRedstoneOperationsAmount;
    }

    /**
     * Returns last modified blocks amount.
     * @return number of modified blocks.
     */
    public int getLastModifiedBlocksAmount() {
        return lastModifiedBlocksAmount;
    }

    /**
     * Returns last redstone operations amount.
     * @return number of redstone operations.
     */
    public int getLastRedstoneOperationsAmount() {
        return lastRedstoneOperationsAmount;
    }

    /**
     * Returns last elements changes amount in lists or maps.
     * @return number of elements changes.
     */
    public int getLastVariableElementsChangesAmount() {
        return lastListElementsChangesAmount;
    }

    /**
     * Returns maximum coding platforms amount in the planet.
     * Doesn't change with players' amount.
     * @return limit of coding platforms.
     */
    public int getCodingPlatformsLimit() {
        return planet.getGroup().getCodingPlatformsLimit();
    }

    /**
     * Checks if world can strike lightning. Used to prevent
     * "too many lightning strikes" crash. Checks if the amount
     * of lightning strikes in last 5 seconds is not greater than 5.
     * @return true - if it's allowed to strike lightning, false - it's disallowed.
     */
    public boolean canLightningStrike() {

        long now = System.currentTimeMillis();

        // Removes time from list, if it's more than 5 seconds.
        while (!lastLightningsStrikes.isEmpty() && (now - lastLightningsStrikes.peek()) > 5000) {
            lastLightningsStrikes.poll();
        }

        if (lastLightningsStrikes.size() >= 5) {
            return false;
        } else {
            lastLightningsStrikes.add(now);
            return true;
        }

    }

    /**
     * Checks if world can open custom menu for player.
     * Used to prevent unavailability of leaving game.
     * Checks if the amount of player's opened menus
     * in last 5 seconds is not greater than 5.
     * @return true - if it's allowed to open menu, false - it's disallowed.
     */
    public boolean canOpenMenu(Player player) {

        for (UUID uuid : lastPlayerMenuOpens.keySet()) {
            // Removes offline players
            if (Bukkit.getPlayer(uuid) == null) {
                lastPlayerMenuOpens.remove(uuid);
            }
        }

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        lastPlayerMenuOpens.putIfAbsent(uuid, new LinkedList<>());
        Deque<Long> timestamps = lastPlayerMenuOpens.get(uuid);

        // Removes time from list, if it's more than 5 seconds.
        while (!timestamps.isEmpty() && (now - timestamps.peekFirst()) > 5000) {
            timestamps.pollFirst();
        }

        if (timestamps.size() >= 5) {
            return false;
        }

        timestamps.addLast(now);
        return true;
    }

    /**
     * Clears player's data when player leaves planet.
     * @param player player to clear data.
     */
    public void clearPlayerLimits(Player player) {
        lastPlayerMenuOpens.remove(player.getUniqueId());
    }

    /**
     * Clears data on planet unload.
     */
    public void clear() {
        lastLightningsStrikes.clear();
        lastModifiedBlocksAmount = 0;
        lastRedstoneOperationsAmount = 0;
        lastListElementsChangesAmount = 0;
    }

}

