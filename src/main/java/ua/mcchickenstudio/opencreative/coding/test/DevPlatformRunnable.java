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

package ua.mcchickenstudio.opencreative.coding.test;

import ua.mcchickenstudio.opencreative.plots.DevPlot;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class DevPlatformRunnable extends BukkitRunnable {

    private final DevPlot devPlot;
    private final int numberX;
    private final int numberZ;

    public DevPlatformRunnable(DevPlot devPlot, int x, int z) {
        this.devPlot = devPlot;
        this.numberX = x;
        this.numberZ = z;
    }

    @Override
    public void run() {
        int beginX =  devPlot.getPlatformBeginCoordinate(numberX)+4;
        int beginZ = devPlot.getPlatformBeginCoordinate(numberZ)+4;
        int endX = devPlot.getPlatformEndCoordinate(numberZ)-4;
        int endZ = devPlot.getPlatformEndCoordinate(numberZ)-4;
        // For executor block
        for (int z = beginZ; z <= endZ; z=z+3) {

            Block executorBlock = devPlot.getWorld().getBlockAt(beginX,1,z);
            parseExecutorBlock(executorBlock);

            // For action block
            for (int x = beginX; x <= endX; x=x+2) {
                Block actionBlock = devPlot.getWorld().getBlockAt(x,1,z);
                parseActionBlock(executorBlock,actionBlock);
            }
        }
    }

    public abstract void parseExecutorBlock(Block block);
    public abstract void parseActionBlock(Block executor, Block block);

}
