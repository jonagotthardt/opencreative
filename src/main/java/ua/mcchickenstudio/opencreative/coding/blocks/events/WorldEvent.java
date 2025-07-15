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

package ua.mcchickenstudio.opencreative.coding.blocks.events;

import org.bukkit.block.Block;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>WorldEvent</h1>
 * This class represents event in Creative's planet.
 */
public abstract class WorldEvent extends Event  {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    protected List<Entity> selection = new ArrayList<>();
    protected final World world;

    public WorldEvent(@NotNull Planet planet, @NotNull List<Entity> selection) {
        this.selection = selection;
        world = planet.getTerritory().getWorld();
    }

    public WorldEvent(@NotNull Planet planet) {
        this.selection.addAll(planet.getTerritory().getWorld().getPlayers());
        world = planet.getTerritory().getWorld();
    }

    public WorldEvent(@NotNull Planet planet, @NotNull Block block) {
        this.selection.addAll(planet.getTerritory().getWorld().getPlayers());
        world = block.getWorld();
    }

    public WorldEvent(@NotNull Entity entity) {
        selection.add(entity);
        world = entity.getWorld();
    }

    public List<Entity> getSelection() {
        return selection;
    }

    public World getWorld() {
        return world;
    }

    public Planet getPlanet() {
        if (getWorld() == null) return null;
        return OpenCreative.getPlanetsManager().getPlanetByWorld(getWorld());
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
