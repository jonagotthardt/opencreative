/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package mcchickenstudio.creative.plots;

import mcchickenstudio.creative.Main;
import org.bukkit.*;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.FileUtils.*;

public class DevPlot {
    public Plot linkedPlot;
    public String worldName;

    public World world;
    public boolean isLoaded;
    public Material floorBlockMaterial;
    public Material eventBlockMaterial;
    public Material actionBlockMaterial;

    static final Plugin plugin = Main.getPlugin();

    public static List<DevPlot> devPlots = new ArrayList<>();

    public DevPlot(Plot plot) {

        this.linkedPlot = plot;
        this.worldName = plot.worldName + "dev";
        this.isLoaded = !(Bukkit.getWorld(this.worldName) == null);

        this.floorBlockMaterial = Material.WHITE_STAINED_GLASS;
        this.eventBlockMaterial = Material.LIGHT_BLUE_STAINED_GLASS;
        this.actionBlockMaterial = Material.LIGHT_GRAY_STAINED_GLASS;

        plot.devPlot = this;
        devPlots.add(this);

    }

    public void loadDevPlotWorld() {
        if (this.exists()) {
            if (loadWorldFolder(this.worldName, true)) {
                Bukkit.createWorld(new WorldCreator(this.worldName));
                this.world = Bukkit.getWorld(this.worldName);
                this.isLoaded = true;
            }
        } else {
            createDevPlot();
            Bukkit.createWorld(new WorldCreator(this.worldName));
            this.world = Bukkit.getWorld(this.worldName);
            this.isLoaded = true;
        }
    }

    public void createDevPlot() {

        File template = new File(plugin.getDataFolder() + File.separator + "templates" + File.separator + "devWorld" + File.separator);
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

    public List<Material> getAllCodingBlocksForPlacing() {
        List<Material> allBlocks = new ArrayList<>();
        allBlocks.addAll(getEventsBlocks());
        allBlocks.addAll(getActionsBlocks());
        allBlocks.addAll(getConditionBlocks());
        return allBlocks;
    }

    public List<Material> getEventsBlocks() {
        List<Material> eventsBlocks = new ArrayList<>();
        eventsBlocks.add(Material.DIAMOND_BLOCK);
        eventsBlocks.add(Material.GOLD_BLOCK);
        eventsBlocks.add(Material.EMERALD_BLOCK);
        eventsBlocks.add(Material.LAPIS_BLOCK);
        return eventsBlocks;
    }

    public List<Material> getActionsBlocks() {
        List<Material> actionsBlocks = new ArrayList<>();
        actionsBlocks.add(Material.COBBLESTONE);
        actionsBlocks.add(Material.LAPIS_ORE);
        actionsBlocks.add(Material.IRON_BLOCK);
        actionsBlocks.add(Material.NETHER_BRICKS);
        return actionsBlocks;
    }

    public List<Material> getConditionBlocks() {
        List<Material> conditionsBlocks = new ArrayList<>();
        conditionsBlocks.add(Material.OAK_PLANKS);
        conditionsBlocks.add(Material.OBSIDIAN);
        conditionsBlocks.add(Material.BRICKS);
        return conditionsBlocks;
    }

    public List<Material> getIndestructibleBlocks() {
        List<Material> indestructibleBlocks = new ArrayList<>();
        indestructibleBlocks.add(floorBlockMaterial);
        indestructibleBlocks.add(actionBlockMaterial);
        indestructibleBlocks.add(eventBlockMaterial);
        indestructibleBlocks.add(Material.DIAMOND_ORE);
        indestructibleBlocks.add(Material.IRON_ORE);
        indestructibleBlocks.add(Material.STONE);
        indestructibleBlocks.add(Material.NETHERRACK);
        return indestructibleBlocks;
    }

    public List<Material> getAllowedBlocks() {
        List<Material> allowedBlocks = new ArrayList<>();
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


}
