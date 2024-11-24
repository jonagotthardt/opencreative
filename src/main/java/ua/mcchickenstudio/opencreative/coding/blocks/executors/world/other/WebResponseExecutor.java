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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.world.other;

import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.WebResponseEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.world.WorldExecutor;
import ua.mcchickenstudio.opencreative.plots.Plot;

public class WebResponseExecutor extends WorldExecutor {

    public WebResponseExecutor(Plot plot, int x, int y, int z) {
        super(plot, x, y, z);
    }

    @Override
    protected void setTempVars(WorldEvent event) {
        if (event instanceof WebResponseEvent webEvent) {
            setTempVar(EventValues.Variable.URL,webEvent.getUrl());
            setTempVar(EventValues.Variable.URL_RESPONSE_CODE,webEvent.getCode());
            setTempVar(EventValues.Variable.URL_RESPONSE,webEvent.getResponse());
        }
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.WORLD_WEB_RESPONSE;
    }
}
