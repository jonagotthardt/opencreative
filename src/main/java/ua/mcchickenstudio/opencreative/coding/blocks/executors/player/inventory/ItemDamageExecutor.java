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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.player.inventory;

import org.bukkit.event.Cancellable;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory.PlayerItemDamagedEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.PlayerExecutor;
import ua.mcchickenstudio.opencreative.planets.Planet;

public class ItemDamageExecutor extends PlayerExecutor implements Cancellable {

    @Override
    protected void setTempVars(WorldEvent event) {
        if (event instanceof PlayerItemDamagedEvent craftEvent) {
            setTempVar(EventValues.Variable.ITEM,craftEvent.getItem());
            setTempVar(EventValues.Variable.DAMAGE,craftEvent.getItem());
        }
    }

    public ItemDamageExecutor(Planet planet, int x, int y, int z) {
        super(planet, x, y, z);
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.PLAYER_ITEM_CRAFT;
    }

    @Override
    public void setCancelled(boolean cancel) {
        getEvent().setCancelled(cancel);
    }

    @Override
    public boolean isCancelled() {
        return getEvent().isCancelled();
    }
}
