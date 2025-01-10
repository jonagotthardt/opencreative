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

package ua.mcchickenstudio.opencreative.coding.test;

import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class DevPlatformRunnable extends BukkitRunnable {

    private final DevPlanet devPlanet;
    private final int numberX;
    private final int numberZ;

    public DevPlatformRunnable(DevPlanet devPlanet, int x, int z) {
        this.devPlanet = devPlanet;
        this.numberX = x;
        this.numberZ = z;
    }

    @Override
    public void run() {
        int beginX =  devPlanet.getPlatformBeginCoordinate(numberX)+4;
        int beginZ = devPlanet.getPlatformBeginCoordinate(numberZ)+4;
        int endX = devPlanet.getPlatformEndCoordinate(numberZ)-4;
        int endZ = devPlanet.getPlatformEndCoordinate(numberZ)-4;
        // For executor block
        for (int z = beginZ; z <= endZ; z=z+3) {

            Block executorBlock = devPlanet.getWorld().getBlockAt(beginX,1,z);
            parseExecutorBlock(executorBlock);

            // For action block
            for (int x = beginX; x <= endX; x=x+2) {
                Block actionBlock = devPlanet.getWorld().getBlockAt(x,1,z);
                parseActionBlock(executorBlock,actionBlock);
            }
        }
    }

    public abstract void parseExecutorBlock(Block block);
    public abstract void parseActionBlock(Block executor, Block block);

}
