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

package mcchickenstudio.creative.coding.blocks.executors.player.interaction;

import mcchickenstudio.creative.coding.blocks.events.WorldEvent;
import mcchickenstudio.creative.coding.blocks.events.EventValues;
import mcchickenstudio.creative.coding.blocks.events.player.interaction.BedLeaveEvent;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorType;
import mcchickenstudio.creative.coding.blocks.executors.player.PlayerExecutor;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.event.Cancellable;

public class BedLeaveExecutor extends PlayerExecutor implements Cancellable {

    public BedLeaveExecutor(Plot plot, int x, int y, int z) {
        super(plot, x, y, z);
    }

    @Override
    protected void setTempVars(WorldEvent event) {
        if (event instanceof BedLeaveEvent leaveEvent) {
            setTempVar(EventValues.Variable.BED,leaveEvent.getBed());
        }
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.PLAYER_BED_ENTER;
    }

    @Override
    public boolean isCancelled() {
        return getEvent().isCancelled();
    }

    @Override
    public void setCancelled(boolean cancel) {
        getEvent().setCancelled(true);
    }
}
