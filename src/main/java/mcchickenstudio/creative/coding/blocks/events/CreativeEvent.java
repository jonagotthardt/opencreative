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

package mcchickenstudio.creative.coding.blocks.events;

import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>CreativeEvent</h1>
 * This class represents event in Creative's plot.
 */
public abstract class CreativeEvent extends Event  {

    private static final HandlerList handlers = new HandlerList();
    protected List<Entity> selection = new ArrayList<>();
    protected boolean cancelled = false;
    protected final World world;

    public CreativeEvent(Plot plot, List<Entity> selection) {
        this.selection = selection;
        world = plot.getWorld();
    }

    public CreativeEvent(Plot plot) {
        this.selection.addAll(plot.getWorld().getPlayers());
        world = plot.getWorld();
    }

    public CreativeEvent(Entity entity) {
        selection.add(entity);
        world = entity.getWorld();
    }

    public List<Entity> getSelection() {
        return selection;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public World getWorld() {
        return world;
    }

    public Plot getPlot() {
        if (getWorld() == null) return null;
        return PlotManager.getInstance().getPlotByWorld(getWorld());
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
