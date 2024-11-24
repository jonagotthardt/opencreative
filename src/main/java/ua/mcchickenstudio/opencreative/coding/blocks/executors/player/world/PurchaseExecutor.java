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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.player.world;

import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.PlayerPurchaseEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.PlayerExecutor;
import ua.mcchickenstudio.opencreative.plots.Plot;

public class PurchaseExecutor extends PlayerExecutor{

    public PurchaseExecutor(Plot plot, int x, int y, int z) {
        super(plot, x, y, z);
    }

    @Override
    public void setTempVars(WorldEvent event) {
        if (event instanceof PlayerPurchaseEvent purchaseEvent) {
            setTempVar(EventValues.Variable.PURCHASE_ID, purchaseEvent.getId());
            setTempVar(EventValues.Variable.PURCHASE_NAME, purchaseEvent.getName());
            setTempVar(EventValues.Variable.PURCHASE_SAVE, purchaseEvent.isSave());
            setTempVar(EventValues.Variable.PURCHASE_PRICE, purchaseEvent.getPrice());
        }
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.PLAYER_PURCHASE;
    }

}
