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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.player.interaction;

import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.FishEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.PlayerExecutor;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.event.Cancellable;

public class FishExecutor extends PlayerExecutor implements Cancellable {

    public FishExecutor(Planet planet, int x, int y, int z) {
        super(planet, x, y, z);
    }

    @Override
    protected void setTempVars(WorldEvent event) {
        if (event instanceof FishEvent) {
            setTempVar(EventValues.Variable.ITEM,((FishEvent) event).getCaughtItem());
        }
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.PLAYER_FISHING;
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
