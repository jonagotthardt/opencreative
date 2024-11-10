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

package mcchickenstudio.creative.plots;

import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.coding.menus.layouts.Layout;
import mcchickenstudio.creative.utils.DevPlotChunkGenerator;
import mcchickenstudio.creative.utils.PlayerUtils;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;

import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

import static mcchickenstudio.creative.utils.BlockUtils.getSignLine;
import static mcchickenstudio.creative.utils.FileUtils.*;

/**
 * <h1>DevPlot</h1>
 * This class represents developer's world, where players
 * can edit and change code with blocks on platform.
 * <p>
 * Platform consists of white, blue and gray stained glass
 * and it can't be destroyed. Players can place chests,
 * shulkers, signs and anvils on white stained glass.
 * On blue stained glass players should place executor blocks,
 * on gray stained glass - actions and conditions.
 * </p>
 */
public class DevPlot {

    private final Plot plot;
    public final String worldName;

    public World world;
    private Material floorBlockMaterial;
    private Material eventBlockMaterial;
    private Material actionBlockMaterial;
    private Material signBlockMaterial = Material.OAK_WALL_SIGN;
    private Material containerMaterial = Material.CHEST;

    public final Map<Player, Location> lastLocations = new HashMap<>();
    private final Map<Location, Layout> openedBlocksMenus = new HashMap<>();

    public static final List<DevPlot> devPlots = new ArrayList<>();
    private boolean isLoaded;

    public DevPlot(Plot plot) {

        this.plot = plot;
        this.worldName = plot.getWorldName() + "dev";

        this.floorBlockMaterial = Material.WHITE_STAINED_GLASS;
        this.eventBlockMaterial = Material.BLUE_STAINED_GLASS;
        this.actionBlockMaterial = Material.GRAY_STAINED_GLASS;

        this.isLoaded = false;
        devPlots.add(this);

    }

    public void loadDevPlotWorld() {
        if (this.exists()) {
            if (loadWorldFolder(this.worldName, true)) {
                Bukkit.createWorld(new WorldCreator(this.worldName));
                this.world = Bukkit.getWorld(this.worldName);
                if (world != null) {
                    setFloorBlockMaterial(world.getBlockAt(1,0,1).getType());
                    setEventBlockMaterial(world.getBlockAt(4,0,4).getType());
                    setActionBlockMaterial(world.getBlockAt(6,0,4).getType());
                    if (world.getBlockAt(4,0,4).getType() != getEventBlockMaterial()) {
                        createPlatform(1,1);
                    }
                    setupWorld();
                }
            }
        } else {
            this.world = Bukkit.createWorld(new WorldCreator(this.worldName).type(WorldType.FLAT).generator(new DevPlotChunkGenerator()).keepSpawnLoaded(TriState.FALSE));
            createPlatform(1,1);
            this.world.setTime(12500);
            setupWorld();
        }
        this.isLoaded = true;
    }

    private void setupWorld() {
        this.world.setSpawnLocation(2,1,2);
        this.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
        this.world.setGameRule(GameRule.DO_WEATHER_CYCLE,false);
        this.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
        this.world.setGameRule(GameRule.DO_MOB_SPAWNING,false);
        this.world.setGameRule(GameRule.DO_MOB_SPAWNING,false);
        this.world.setGameRule(GameRule.MOB_GRIEFING,false);
        this.world.setGameRule(GameRule.DO_PATROL_SPAWNING,false);
        this.world.setGameRule(GameRule.DO_FIRE_TICK,false);
        this.world.getWorldBorder().setSize(120);
        this.world.getWorldBorder().setCenter(50,50);
        this.world.getWorldBorder().setWarningDistance(0);
    }

    public boolean exists() {
        boolean exists = false;
        for (File folder : getWorldsFolders(true)) {
            if (folder.getName().equalsIgnoreCase(this.worldName)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    public int getFloors() {
        return 1;
    }

    public void translateCodingBlocks(Player player) {
        for (byte y = 1; y < getFloors() * 4; y = (byte) (y + 4)) {
            for (byte z = 4; z < 96; z = (byte) (z + 4)) {
                Block executorBlock = world.getBlockAt(4, y, z);
                PlayerUtils.translateBlockSign(executorBlock.getRelative(BlockFace.SOUTH),player);
                for (byte x = 6; x < 96; x = (byte) (x + 2)) {
                    Block actionBlock = world.getBlockAt(x,y,z);
                    PlayerUtils.translateBlockSign(actionBlock.getRelative(BlockFace.SOUTH),player);
                }
            }
        }
    }

    public Set<Material> getAllCodingBlocksForPlacing() {
        Set<Material> allBlocks = new HashSet<>();
        allBlocks.addAll(getEventsBlocks());
        allBlocks.addAll(getActionsBlocks());
        return allBlocks;
    }

    public Set<Material> getEventsBlocks() {
        return new HashSet<>(Arrays.stream(ExecutorCategory.values()).map(ExecutorCategory::getBlock).toList());
    }

    public Set<Material> getActionsBlocks() {
        return new HashSet<>(Arrays.stream(ActionCategory.values()).map(ActionCategory::getBlock).toList());
    }

    /**
     * Creates a coding platform with specified X and Z of platform.
     * <p>
     * By default, it generates floor with white stained-glass,
     * and fills executor sections with blue stained-glass,
     * action sections with gray stained-glass.</p>
     * @param platformX X number of platform.
     * @param platformZ Z number of platform.
     * @return true - if successfully created, false - if failed.
     */
    public boolean createPlatform(int platformX, int platformZ) {
        if (platformX >= 30 || platformZ >= 30 || platformX <= 0 || platformZ <= 0) {
            return false;
        }
        int beginX = getPlatformBeginCoordinate(platformX);
        int endX = getPlatformEndCoordinate(platformX);
        int beginZ = getPlatformBeginCoordinate(platformZ);
        int endZ = getPlatformEndCoordinate(platformZ);
        int executorX = beginX+4;
        for (int x = beginX; x <= endX; x++) {
            for (int z = beginZ; z <= endZ; z++) {
                Block block = world.getBlockAt(x,0,z);
                if (x == executorX && (z - beginZ) % 4 == 0 && z != beginZ && z != endZ) {
                    block.setType(getEventBlockMaterial());
                } else if (x > executorX && (x - executorX) % 2 == 0 && x < endX - 2 && (z - beginZ) % 4 == 0 && z != beginZ && z != endZ) {
                    block.setType(getActionBlockMaterial());
                } else {
                    block.setType(getFloorBlockMaterial());
                }
                block.setBiome(Biome.ICE_SPIKES);
            }
        }
        return true;
    }

    public int getPlatformBeginCoordinate(int platformNumber) {
        return (platformNumber - 1) * 102;
    }

    public int getPlatformEndCoordinate(int platformNumber) {
        return getPlatformBeginCoordinate(platformNumber) + 100;
    }

    public Set<Material> getIndestructibleBlocks() {
        Set<Material> indestructibleBlocks = new HashSet<>();
        indestructibleBlocks.add(getFloorBlockMaterial());
        indestructibleBlocks.add(getActionBlockMaterial());
        indestructibleBlocks.add(getEventBlockMaterial());
        indestructibleBlocks.addAll(Arrays.stream(ExecutorCategory.values()).map(ExecutorCategory::getAdditionalBlock).toList());
        indestructibleBlocks.addAll(Arrays.stream(ActionCategory.values()).map(ActionCategory::getAdditionalBlock).toList());
        indestructibleBlocks.remove(Material.PISTON);
        return indestructibleBlocks;
    }

    public Set<Material> getAllowedBlocks() {
        Set<Material> allowedBlocks = new HashSet<>();
        allowedBlocks.add(Material.LANTERN);
        allowedBlocks.add(Material.JACK_O_LANTERN);
        allowedBlocks.add(Material.SOUL_LANTERN);
        allowedBlocks.add(Material.TORCH);
        allowedBlocks.add(Material.SOUL_TORCH);
        allowedBlocks.add(Material.BARREL);
        allowedBlocks.add(Material.OAK_SIGN);
        allowedBlocks.add(Material.SPRUCE_SIGN);
        allowedBlocks.add(Material.ACACIA_SIGN);
        allowedBlocks.add(Material.BAMBOO_SIGN);
        allowedBlocks.add(Material.JUNGLE_SIGN);
        allowedBlocks.add(Material.CHERRY_SIGN);
        allowedBlocks.add(Material.WARPED_SIGN);
        allowedBlocks.add(Material.CRIMSON_SIGN);
        allowedBlocks.add(Material.MANGROVE_SIGN);
        allowedBlocks.add(Material.DARK_OAK_SIGN);
        allowedBlocks.add(Material.BIRCH_SIGN);
        allowedBlocks.add(Material.CRAFTING_TABLE);
        allowedBlocks.add(Material.JUKEBOX);
        allowedBlocks.add(Material.STONECUTTER);
        allowedBlocks.add(Material.CARTOGRAPHY_TABLE);
        allowedBlocks.add(Material.SMITHING_TABLE);
        allowedBlocks.add(Material.LOOM);
        allowedBlocks.add(Material.GRINDSTONE);
        allowedBlocks.add(Material.CHEST);
        allowedBlocks.add(Material.ANVIL);
        allowedBlocks.add(Material.CHIPPED_ANVIL);
        allowedBlocks.add(Material.DAMAGED_ANVIL);
        allowedBlocks.add(Material.ENDER_CHEST);
        allowedBlocks.add(Material.SHULKER_BOX);
        allowedBlocks.add(Material.WHITE_SHULKER_BOX);
        allowedBlocks.add(Material.BLACK_SHULKER_BOX);
        allowedBlocks.add(Material.BLUE_SHULKER_BOX);
        allowedBlocks.add(Material.BROWN_SHULKER_BOX);
        allowedBlocks.add(Material.CYAN_SHULKER_BOX);
        allowedBlocks.add(Material.MAGENTA_SHULKER_BOX);
        allowedBlocks.add(Material.GRAY_SHULKER_BOX);
        allowedBlocks.add(Material.GREEN_SHULKER_BOX);
        allowedBlocks.add(Material.LIME_SHULKER_BOX);
        allowedBlocks.add(Material.RED_SHULKER_BOX);
        allowedBlocks.add(Material.ORANGE_SHULKER_BOX);
        allowedBlocks.add(Material.PURPLE_SHULKER_BOX);
        allowedBlocks.add(Material.YELLOW_SHULKER_BOX);
        allowedBlocks.add(Material.LIGHT_BLUE_SHULKER_BOX);
        allowedBlocks.add(Material.LIGHT_GRAY_SHULKER_BOX);
        allowedBlocks.add(Material.PINK_SHULKER_BOX);
        return allowedBlocks;
    }

    public List<Location> getPlacedExecutors(ExecutorCategory category) {
        List<Location> locations = new ArrayList<>();
        byte x = 4;
        for (byte y = 1; y < getFloors()*4; y=(byte)(y+4)) {
            for (byte z = 4; z < 96; z = (byte) (z + 4)) {
                ExecutorCategory blockCategory = ExecutorCategory.getByMaterial(world.getBlockAt(x,y,z).getType());
                if (blockCategory == category) {
                    locations.add(world.getBlockAt(x,y,z).getLocation());
                }
            }
        }
        return locations;
    }

    public List<Location> getPlacedFunctions() {
        List<Location> locations = new ArrayList<>();
        byte x = 4;
        for (byte y = 1; y < getFloors()*4; y=(byte)(y+4)) {
            for (byte z = 4; z < 96; z = (byte) (z + 4)) {
                Block block = world.getBlockAt(x,y,z);
                ExecutorCategory blockCategory = ExecutorCategory.getByMaterial(block.getType());
                String line = getSignLine(block.getRelative(BlockFace.SOUTH).getLocation(),(byte) 3);
                if (blockCategory == ExecutorCategory.FUNCTION && line != null && !line.isEmpty()) {
                    locations.add(block.getLocation());
                }
            }
        }
        return locations;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public Plot getPlot() {
        return plot;
    }

    public Layout getOpenedMenu(Location location) {
        return openedBlocksMenus.get(location);
    }

    public void registerOpenedMenu(Location location, Layout menu) {
        openedBlocksMenus.put(location,menu);
    }

    public void unregisterOpenedMenu(Location location) {
        openedBlocksMenus.remove(location);
    }

    public Material getContainerMaterial() {
        return containerMaterial;
    }

    public boolean setContainerMaterial(Material containerMaterial) {
        if (containerMaterial == Material.BARREL || containerMaterial == Material.CHEST || containerMaterial == Material.TRAPPED_CHEST) {
            this.containerMaterial = containerMaterial;
            return true;
        }
        return false;
    }

    public boolean setSignMaterial(Material signMaterial) {
        if (signMaterial == Material.OAK_WALL_SIGN || signMaterial == Material.ACACIA_WALL_SIGN || signMaterial == Material.BAMBOO_WALL_SIGN || signMaterial == Material.CHERRY_WALL_SIGN || signMaterial == Material.BIRCH_WALL_SIGN || signMaterial == Material.JUNGLE_WALL_SIGN) {
            this.containerMaterial = signMaterial;
            return true;
        }
        return false;
    }

    public boolean setFloorEventActionBlocksMaterial(Material floorBlockMaterial, Material eventBlockMaterial, Material actionBlockMaterial) {
        if (!floorBlockMaterial.isBlock() || !eventBlockMaterial.isBlock() || !actionBlockMaterial.isBlock() || floorBlockMaterial == eventBlockMaterial || floorBlockMaterial == actionBlockMaterial || eventBlockMaterial == actionBlockMaterial) {
            return false;
        }
        if (floorBlockMaterial != Material.BARRIER && !floorBlockMaterial.name().endsWith("GLASS")) {
            return false;
        }
        if (eventBlockMaterial != Material.BARRIER && !eventBlockMaterial.name().endsWith("GLASS")) {
            return false;
        }
        if (actionBlockMaterial != Material.BARRIER && !actionBlockMaterial.name().endsWith("GLASS")) {
            return false;
        }
        this.floorBlockMaterial = floorBlockMaterial;
        this.eventBlockMaterial = eventBlockMaterial;
        this.actionBlockMaterial = actionBlockMaterial;
        return true;
    }

    public boolean setFloorBlockMaterial(Material floorBlockMaterial) {
        if (!floorBlockMaterial.isBlock() || floorBlockMaterial == eventBlockMaterial || floorBlockMaterial == actionBlockMaterial || floorBlockMaterial == containerMaterial) {
            return false;
        }
        if (floorBlockMaterial == Material.BARRIER || floorBlockMaterial.name().endsWith("GLASS")) {
            this.floorBlockMaterial = floorBlockMaterial;
            return true;
        }
        return false;
    }

    public boolean setEventBlockMaterial(Material eventBlockMaterial) {
        if (!eventBlockMaterial.isBlock() || eventBlockMaterial == floorBlockMaterial || eventBlockMaterial == actionBlockMaterial || eventBlockMaterial == containerMaterial) {
            return false;
        }
        if (eventBlockMaterial == Material.BARRIER || eventBlockMaterial.name().endsWith("GLASS")) {
            this.eventBlockMaterial = eventBlockMaterial;
            return true;
        }
        return false;
    }

    public boolean setActionBlockMaterial(Material actionBlockMaterial) {
        if (!actionBlockMaterial.isBlock() || actionBlockMaterial == eventBlockMaterial || actionBlockMaterial == floorBlockMaterial || actionBlockMaterial == containerMaterial) {
            return false;
        }
        if (actionBlockMaterial == Material.BARRIER || actionBlockMaterial.name().endsWith("GLASS")) {
            this.actionBlockMaterial = actionBlockMaterial;
            return true;
        }
        return false;
    }

    public Material getFloorBlockMaterial() {
        return floorBlockMaterial;
    }

    public Material getEventBlockMaterial() {
        return eventBlockMaterial;
    }

    public Material getActionBlockMaterial() {
        return actionBlockMaterial;
    }
}
