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

import mcchickenstudio.creative.utils.PlayerUtils;

public class PlotLimits {

    private final Plot plot;

    private final int entitiesLimit;
    private final int codeOperationsLimit;
    private final int redstoneOperationsLimit;
    private final int modifyingBlocksLimit;
    private final int scoreboardsLimit;
    private final int bossBarsLimit;
    private final int variablesAmountLimit;

    private int lastModifiedBlocksAmount;
    private int lastRedstoneOperationsAmount;

    public PlotLimits(Plot plot) {
        this.plot = plot;
        entitiesLimit = PlayerUtils.getPlayerLimitValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_ENTITIES_LIMIT);
        codeOperationsLimit = PlayerUtils.getPlayerLimitValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_CODE_OPERATIONS_LIMIT);
        redstoneOperationsLimit = PlayerUtils.getPlayerLimitValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_REDSTONE_OPERATIONS_LIMIT);
        modifyingBlocksLimit = PlayerUtils.getPlayerLimitValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_MODIFYING_BLOCKS_LIMIT);
        scoreboardsLimit = PlayerUtils.getPlayerLimitValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_SCOREBOARDS_LIMIT);
        bossBarsLimit = PlayerUtils.getPlayerLimitValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_BOSSBARS_LIMIT);
        variablesAmountLimit = PlayerUtils.getPlayerLimitValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_VARIABLES_LIMIT);
    }

    public int getVariablesAmountLimit() {
        return variablesAmountLimit + (plot.getPlayers().size() * PlayerUtils.getPlayerModifierValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_VARIABLES_LIMIT));
    }

    public int getModifyingBlocksLimit() {
        return modifyingBlocksLimit + (plot.getPlayers().size() * PlayerUtils.getPlayerModifierValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_MODIFYING_BLOCKS_LIMIT));
    }

    public int getRedstoneOperationsLimit() {
        return redstoneOperationsLimit + (plot.getPlayers().size() * PlayerUtils.getPlayerModifierValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_REDSTONE_OPERATIONS_LIMIT));
    }

    public int getCodeOperationsLimit() {
        return codeOperationsLimit + (plot.getPlayers().size() * PlayerUtils.getPlayerModifierValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_CODE_OPERATIONS_LIMIT));
    }

    public int getEntitiesLimit() {
        return entitiesLimit + (plot.getPlayers().size() * PlayerUtils.getPlayerModifierValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_ENTITIES_LIMIT));
    }

    public int getScoreboardsLimit() {
        return scoreboardsLimit + (plot.getPlayers().size() * PlayerUtils.getPlayerModifierValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_SCOREBOARDS_LIMIT));
    }

    public int getBossBarsLimit() {
        return bossBarsLimit + (plot.getPlayers().size() * PlayerUtils.getPlayerModifierValue(plot.getOwnerGroup(), PlayerUtils.PlayerLimit.WORLD_BOSSBARS_LIMIT));
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
}

