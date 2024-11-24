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

package mcchickenstudio.creative.events.plot;

import mcchickenstudio.creative.plots.Plot;
import org.bukkit.entity.Player;

/**
 * Called when player disconnects from plot.
 * <p>
 * Usually it happens, when player teleports to another world (not related to this plot) or quits the server.
 */
public class PlotDisconnectPlayerEvent extends PlotEvent {

    private final Player player;

    public PlotDisconnectPlayerEvent(Plot plot, Player player) {
        super(plot);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
