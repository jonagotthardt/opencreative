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
    protected World world;

    public CreativeEvent(Plot plot, List<Entity> selection) {
        this.selection = selection;
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
