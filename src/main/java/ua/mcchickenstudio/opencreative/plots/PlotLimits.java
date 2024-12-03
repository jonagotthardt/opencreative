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

package ua.mcchickenstudio.opencreative.plots;

import ua.mcchickenstudio.opencreative.settings.groups.LimitType;

public class PlotLimits {

    private final Plot plot;

    private int lastModifiedBlocksAmount;
    private int lastRedstoneOperationsAmount;

    public PlotLimits(Plot plot) {
        this.plot = plot;
    }

    public int getVariablesAmountLimit() {
        return plot.getGroup().getLimit(LimitType.VARIABLES).calculateLimit(plot.getPlayers().size());
    }

    public int getModifyingBlocksLimit() {
        return plot.getGroup().getLimit(LimitType.MODIFYING_BLOCKS).calculateLimit(plot.getPlayers().size());
    }

    public int getRedstoneOperationsLimit() {
        return plot.getGroup().getLimit(LimitType.REDSTONE_OPERATIONS).calculateLimit(plot.getPlayers().size());
    }

    public int getCodeOperationsLimit() {
        return plot.getGroup().getLimit(LimitType.CODE_OPERATIONS).calculateLimit(plot.getPlayers().size());
    }

    public int getEntitiesLimit() {
        return plot.getGroup().getLimit(LimitType.ENTITIES).calculateLimit(plot.getPlayers().size());
    }

    public int getScoreboardsLimit() {
        return plot.getGroup().getLimit(LimitType.SCOREBOARDS).calculateLimit(plot.getPlayers().size());
    }

    public int getBossBarsLimit() {
        return plot.getGroup().getLimit(LimitType.BOSSBARS).calculateLimit(plot.getPlayers().size());
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
        return plot.getGroup().getCodingPlatformsLimit();
    }
}

