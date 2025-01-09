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

import ua.mcchickenstudio.opencreative.settings.groups.LimitType;

public class PlanetLimits {

    private final Planet planet;

    private int lastModifiedBlocksAmount;
    private int lastRedstoneOperationsAmount;

    public PlanetLimits(Planet planet) {
        this.planet = planet;
    }

    public int getVariablesAmountLimit() {
        return planet.getGroup().getLimit(LimitType.VARIABLES).calculateLimit(planet.getPlayers().size());
    }

    public int getModifyingBlocksLimit() {
        return planet.getGroup().getLimit(LimitType.MODIFYING_BLOCKS).calculateLimit(planet.getPlayers().size());
    }

    public int getRedstoneOperationsLimit() {
        return planet.getGroup().getLimit(LimitType.REDSTONE_OPERATIONS).calculateLimit(planet.getPlayers().size());
    }

    public int getCodeOperationsLimit() {
        return planet.getGroup().getLimit(LimitType.CODE_OPERATIONS).calculateLimit(planet.getPlayers().size());
    }

    public int getEntitiesLimit() {
        return planet.getGroup().getLimit(LimitType.ENTITIES).calculateLimit(planet.getPlayers().size());
    }

    public int getScoreboardsLimit() {
        return planet.getGroup().getLimit(LimitType.SCOREBOARDS).calculateLimit(planet.getPlayers().size());
    }

    public int getBossBarsLimit() {
        return planet.getGroup().getLimit(LimitType.BOSSBARS).calculateLimit(planet.getPlayers().size());
    }

    public void setLastModifiedBlocksAmount(int lastModifiedBlocksAmount) {
        this.lastModifiedBlocksAmount = lastModifiedBlocksAmount;
    }

    public void setLastRedstoneOperationsAmount(int lastRedstoneOperationsAmount) {
        this.lastRedstoneOperationsAmount = lastRedstoneOperationsAmount;
    }

    public int getLastModifiedBlocksAmount() {
        return lastModifiedBlocksAmount;
    }

    public int getLastRedstoneOperationsAmount() {
        return lastRedstoneOperationsAmount;
    }

    public int getCodingPlatformsLimit() {
        return planet.getGroup().getCodingPlatformsLimit();
    }
}

