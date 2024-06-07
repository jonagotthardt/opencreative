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

package mcchickenstudio.creative.coding.blocks.actions.playeractions.appearance;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.WeatherType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class SetWeatherAction extends PlayerAction {
    public SetWeatherAction(Executor executor, int x, Arguments args) {
        super(executor, x, args);
    }

    @Override
    public void execute(List<Entity> selection) {
        float weatherFloat = getArguments().getValue("weather",1.0f);
        WeatherType weather = (weatherFloat == 2 ? WeatherType.DOWNFALL : WeatherType.CLEAR);
        for (Player player : getPlayers(selection)) {
            player.setPlayerWeather(weather);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PLAYER_SET_WEATHER;
    }
}
