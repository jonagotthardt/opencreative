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

package ua.mcchickenstudio.opencreative.events.planet;

import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.entity.Player;

/**
 * Called when player disconnects from planet.
 * <p>
 * Usually it happens, when player teleports to another world (not related to this planet) or quits the server.
 */
public class PlanetDisconnectPlayerEvent extends PlanetEvent {

    private final Player player;

    public PlanetDisconnectPlayerEvent(Planet planet, Player player) {
        super(planet);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
