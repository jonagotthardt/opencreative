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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.isOutOfBorders;

public final class SetBlocksAreaTypeAction extends WorldAction {
    public SetBlocksAreaTypeAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        if (!getArguments().pathExists("first") || !getArguments().pathExists("second")) {
            return;
        }
        Location firstLocation = getArguments().getValue("first",getWorld().getSpawnLocation(),this);
        Location secondLocation = getArguments().getValue("second",getWorld().getSpawnLocation(),this);
        Material type = getArguments().getValue("type", Material.AIR,this);
        int minX = Math.min(firstLocation.getBlockX(),secondLocation.getBlockX());
        int minY = Math.min(firstLocation.getBlockY(),secondLocation.getBlockY());
        int minZ = Math.min(firstLocation.getBlockZ(),secondLocation.getBlockZ());
        int maxX = Math.max(firstLocation.getBlockX(),secondLocation.getBlockX());
        int maxY = Math.max(firstLocation.getBlockY(),secondLocation.getBlockY());
        int maxZ = Math.max(firstLocation.getBlockZ(),secondLocation.getBlockZ());
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                getPlanet().getLimits().setLastModifiedBlocksAmount(0);
            }
        };
        getPlanet().getTerritory().addBukkitRunnable(runnable);
        type = switch (type) {
            case WATER_BUCKET -> Material.WATER;
            case LAVA_BUCKET -> Material.LAVA;
            case POWDER_SNOW_BUCKET -> Material.POWDER_SNOW;
            default -> type;
        };
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (getPlanet().getLimits().getLastModifiedBlocksAmount() > getPlanet().getLimits().getModifyingBlocksLimit()) {
                        runnable.runTaskLater(OpenCreative.getPlugin(),20L);
                        getPlanet().getTerritory().removeBukkitRunnable(runnable);
                        return;
                    }
                    getPlanet().getLimits().setLastModifiedBlocksAmount(getPlanet().getLimits().getLastModifiedBlocksAmount()+1);
                    Block block = getWorld().getBlockAt(x,y,z);
                    if (type.isBlock() && !isOutOfBorders(block.getLocation())) {
                        block.setType(type,false);
                    }
                }
            }
        }
        runnable.runTaskLater(OpenCreative.getPlugin(),20L);
        getPlanet().getTerritory().removeBukkitRunnable(runnable);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SET_BLOCKS_AREA_TYPE;
    }
}
