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

package ua.mcchickenstudio.opencreative.planets;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;

import java.util.ArrayList;
import java.util.List;

public class DevPlatform {

    private final int x;
    private final int z;
    private final World world;

    public DevPlatform(World world, int x, int z) {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public boolean exists() {
        if (!world.isChunkGenerated(getBeginX() >> 4,getBeginZ() >> 4)) {
            return false;
        }
        return world.getBlockAt(getBeginX(),0,getBeginZ()).isSolid();
    }

    /**
     * Checks if specified column doesn't have any block.
     * @param column from 1 to 24.
     * @return true - column is empty, false - column has blocks.
     */
    public boolean isEmptyColumn(int column) {
        if (column < 1 || column > 24) throw new IllegalArgumentException("Developer platform column must be in range from 1 to 24.");
        int z = getBeginZ()+(column*4);
        for (int x = getBeginX()+4; x <= getEndX()-3; x++) {
            Block block = world.getBlockAt(x,1,z);
            if (!block.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public List<Location> getFreeColumns() {
        List<Location> columns = new ArrayList<>();
        for (int column = 1; column <= 24; column++) {
            if (isEmptyColumn(column)) {
                columns.add(new Location(world,getBeginX()+4,1,getBeginZ()+(column*4)));
            }
        }
        return columns;
    }

    public Material getFloorMaterial() {
        return world.getBlockAt(getBeginX(),0,getBeginZ()).getType();
    }

    public Material getEventMaterial() {
        return world.getBlockAt(getBeginX()+4,0,getBeginZ()+4).getType();
    }

    public Material getActionMaterial() {
        return world.getBlockAt(getBeginX()+6,0,getBeginZ()+4).getType();
    }

    public boolean cantBePlatformMaterial(Material material) {
        if (!material.isBlock()) {
            return true;
        }
        return material != Material.BARRIER && !material.name().endsWith("GLASS");
    }

    public boolean setMaterials(Material floor, Material event, Material action) {
        if (cantBePlatformMaterial(floor) || cantBePlatformMaterial(event) || cantBePlatformMaterial(action)) return false;
        if (floor == event || floor == action || event == action) return false;
        return build(floor,event,action);
    }

    public boolean setFloorMaterial(Material floor) {
        if (cantBePlatformMaterial(floor)) return false;
        if (floor == getEventMaterial()) return false;
        if (floor == getActionMaterial()) return false;
        return build(floor,getEventMaterial(),getActionMaterial());
    }

    public boolean setEventMaterial(Material event) {
        if (cantBePlatformMaterial(event)) return false;
        if (event == getFloorMaterial()) return false;
        if (event == getActionMaterial()) return false;
        return build(getFloorMaterial(),event,getActionMaterial());
    }

    public boolean setActionMaterial(Material action) {
        if (cantBePlatformMaterial(action)) return false;
        if (action == getEventMaterial()) return false;
        if (action == getActionMaterial()) return false;
        return build(getFloorMaterial(),getEventMaterial(),action);
    }

    public boolean build(Material floor, Material event, Material action) {
        int executorX = getBeginX()+4;
        for (int x = getBeginX(); x <= getEndX(); x++) {
            for (int z = getBeginZ(); z <= getEndZ(); z++) {
                Block block = world.getBlockAt(x,0,z);
                if (x == executorX && (z - getBeginZ()) % 4 == 0 && z != getBeginZ() && z != getEndZ()) {
                    block.setType(event);
                } else if (x > executorX && (x - executorX) % 2 == 0 && x < getEndX() - 2 && (z - getBeginZ()) % 4 == 0 && z != getBeginZ() && z != getEndZ()) {
                    block.setType(action);
                } else {
                    block.setType(floor);
                }
                block.setBiome(Biome.ICE_SPIKES);
            }
        }
        return true;
    }

    public int getBeginX() {
        return (x - 1) * 102;
    }

    public int getEndX() {
        return getBeginX() + 100;
    }

    public int getBeginZ() {
        return (z - 1) * 102;
    }

    public int getEndZ() {
        return getBeginZ() + 100;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public Location getSpawnLocation() {
        return new Location(world,getBeginX()+2.5,1,getBeginZ()+2.5,-45,0);
    }

    @Override
    public String toString() {
        return "DevPlatform x: " + x + " z: " + z;
    }
}
