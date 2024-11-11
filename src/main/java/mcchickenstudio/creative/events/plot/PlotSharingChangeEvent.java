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
 * Called when plot's sharing mode will be changed.
 * <p>
 * If a Plot Sharing Change event is cancelled, it will not change plot's sharing.
 * <p>
 * <b>NOTE:</b> It's not recommended to cancel event, when the {@link Cause} is WORLD,
 * because this cause is required to prevent world owner from joining the world while
 * it's being deleted.
 */
public class PlotSharingChangeEvent extends PlotEvent implements Cancellable {

    private final Player player;
    private final Plot.Sharing oldSharing;
    private final Plot.Sharing newSharing;
    private final Cause cause;
    private boolean cancel;

    public PlotSharingChangeEvent(Plot plot, Plot.Sharing oldSharing, Plot.Sharing newSharing) {
        super(plot);
        this.oldSharing = oldSharing;
        this.newSharing = newSharing;
        this.player = null;
        this.cause = Cause.WORLD;
    }

    public PlotSharingChangeEvent(Plot plot, Plot.Sharing oldSharing, Plot.Sharing newSharing, Player player) {
        super(plot);
        this.oldSharing = oldSharing;
        this.newSharing = newSharing;
        this.player = player;
        this.cause = Cause.PLAYER;
    }

    public Plot.Sharing getOldSharing() {
        return oldSharing;
    }

    public Plot.Sharing getNewSharing() {
        return newSharing;
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
        WORLD,
    }
}
