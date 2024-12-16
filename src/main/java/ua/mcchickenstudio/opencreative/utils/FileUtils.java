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

package ua.mcchickenstudio.opencreative.utils;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.plots.DevPlot;
import ua.mcchickenstudio.opencreative.plots.Plot;
import ua.mcchickenstudio.opencreative.plots.PlotInfo;
import ua.mcchickenstudio.opencreative.plots.PlotManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;

/**
 * <h1>FileUtils</h1>
 * This class contains utils for creating, reading, modifying
 * and removing files for worlds.
 */
public class FileUtils {

    /**
     * Creates plot's settings.yml file.
     * @param id        Plot's ID.
     * @param isLoaded  Create in plot's folder or in unloadedWorlds.
     * @param owner     Owner of new world.
     */
    public static void createWorldSettings(int id, boolean isLoaded, Player owner, World.Environment environment) {
        String worldFolderPath = Bukkit.getServer().getWorldContainer() + File.separator + (!isLoaded ? "unloadedWorlds" + File.separator : "") + "plot" + id + File.separator;
        File folder = new File(worldFolderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(worldFolderPath, "settings.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException error) {
                sendCriticalErrorMessage("Couldn't create a settings.yml for world " + id, error);
                return;
            }
        }
        FileConfiguration worldFile = YamlConfiguration.loadConfiguration(file);
        fillDefaultSettings(worldFile,id,owner,environment);
        try {
            worldFile.save(file);
        } catch (IOException | IllegalArgumentException error) {
            sendCriticalErrorMessage("Couldn't save world settings.yml for " + id,error);
        }
    }

    /**
     * Fills world's settings configuration with default values.
     * @param config settings configuration.
     * @param id world's id.
     * @param owner world's owner.
     * @param environment environment on world creation.
     */
    public static void fillDefaultSettings(FileConfiguration config, int id, Player owner, World.Environment environment) {
        config.set("owner", owner.getName());
        config.set("owner-group",OpenCreative.getSettings().getGroups().getGroup(owner).getName().toLowerCase());
        config.set("environment", environment.name());
        config.set("world","plot"+id);
        config.set("creation-time",System.currentTimeMillis());
        config.set("last-activity-time",System.currentTimeMillis());
        config.set("name", MessageUtils.getLocaleMessage("creating-world.default-world-name").replace("%player%", owner.getName()));
        config.set("description", MessageUtils.getLocaleMessage("creating-world.default-world-description").replace("%player%", owner.getName()));
        config.set("icon", String.valueOf(Material.DIAMOND));
        config.set("sharing", String.valueOf(Plot.Sharing.PUBLIC));
        config.set("category", String.valueOf(PlotInfo.Category.SANDBOX));
        config.set("customID",String.valueOf(id));
        config.set("players.unique", new ArrayList<String>());
        config.set("players.liked", new ArrayList<String>());
        config.set("players.builders.trusted", new ArrayList<String>());
        config.set("players.builders.not-trusted", new ArrayList<String>());
        config.set("players.developers.trusted", new ArrayList<String>());
        config.set("players.developers.not-trusted", new ArrayList<String>());
        config.set("players.whitelist", new ArrayList<String>());
        config.set("players.blacklist", new ArrayList<String>());
        config.set("flags",new HashMap<String,Integer>());
    }

    /**
     * Creates plot's codeScript.yml file.
     **/
    public static void createCodeScript(String path, String worldName) {
        File file = new File(path, "codeScript.yml");
        FileConfiguration worldFile = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException error) {
                sendCriticalErrorMessage("Couldn't create a codeScript.yml for world " + worldName + " because of IOException. Maybe it is already exists? " + error.getMessage());
                return;
            }
        }
        worldFile.set("world",worldName);
        worldFile.set("creation-time",System.currentTimeMillis());
        worldFile.set("last-activity-time",System.currentTimeMillis());
        worldFile.createSection("code");
        try {
            worldFile.save(file);
        } catch (IOException | IllegalArgumentException error) {
            sendCriticalErrorMessage("Couldn't save world codeScript.yml for " + worldName + " because of " + error.getClass().getName() + " " + error.getMessage());
        }
    }

    /**
     Loads localization file from OpenCreative/locales/ folder. If no localization file was found, then it creates a new one.
     **/
    public static void loadLocales() {
        OpenCreative.getPlugin().getLogger().info("Loading Creative localization file...");
        File folder = new File(OpenCreative.getPlugin().getDataFolder() + File.separator + "locales" + File.separator);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                sendCriticalErrorMessage("Couldn't create directory for locales... " + folder.getPath());
            }
        }
        String selectedLang = OpenCreative.getPlugin().getConfig().getString("messages.locale","en");
        File file = new File(folder.getPath() + File.separator + selectedLang + ".yml");
        if (!file.exists()) {
            setDefaultLocales();
            OpenCreative.getPlugin().getConfig().set("messages.locale","en");
        }
        MessageUtils.loadLocalizationFile();
        OpenCreative.getPlugin().getLogger().info("Loaded Creative localization file...");

    }

    private static void setDefaultLocales() {
        try {
            OpenCreative.getPlugin().saveResource("locales" + File.separator + "olden.yml",false);
            OpenCreative.getPlugin().saveResource("locales" + File.separator + "ru.yml",false);
        } catch (IllegalArgumentException error) {
            sendWarningErrorMessage("Couldn't save default localization file (resource) " + error.getClass().getName() + " " + error.getMessage());
        }
        OpenCreative.getPlugin().saveConfig();
    }

    /**
     Resets localization file from OpenCreative/locales/ folder. If localization file is detected in folder, then it will be removed and replaced with plugin's new one.
     **/
    public static void resetLocales() {
        OpenCreative.getPlugin().getLogger().info("Resseting Creative localization file...");
        File folder = new File(OpenCreative.getPlugin().getDataFolder() + File.separator + "locales" + File.separator);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                sendCriticalErrorMessage("Couldn't create directory for locales... " + folder.getPath());
            }
        }
        String selectedLang = OpenCreative.getPlugin().getConfig().getString("messages.locale","en");
        File file = new File(folder.getPath() + File.separator + selectedLang + ".yml");
        if (file.exists()) {
            file.delete();
        }
        setDefaultLocales();
        MessageUtils.loadLocalizationFile();
        OpenCreative.getPlugin().getLogger().info("Reset Creative localization file!");
    }

    /**
     Loads all plots to base. It contains plots from /unloadedWorlds/ and plots with loaded worlds.
     **/
    public static void loadPlots() {
        OpenCreative.getPlugin().getLogger().info("Registering worlds to base...");
        try {
            File[] plotsFolders = getWorldsFolders(true);
            if (plotsFolders.length == 0) {
                OpenCreative.getPlugin().getLogger().info("No worlds have been detected.");
                return;
            }
            OpenCreative.getPlugin().getLogger().info("Found " + plotsFolders.length + " worlds, adding...");
            int corruptedWorlds = 0;
            int deprecatedWorlds = 0;
            long currentTime = System.currentTimeMillis();
            for (File plotFolder : plotsFolders) {
                String worldName = plotFolder.getPath().replace(Bukkit.getServer().getWorldContainer() + File.separator,"").replace("unloadedWorlds" + File.separator,"");
                if (!plotFolder.getPath().contains("unloadedWorlds")) {
                    OpenCreative.getPlugin().getLogger().info("Moving loaded world " + worldName + " to unloadedWorlds folder...");
                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        for (Player player : world.getPlayers()) {
                            teleportToLobby(player);
                        }
                    }
                    unloadWorldFolder(worldName,true);
                }
                if (!worldName.endsWith("dev")) {
                    OpenCreative.getPlugin().getLogger().info("Adding world " + worldName + " to base...");
                    int id = -1;
                    try {
                        id = Integer.parseInt(worldName.replace("plot",""));
                    } catch (NumberFormatException ignored) {}
                    Plot plot = new Plot(id);
                    if (plot.isCorrupted()) {
                        corruptedWorlds++;
                    } else if (currentTime-plot.getCreationTime() > 2592000000L) {
                        OfflinePlayer plotOwner = Bukkit.getOfflinePlayer(plot.getOwner());
                        if (plotOwner.getLastSeen() == 0 || currentTime-plotOwner.getLastSeen() > 2592000000L) {
                            deprecatedWorlds++;
                        }
                    }
                }
            }
            OpenCreative.getPlugin().getLogger().info("Loaded " + PlotManager.getInstance().getPlots().size() + " worlds for " + (System.currentTimeMillis()-currentTime) + " ms.");
            OpenCreative.getPlugin().getLogger().info(" Deprecated worlds: " + deprecatedWorlds);
            OpenCreative.getPlugin().getLogger().info(" Corrupted worlds: " + corruptedWorlds);
        } catch (Exception error) {
            sendCriticalErrorMessage("An error has occurred while loading worlds...",error);
        }
    }

    /**
     * Returns plot's folder, that stores plot's build world data, settings, script and players data.
     * @param plot plot to get folder.
     * @return plot's folder.
     */
    public static File getPlotFolder(Plot plot) {
        return new File(getPlotFolderPath(plot));
    }

    /**
     Returns development plot's folder. It contains world's map.
     **/
    public static File getDevPlotFolder(DevPlot devPlot) {
        if (devPlot.isLoaded()) {
            return new File(Bukkit.getServer().getWorldContainer() + File.separator + devPlot.getWorldName() + File.separator);
        } else {
            return new File(Bukkit.getServer().getWorldContainer() + File.separator + "unloadedWorlds" + File.separator + devPlot.getWorldName() + File.separator);
        }
    }

    /**
     Returns plot's settings.yml configuration.
     **/
    public static FileConfiguration getPlotConfig(Plot plot) {
        File file = getPlotConfigFile(plot);
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     Returns plot's settings.yml file.
     **/
    public static File getPlotConfigFile(Plot plot) {
        return new File(getPlotFolder(plot),"settings.yml");
    }

    /**
     Returns plot's codeScript.yml file.
     **/
    public static File getPlotScriptFile(Plot plot) {
        File scriptFile = new File((getPlotFolder(plot)),"codeScript.yml");
        if (!scriptFile.exists()) {
            createCodeScript(getPlotFolder(plot).getPath(), plot.getWorldName());
        }
        return scriptFile;
    }

    /**
     Returns plot's variables.yml configuration.
     **/
    public static File getPlotVariablesJson(Plot plot) {
        File variablesFile = new File(getPlotFolder(plot),"variables.json");
        if (!variablesFile.exists()) {
            try {
                variablesFile.createNewFile();
            } catch (Exception error) {
                sendCriticalErrorMessage("Failed to create world's variables file.",error);
                return null;
            }
        }
        return variablesFile;
    }

    /**
     Returns player's data json from plot folder.
     **/
    public static File getPlayerDataJson(Plot plot, Player player) {
        File plotFolder = getPlotFolder(plot);
        File folder = new File(plotFolder.getPath() + File.separator + "playersData");
        try {
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File dataFile = new File(folder,  player.getUniqueId()+ ".json");
            if (dataFile.exists()) {
                return dataFile;
            } else {
                dataFile.createNewFile();
                return dataFile;
            }
        } catch (IOException error) {
            return null;
        }
    }

    /**
     * Returns plots worlds folders.
     * @param includeUnloadedWorlds if true - includes unloaded worlds too, false - only loaded worlds.
     * @return plots worlds folders.
     */
    public static File[] getWorldsFolders(boolean includeUnloadedWorlds) {

        ArrayList<File> worldsFolders = new ArrayList<>();
        File serverDirectory = Bukkit.getServer().getWorldContainer();
        File[] serverDirectoryFiles = serverDirectory.listFiles();

        if (serverDirectoryFiles != null) {
            for (File file : serverDirectoryFiles) {
                if (file.isDirectory() && file.getName().startsWith("plot")) worldsFolders.add(file);
            }
        } else {
            sendCriticalErrorMessage("World container of server is null.");
        }

        if (includeUnloadedWorlds) {
            File unloadedWorldsFolder = new File(serverDirectory + File.separator + "unloadedWorlds" + File.separator);
            if (!unloadedWorldsFolder.exists()) {
                unloadedWorldsFolder.mkdirs();
            }
            File[] unloadedWorlds = unloadedWorldsFolder.listFiles();

            if (unloadedWorlds != null) {
                File unloadedWorldFolder = new File(serverDirectory + File.separator + "unloadedWorlds" + File.separator);
                if (!unloadedWorldFolder.exists())  {
                    if (!unloadedWorldFolder.mkdirs()) sendCriticalErrorMessage("Can't create /unloadedWorlds/ folder");
                }
                for (File file : unloadedWorlds) {
                    if (file.isDirectory() && file.getName().startsWith("plot")) {
                        worldsFolders.add(file);
                    }
                }
            } else {
                sendCriticalErrorMessage(unloadedWorldsFolder.getPath() + " is null.");
            }
        }
        return worldsFolders.toArray(new File[0]);
    }

    /**
     * Unloads all loaded plots worlds into /unloadedWorlds/ directory.
     */
    public static void unloadPlots() {
        OpenCreative.getPlugin().getLogger().info("Creative+ is unloading worlds, please wait...");
        try {
            File[] worldsFiles = getWorldsFolders(false);
            if (worldsFiles.length > 0) {
                for (File file : worldsFiles) {
                    String worldName = file.getPath().replace(Bukkit.getServer().getWorldContainer() + File.separator,"");
                        try {
                            OpenCreative.getPlugin().getLogger().info("Unloading Creative world " + worldName + "...");
                            Bukkit.unloadWorld(worldName,true);
                            unloadWorldFolder(worldName,true);
                        } catch (Exception error) {
                            OpenCreative.getPlugin().getLogger().severe("An error has occurred when unloading world " + worldName + ": " + error.getMessage());
                        }
                }
            } else {
                OpenCreative.getPlugin().getLogger().info("No worlds been detected ;(");
            }
            PlotManager.getInstance().clearPlots();
        } catch (NullPointerException error) {
            sendCriticalErrorMessage("Error while unloading worls: " + error.getMessage());
        }
    }

    /**
     * Loads plot folder from /unloadedWorlds/ to server storage.
     **/
    public static boolean loadWorldFolder(String worldName, boolean removeUnloadedFolder) {
        Path serverPath = Bukkit.getServer().getWorldContainer().toPath();
        File unloadedWorldFolder = new File(serverPath + File.separator + "unloadedWorlds" + File.separator + worldName);
        File worldFolder = new File(serverPath + File.separator + worldName);

        if (copyFilesToDirectory(unloadedWorldFolder,worldFolder)) {
            if (!removeUnloadedFolder) return true;
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(unloadedWorldFolder);
                return true;
            } catch (IOException error) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Unloads plot folder from server storage to /unloadedWorlds/ folder.
     **/
    public static void unloadWorldFolder(String worldName, boolean removeWorldFolder) {
        Path serverPath = Bukkit.getServer().getWorldContainer().toPath();
        File worldFolder = new File(serverPath + File.separator + worldName);
        File unloadedWorldsFolder = new File(serverPath + File.separator + "unloadedWorlds" + File.separator + worldName);
        if (copyFilesToDirectory(worldFolder,unloadedWorldsFolder)) {
            if (!removeWorldFolder) return;
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(worldFolder);
            } catch (IOException error) {
                sendCriticalErrorMessage("Can't delete directory on unloading world folder " + worldFolder.getPath(), error);
            }
        }
    }

    /**
     * Copies input files into output directory.
     **/
    public static boolean copyFilesToDirectory(File input, File output) {
        try {
            File[] inputFiles = input.listFiles();
            if (!output.exists()) {
                if (!output.mkdirs()) sendCriticalErrorMessage("Can't create a output directory " + output.getPath() + " for copying from input " + input.getPath());
            }
            if (inputFiles != null) {
                for (File worldFile : inputFiles) {
                    if (worldFile.isDirectory()) org.apache.commons.io.FileUtils.copyDirectoryToDirectory(worldFile,output);
                    else org.apache.commons.io.FileUtils.copyFileToDirectory(worldFile,output);
                }
            }
            return true;
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't copy files from directory " + input.getPath() + " to directory: " + output.getPath(),error);
            return false;
        }
    }

    /**
     * Deletes Minecraft files, that are interrupting world copying process.
     *
     * @param worldFolder folder of world.
     */
    public static void deleteUnnecessaryWorldFiles(File worldFolder) {
        try {
            if (!worldFolder.exists()) return;
            File uidFile = new File(worldFolder,"uid.dat");
            File sessionFile = new File(worldFolder,"session.lock");
            uidFile.delete();
            sessionFile.delete();
        } catch (Exception error) {
            sendCriticalErrorMessage("Cannot delete uid.dat file.",error);
        }
    }

    /**
     * Deletes directory and files inside it, if exists.
     * @param directory path of directory.
     */
    public static void deleteFolder(File directory) {
        if (!directory.exists()) return;
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(directory);
        } catch (IOException error) {
            sendCriticalErrorMessage("Couldn't delete a folder with path " + directory.getPath(),error);
        }
    }

    /**
     * Sets parameter to Long value in plot's settings.
     * @param plot plot to set.
     * @param parameterPath path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlotConfigParameter(Plot plot, String parameterPath, long parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        plotConfig.set(parameterPath,String.valueOf(parameterValue));
        try {
            plotConfig.save(plotConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save plot's settings configuration to file.",error);
        }
    }

    /**
     * Sets parameter to Int value in plot's settings.
     * @param plot plot to set.
     * @param parameterPath path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlotConfigParameter(Plot plot, String parameterPath, int parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        plotConfig.set(parameterPath,String.valueOf(parameterValue));
        try {
            plotConfig.save(plotConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save plot's settings configuration to file.",error);
        }
    }

    /**
     * Sets parameter to Object value in plot's settings.
     * @param plot plot to set.
     * @param parameterPath path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlotConfigParameter(Plot plot, String parameterPath, Object parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        plotConfig.set(parameterPath,String.valueOf(parameterValue));
        try {
            plotConfig.save(plotConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save plot's settings configuration to file.",error);
        }
    }

    /**
     * Sets parameter to String value in plot's settings.
     * @param plot plot to set.
     * @param parameterPath path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlotConfigParameter(Plot plot, String parameterPath, String parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        plotConfig.set(parameterPath,parameterValue);
        try {
            plotConfig.save(plotConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save plot's settings configuration to file.",error);
        }
    }

    /**
     * Sets parameter to List value in plot's settings.
     * @param plot plot to set.
     * @param parameterPath path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlotConfigParameter(Plot plot, String parameterPath, List<String> parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        plotConfig.set(parameterPath,parameterValue);
        try {
            plotConfig.save(plotConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save plot's settings configuration to file.",error);
        }
    }

    /**
     * Sets parameter to Set value in plot's settings.
     * @param plot plot to set.
     * @param parameterPath path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlotConfigParameter(Plot plot, String parameterPath, Set<String> parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        plotConfig.set(parameterPath,new ArrayList<>(parameterValue));
        try {
            plotConfig.save(plotConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save plot's settings configuration to file.",error);
        }
    }

    /**
     * Returns a specified list of players nicknames.
     * @param plot plot to get list.
     * @param type type of players list.
     * @return list of nicknames.
     */
    public static List<String> getPlayersFromPlotList(Plot plot, Plot.PlayersType type) {
        return new ArrayList<>(getPlotConfig(plot).getStringList(type.getPath()));
    }

    /**
     * Adds player to list, that located in plot's settings.yml file.
     * @param plot plot to add player.
     * @param nickname nickname of player.
     * @param type type of player list.
     * @return true - if successfully added, false - if failed.
     */
    public static boolean addPlayerInPlotList(Plot plot, String nickname, Plot.PlayersType type) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        List<String> playersList = plotConfig.getStringList(type.getPath());
        for (String player : playersList) {
            /*
             * We will not add player, if list
             * already contains him.
             */
            if (player.equalsIgnoreCase(nickname)) {
                return false;
            }
        }
        playersList.add(nickname);
        setPlotConfigParameter(plot,type.getPath(),playersList);
        return true;
    }

    /**
     * Returns size of folder.
     * @param file folder to get size.
     * @return size of folder.
     */
    public static long getFolderSize(File file) {
        try {
            return org.apache.commons.io.FileUtils.sizeOfDirectory(file);
        } catch (Exception exception) {
            return 0;
        }
    }

    /**
     * Returns size of file.
     * @param file file to get size.
     * @return size of file.
     */
    public static long getFileSize(File file) {
        try {
            return org.apache.commons.io.FileUtils.sizeOf(file);
        } catch (Exception exception) {
            return 0;
        }
    }

    /**
     * Returns file path of plot's world folder.
     * @param plot plot to get folder.
     * @return plot's folder path.
     */
    public static String getPlotFolderPath(Plot plot) {
        return Bukkit.getWorldContainer().getPath() + File.separator + (!plot.isLoaded() ? "unloadedWorlds" + File.separator : "") + "plot" + plot.getId() + File.separator;
    }

    /**
     * Returns JSON file of player's profile.
     * @param uuid uuid of player.
     * @return json file.
     */
    public static File getProfileJson(String uuid) {
        File folder = new File(OpenCreative.getPlugin().getDataFolder().getPath() + File.separator + "profiles");
        try {
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File profileFile = new File(folder,  uuid+ ".json");
            if (profileFile.exists()) {
                return profileFile;
            } else {
                profileFile.createNewFile();
                return profileFile;
            }
        } catch (IOException error) {
            return null;
        }
    }

}
