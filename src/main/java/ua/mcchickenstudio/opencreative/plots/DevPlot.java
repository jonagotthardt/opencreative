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

package ua.mcchickenstudio.opencreative.plots;

import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.Layout;
import ua.mcchickenstudio.opencreative.utils.DevPlotChunkGenerator;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;

import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

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

    private World world;
    private Material containerMaterial = Material.CHEST;

    private final Map<Player, Location> lastLocations = new HashMap<>();
    private final Map<Location, Layout> openedBlocksMenus = new HashMap<>();

    private final static Material DEFAULT_EVENT_MATERIAL = Material.BLUE_STAINED_GLASS;
    private final static Material DEFAULT_ACTION_MATERIAL = Material.GRAY_STAINED_GLASS;
    private final static Material DEFAULT_FLOOR_MATERIAL = Material.WHITE_STAINED_GLASS;

    public DevPlot(Plot plot) {

        this.plot = plot;

    }

    public void loadDevPlotWorld() {
        if (this.exists()) {
            if (loadWorldFolder(this.getWorldName(), true)) {
                Bukkit.createWorld(new WorldCreator(this.getWorldName()).type(WorldType.FLAT).generator(new DevPlotChunkGenerator()));
                this.setWorld(Bukkit.getWorld(this.getWorldName()));
                if (getWorld() != null) {
                    if (getWorld().getBlockAt(4,0,4).isEmpty()) {
                        createPlatform(1,1);
                    }
                    setupWorld();
                }
            }
        } else {
            this.setWorld(Bukkit.createWorld(new WorldCreator(this.getWorldName()).type(WorldType.FLAT).generator(new DevPlotChunkGenerator()).keepSpawnLoaded(TriState.FALSE)));
            createPlatform(1,1);
            this.getWorld().setTime(12500);
            setupWorld();
        }
    }

    private void setupWorld() {
        this.getWorld().setSpawnLocation(2,1,2);
        this.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
        this.getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE,false);
        this.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
        this.getWorld().setGameRule(GameRule.DO_MOB_SPAWNING,false);
        this.getWorld().setGameRule(GameRule.DO_MOB_SPAWNING,false);
        this.getWorld().setGameRule(GameRule.MOB_GRIEFING,false);
        this.getWorld().setGameRule(GameRule.DO_PATROL_SPAWNING,false);
        this.getWorld().setGameRule(GameRule.DO_FIRE_TICK,false);
        setWorldBorder();

    }

    public boolean exists() {
        boolean exists = false;
        for (File folder : getWorldsFolders(true)) {
            if (folder.getName().equalsIgnoreCase(this.getWorldName())) {
                exists = true;
                break;
            }
        }
        return exists;
    }


    public void translateCodingBlocks(Player player) {
        for (byte z = 4; z < 96; z = (byte) (z + 4)) {
            Block executorBlock = getWorld().getBlockAt(4, 1, z);
            PlayerUtils.translateBlockSign(executorBlock.getRelative(BlockFace.SOUTH),player);
            for (byte x = 6; x < 96; x = (byte) (x + 2)) {
                Block actionBlock = getWorld().getBlockAt(x,1,z);
                PlayerUtils.translateBlockSign(actionBlock.getRelative(BlockFace.SOUTH),player);
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
                Block block = getWorld().getBlockAt(x,0,z);
                if (x == executorX && (z - beginZ) % 4 == 0 && z != beginZ && z != endZ) {
                    block.setType(DEFAULT_EVENT_MATERIAL);
                } else if (x > executorX && (x - executorX) % 2 == 0 && x < endX - 2 && (z - beginZ) % 4 == 0 && z != beginZ && z != endZ) {
                    block.setType(DEFAULT_ACTION_MATERIAL);
                } else {
                    block.setType(DEFAULT_FLOOR_MATERIAL);
                }
                block.setBiome(Biome.ICE_SPIKES);
            }
        }
        return true;
    }

    public boolean claimPlatform(DevPlatform platform, Player player) {
        if (platform.exists()) return false;
        player.setAllowFlight(true);
        player.setFlying(true);
        platform.build(DEFAULT_FLOOR_MATERIAL,DEFAULT_EVENT_MATERIAL,DEFAULT_ACTION_MATERIAL);
        setWorldBorder();
        player.teleport(platform.getSpawnLocation());
        for (Player developer : getWorld().getPlayers()) {
            WorldBorder border = Bukkit.createWorldBorder();
            border.setCenter(getWorld().getWorldBorder().getCenter());
            border.setSize(getWorld().getWorldBorder().getSize()*5);
            developer.setWorldBorder(border);
        }
        player.sendMessage(getLocaleMessage("environment.platform.claimed"));
        player.playSound(player.getLocation(),Sound.UI_TOAST_CHALLENGE_COMPLETE,100,1.7f);
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
        indestructibleBlocks.add(DEFAULT_ACTION_MATERIAL);
        indestructibleBlocks.add(DEFAULT_EVENT_MATERIAL);
        indestructibleBlocks.add(DEFAULT_FLOOR_MATERIAL);
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
        for (byte z = 4; z < 96; z = (byte) (z + 4)) {
            ExecutorCategory blockCategory = ExecutorCategory.getByMaterial(getWorld().getBlockAt(x,1,z).getType());
            if (blockCategory == category) {
                locations.add(getWorld().getBlockAt(x,1,z).getLocation());
            }
        }
        return locations;
    }

    public List<Location> getPlacedFunctions() {
        List<Location> locations = new ArrayList<>();
        int x = 4;
        for (DevPlatform platform : getPlatforms()) {
            for (int z = platform.getBeginZ()+4; z < platform.getEndZ()-4; z =z+4) {
                Block block = getWorld().getBlockAt(x,1,z);
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
        return Bukkit.getWorld(getWorldName()) != null;
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

    public String getWorldName() {
        return plot.getWorldName()+"dev";
    }

    public Set<DevPlatform> getPlatforms() {
        Set<DevPlatform> platforms = new HashSet<>();
        if (!isLoaded()) return platforms;
        for (int x = 10; x >= 1; x--) {
            for (int z = 10; z >= 1; z--) {
                DevPlatform platform = new DevPlatform(getWorld(),x,z);
                if (platform.exists()) {
                    platforms.add(platform);
                }
            }
        }
        return platforms;
    }

    public DevPlatform getFarPlatformByX() {
        DevPlatform farPlatform = new DevPlatform(getWorld(),1,1);
        if (!isLoaded()) return farPlatform;
        for (int x = 2; x <= 10; x++) {
            DevPlatform current = new DevPlatform(getWorld(),x,1);
            if (current.exists()) {
                farPlatform = current;
            }
        }
        return farPlatform;
    }

    public DevPlatform getFarPlatformByZ() {
        DevPlatform farPlatform = new DevPlatform(getWorld(),1,1);
        if (!isLoaded()) return farPlatform;
        for (int z = 2; z <= 10; z++) {
            DevPlatform current = new DevPlatform(getWorld(),1,z);
            if (current.exists()) {
                farPlatform = current;
            }
        }
        return farPlatform;
    }

    public DevPlatform getPlatformInLocation(double x, double z) {
        for (DevPlatform platform : getPlatforms()) {
            if (x >= platform.getBeginX() && x <= platform.getEndX()) {
                if (z >= platform.getBeginZ() && z <= platform.getEndZ()) {
                    return platform;
                }
            }
        }
        return null;
    }

    public DevPlatform getPlatformInLocation(Location location) {
        return getPlatformInLocation(location.getX(),location.getZ());
    }

    public void setWorldBorder() {
        getWorld().getWorldBorder().setWarningDistance(0);
        getWorld().getWorldBorder().setCenter(50,50);
        getWorld().getWorldBorder().setSize(120);
        DevPlatform platformZ = getFarPlatformByZ();
        DevPlatform platformX = getFarPlatformByX();
        double endZ = platformZ.getEndZ();
        double endX = platformX.getEndX();
        /*
         * We find center of world border by dividing
         * most far platform end coordinate by 2.
         * (the start coordinate is 0)
         */
        double centerZ = endZ/2;
        double centerX = endX/2;
        getWorld().getWorldBorder().setCenter(centerX,centerZ);
        /*
         * We find size by subtracting most far
         * coordinate with center coordinate.
         */
        double size = ((Math.max(endX, endZ))-(Math.max(centerX, centerZ)))*2+20;
        getWorld().getWorldBorder().setSize(size);
    }

    public static Material getDefaultActionMaterial() {
        return DEFAULT_ACTION_MATERIAL;
    }

    public static Material getDefaultEventMaterial() {
        return DEFAULT_EVENT_MATERIAL;
    }

    public static Material getDefaultFloorMaterial() {
        return DEFAULT_FLOOR_MATERIAL;
    }

    public Map<Player, Location> getLastLocations() {
        return lastLocations;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
