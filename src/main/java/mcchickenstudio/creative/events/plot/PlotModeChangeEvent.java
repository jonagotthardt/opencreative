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
import org.bukkit.event.Cancellable;

/**
 * Called when plot's mode will be changed.
 * <p>
 * If a Plot Mode Change event is cancelled, it will not change plot's mode.
 * <p>
 * <b>NOTE:</b> It's not recommended to cancel event, when the {@link Cause} is CODE,
 * because this cause is required to stop code running due to critical errors in code.
 * Plugins should check is player a cause of Plot Mode Change event.
 */
public class PlotModeChangeEvent extends PlotEvent implements Cancellable {

    private final Player player;
    private final Plot.Mode oldMode;
    private final Plot.Mode newMode;
    private final Cause cause;
    private boolean cancel;

    public PlotModeChangeEvent(Plot plot, Plot.Mode oldMode, Plot.Mode newMode) {
        super(plot);
        this.oldMode = oldMode;
        this.newMode = newMode;
        this.player = null;
        this.cause = Cause.CODE;
    }

    public PlotModeChangeEvent(Plot plot, Plot.Mode oldMode, Plot.Mode newMode, Player player) {
        super(plot);
        this.oldMode = oldMode;
        this.newMode = newMode;
        this.player = player;
        this.cause = Cause.PLAYER;
    }

    public Plot.Mode getOldMode() {
        return oldMode;
    }

    public Plot.Mode getNewMode() {
        return newMode;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    public Cause getCause() {
        return cause;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public enum Cause {
        PLAYER,
        CODE,
    }
}
