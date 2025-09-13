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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.blocks;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;

import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.LimitReachedBlocksEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import ua.mcchickenstudio.opencreative.planets.PlanetRunnable;

import java.util.List;

public final class SetBlockTypeAction extends WorldAction {
    public SetBlockTypeAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        List<Location> locations = getArguments().getLocationList("locations",this);
        Material material = getArguments().getValue("type", Material.AIR,this);
        PlanetRunnable planetRunnable = new PlanetRunnable(getPlanet()) {
            @Override
            public void execute() {
                getPlanet().getLimits().setLastModifiedBlocksAmount(0);
            }
        };
        for (Location location : locations) {
            if (getPlanet().getLimits().getLastModifiedBlocksAmount() > getPlanet().getLimits().getModifyingBlocksLimit()) {
                getPlanet().getTerritory().scheduleAsyncRunnable(planetRunnable,20L);
                new LimitReachedBlocksEvent(getPlanet()).callEvent();
                return;
            }
            material = switch (material) {
                case BUCKET -> Material.AIR;
                case WATER_BUCKET -> Material.WATER;
                case LAVA_BUCKET -> Material.LAVA;
                case POWDER_SNOW_BUCKET -> Material.POWDER_SNOW;
                case FLINT_AND_STEEL -> Material.FIRE;
                default -> material;
            };
            location.getBlock().setType(material);
            getPlanet().getLimits().setLastModifiedBlocksAmount(getPlanet().getLimits().getLastModifiedBlocksAmount()+1);
        }
        getPlanet().getTerritory().scheduleAsyncRunnable(planetRunnable,20L);

    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SET_BLOCK_TYPE;
    }
}
