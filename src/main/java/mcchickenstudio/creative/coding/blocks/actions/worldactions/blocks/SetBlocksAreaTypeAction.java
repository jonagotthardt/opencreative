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

package mcchickenstudio.creative.coding.blocks.actions.worldactions.blocks;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.WorldAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class SetBlocksAreaTypeAction extends WorldAction {
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
                getPlot().getLimits().setLastModifiedBlocksAmount(0);
            }
        };
        getPlot().getTerritory().addBukkitRunnable(runnable);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (getPlot().getLimits().getLastModifiedBlocksAmount() > getPlot().getLimits().getModifyingBlocksLimit()) {
                        runnable.runTaskLater(Main.getPlugin(),20L);
                        getPlot().getTerritory().removeBukkitRunnable(runnable);
                        return;
                    }
                    getPlot().getLimits().setLastModifiedBlocksAmount(getPlot().getLimits().getLastModifiedBlocksAmount()+1);
                    Block block = getWorld().getBlockAt(x,y,z);
                    type = switch (type) {
                        case WATER_BUCKET -> Material.WATER;
                        case LAVA_BUCKET -> Material.LAVA;
                        case POWDER_SNOW_BUCKET -> Material.POWDER_SNOW;
                        default -> type;
                    };
                    if (type.isBlock()) {
                        block.setType(type);
                    }
                }
            }
        }
        runnable.runTaskLater(Main.getPlugin(),20L);
        getPlot().getTerritory().removeBukkitRunnable(runnable);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SET_BLOCKS_AREA_TYPE;
    }
}
