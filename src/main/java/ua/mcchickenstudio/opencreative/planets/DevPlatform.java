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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.sign.Side;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;
import ua.mcchickenstudio.opencreative.utils.world.cache.ChunkCache;
import ua.mcchickenstudio.opencreative.utils.world.platforms.DevPlatformer;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>DevPlatform</h1>
 * This class represents developers platform in developers world.
 * It's a floor with columns, that are used to place coding blocks.
 */
public class DevPlatform {

    private final int x;
    private final int z;
    private final World world;
    private final DevPlatformer platformer;

    public DevPlatform(DevPlanet devPlanet, int x, int z) {
        this.x = x;
        this.z = z;
        this.world = devPlanet.getWorld();
        this.platformer = devPlanet.getDevPlatformer();
    }

    public DevPlatform(World world, DevPlatformer platformer, int x, int z) {
        this.x = x;
        this.z = z;
        this.world = world;
        this.platformer = platformer;
    }

    public DevPlatform(World world, int x, int z) {
        this.x = x;
        this.z = z;
        this.world = world;
        this.platformer = OpenCreative.getDevPlatformer();
    }

    public boolean exists() {
        Location begin = platformer.getPlatformBeginLocation(this).add(4,0,4);
        if (!ChunkCache.isChunkGenerated(world, begin.getBlockX() >> 4, begin.getBlockZ() >> 4)) {
            return false;
        }
        return begin.getBlock().isSolid();
    }

    /**
     * Checks if specified column doesn't have any block.
     * @param column from 1 to 24.
     * @return true - column is empty, false - column has blocks.
     */
    public boolean isEmptyColumn(int column) {
        if (column < 1 || column > 24) throw new IllegalArgumentException("Developer platform column must be in range from 1 to 24.");
        Location begin = platformer.getPlatformBeginLocation(this);
        Location end = platformer.getPlatformEndLocation(this);
        int z = begin.getBlockZ() + (column*4);
        for (int x = begin.getBlockX() + 4; x <= end.getBlockX() - 3; x++) {
            Block block = world.getBlockAt(x,begin.getBlockY(),z);
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
                columns.add(platformer.getPlatformBeginLocation(this).clone()
                        .add(4,0,column * 4));
            }
        }
        return columns;
    }

    public Material getFloorMaterial() {
        return world.getBlockAt(platformer.getPlatformBeginLocation(this)).getType();
    }

    public Material getEventMaterial() {
        return world.getBlockAt(platformer.getPlatformBeginLocation(this)
                .clone().add(4,0,4)).getType();
    }

    public Material getActionMaterial() {
        return world.getBlockAt(platformer.getPlatformBeginLocation(this)
                .clone().add(6,0,4)).getType();
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
        return platformer.buildPlatform(this, floor, event, action);
    }

    public boolean setFloorMaterial(Material floor) {
        if (cantBePlatformMaterial(floor)) return false;
        if (floor == getEventMaterial()) return false;
        if (floor == getActionMaterial()) return false;
        return platformer.buildPlatform(this, floor, getEventMaterial(), getActionMaterial());
    }

    public boolean setEventMaterial(Material event) {
        if (cantBePlatformMaterial(event)) return false;
        if (event == getFloorMaterial()) return false;
        if (event == getActionMaterial()) return false;
        return platformer.buildPlatform(this, getFloorMaterial(), event, getActionMaterial());
    }

    public boolean setActionMaterial(Material action) {
        if (cantBePlatformMaterial(action)) return false;
        if (action == getEventMaterial()) return false;
        if (action == getActionMaterial()) return false;
        return platformer.buildPlatform(this, getFloorMaterial(), getEventMaterial(), action);
    }

    public void setContainerMaterial(Material containerMaterial) {
        Location begin = platformer.getPlatformBeginLocation(this);
        Location end = platformer.getPlatformEndLocation(this);
        for (int z = begin.getBlockZ()+4; z < end.getBlockZ()-4; z = z + 4) {
            for (int x = begin.getBlockX()+6; x <= end.getBlockX()-4; x = x + 2) {
                Block containerBlock = new Location(getWorld(), x, 2, z).getBlock();
                if (containerBlock.getState() instanceof InventoryHolder container) {
                    ItemStack[] data = container.getInventory().getContents();
                    containerBlock.setType(containerMaterial);
                    ((Container) containerBlock.getState()).getInventory().setContents(data);
                    BlockData blockData = containerBlock.getBlockData();
                    ((Directional) blockData).setFacing(BlockFace.SOUTH);
                    containerBlock.setBlockData(blockData);
                    containerBlock.getState().update();
                }
            }
        }
    }

    public void setSignMaterial(Material signMaterial) {
        Location begin = platformer.getPlatformBeginLocation(this);
        Location end = platformer.getPlatformEndLocation(this);
        for (int z = begin.getBlockZ() + 5; z < end.getBlockZ() - 4; z = z + 4) {
            for (int x = begin.getBlockX()+4; x <= end.getBlockX() - 4; x = x + 2) {
                Block signBlock = new Location(getWorld(), x, 1, z).getBlock();
                if (signBlock.getType().name().contains("WALL_SIGN")) {
                    Sign oldSign = (Sign) signBlock.getState();
                    signBlock.setType(signMaterial);
                    Sign sign = (Sign) signBlock.getState();
                    for (byte i = 0; i < oldSign.getSide(Side.FRONT).lines().size(); i++) {
                        sign.getSide(Side.FRONT).line(i,oldSign.getSide(Side.FRONT).line(i));
                    }
                    sign.getSide(Side.FRONT).setGlowingText(oldSign.getSide(Side.FRONT).isGlowingText());
                    BlockData blockData = signBlock.getBlockData();
                    ((Directional) blockData).setFacing(BlockFace.SOUTH);
                    sign.setBlockData(blockData);
                    sign.update();
                    PlayerUtils.translateBlockSign(signBlock);
                }
            }
        }
    }

    public List<Location> getPlacedExecutors(ExecutorCategory category) {
        Location begin = platformer.getPlatformBeginLocation(this);
        Location end = platformer.getPlatformEndLocation(this);
        List<Location> locations = new ArrayList<>();
        for (int z = begin.getBlockZ()+4; z <= end.getBlockZ()-4; z =z+4) {
            Block block = getWorld().getBlockAt(begin.getBlockX()+4,1,z);
            ExecutorCategory blockCategory = ExecutorCategory.getByMaterial(block.getType());
            if (blockCategory == category) {
                locations.add(block.getLocation());
            }
        }
        return locations;
    }

    public int getBeginCoordinate() {
        return platformer.getPlatformBeginLocation(this).getBlockX();
    }

    public int getEndCoordinate() {
        return platformer.getPlatformEndLocation(this).getBlockX();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public World getWorld() {
        return world;
    }

    public Location getSpawnLocation() {
        Location spawn = platformer.getPlatformBeginLocation(this).clone();
        spawn.add(2.5,1,2.5);
        spawn.setYaw(-45);
        spawn.setPitch(0);
        return spawn;
    }

    @Override
    public String toString() {
        return "DevPlatform x: " + x + " z: " + z;
    }
}
