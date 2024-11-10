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

package mcchickenstudio.creative.coding.blocks.actions.worldactions.world;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.WorldAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;

public class SetWeatherAction extends WorldAction {
    public SetWeatherAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        String weather = getArguments().getValue("weather", "clean",this);
        int duration = getArguments().getValue("duration", 6000,this);
        switch (weather.toLowerCase()) {
            case "storm": {
                getPlot().getWorld().setStorm(true);
                if (duration >= 0) {
                    getPlot().getWorld().setWeatherDuration(duration);
                }
                break;
            }
            case "thunder": {
                getPlot().getWorld().setThundering(true);
                if (duration >= 0) {
                    getPlot().getWorld().setThunderDuration(duration);
                }
                break;
            }
            default: {
                getPlot().getWorld().setClearWeatherDuration(Math.max(duration, 0));
                break;
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SET_WEATHER;
    }
}
