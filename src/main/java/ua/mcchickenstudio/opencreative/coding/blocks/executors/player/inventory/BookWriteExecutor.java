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

import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory.BookWriteEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.PlayerExecutor;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.event.Cancellable;

public class BookWriteExecutor extends PlayerExecutor implements Cancellable {

    @Override
    protected void setTempVars(WorldEvent event) {
        if (event instanceof BookWriteEvent bookWriteEvent) {
            setTempVar(EventValues.Variable.ITEM,bookWriteEvent.getOldBook());
            setTempVar(EventValues.Variable.NEW_ITEM,bookWriteEvent.getNewBook());
        }
    }

    public BookWriteExecutor(Planet planet, int x, int y, int z) {
        super(planet, x, y, z);
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.PLAYER_WRITE_BOOK;
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
