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

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.coding.menus.layouts.Layout;
import mcchickenstudio.creative.utils.PlayerUtils;
import org.bukkit.*;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

import static mcchickenstudio.creative.utils.BlockUtils.getSignLine;
import static mcchickenstudio.creative.utils.ErrorUtils.sendCriticalErrorMessage;
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

    private final Plot linkedPlot;
    public final String worldName;

    public World world;
    public final Material floorBlockMaterial;
    public final Material eventBlockMaterial;
    public final Material actionBlockMaterial;

    public final Map<Player, Location> lastLocations = new HashMap<>();
    private final Map<Location, Layout> openedBlocksMenus = new HashMap<>();

    public static final List<DevPlot> devPlots = new ArrayList<>();
    private boolean isLoaded;

    public DevPlot(Plot plot) {

        this.linkedPlot = plot;
        this.worldName = plot.worldName + "dev";

        this.floorBlockMaterial = Material.WHITE_STAINED_GLASS;
        this.eventBlockMaterial = Material.LIGHT_BLUE_STAINED_GLASS;
        this.actionBlockMaterial = Material.LIGHT_GRAY_STAINED_GLASS;

        this.isLoaded = false;
        plot.devPlot = this;
        devPlots.add(this);

    }

    public void loadDevPlotWorld() {
        if (this.exists()) {
            if (loadWorldFolder(this.worldName, true)) {
                Bukkit.createWorld(new WorldCreator(this.worldName));
                this.world = Bukkit.getWorld(this.worldName);
                setupWorld();
            }
        } else {
            create();
            Bukkit.createWorld(new WorldCreator(this.worldName));
            this.world = Bukkit.getWorld(this.worldName);
            setupWorld();
        }
        this.isLoaded = true;
    }

    private void setupWorld() {
        this.world.setTime(12500);
        this.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
        this.world.setGameRule(GameRule.DO_WEATHER_CYCLE,false);
        this.world.getWorldBorder().setSize(120);
        this.world.getWorldBorder().setWarningDistance(0);
    }

    public void create() {

        File template = new File(Main.getPlugin().getDataFolder() + File.separator + "templates" + File.separator + "devWorld" + File.separator);
        File devWorldFolder = new File(Bukkit.getServer().getWorldContainer() + File.separator + this.worldName + File.separator);
        createCodeScript(devWorldFolder.getPath(),worldName);
        copyFilesToDirectory(template, devWorldFolder);

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
        int floors = 0;
        if (world != null) {
            for (int y = 0; y < 256; y=y+4) {
                if (world.getBlockAt(1, y, 1).getType() == Material.WHITE_STAINED_GLASS) {
                    floors++;
                } else {
                    break;
                }
            }
        }
        return floors;
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
                    block.setType(eventBlockMaterial);
                } else if (x > executorX && (x - executorX) % 2 == 0 && x < endX - 2 && (z - beginZ) % 4 == 0 && z != beginZ && z != endZ) {
                    block.setType(actionBlockMaterial);
                } else {
                    block.setType(floorBlockMaterial);
                }
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
        indestructibleBlocks.add(floorBlockMaterial);
        indestructibleBlocks.add(actionBlockMaterial);
        indestructibleBlocks.add(eventBlockMaterial);
        indestructibleBlocks.addAll(Arrays.stream(ExecutorCategory.values()).map(ExecutorCategory::getAdditionalBlock).toList());
        indestructibleBlocks.addAll(Arrays.stream(ActionCategory.values()).map(ActionCategory::getAdditionalBlock).toList());
        indestructibleBlocks.remove(Material.PISTON);
        return indestructibleBlocks;
    }

    public Set<Material> getAllowedBlocks() {
        Set<Material> allowedBlocks = new HashSet<>();
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
        return linkedPlot;
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
}
