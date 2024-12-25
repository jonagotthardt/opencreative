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
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetInfo;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
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
     * Creates planet's settings.yml file.
     * @param id        Planet's ID.
     * @param isLoaded  Create in planet's folder or in unloadedWorlds.
     * @param owner     Owner of new world.
     */
    public static void createWorldSettings(int id, boolean isLoaded, Player owner, World.Environment environment) {
        String worldFolderPath = Bukkit.getServer().getWorldContainer() + File.separator + (!isLoaded ? "unloadedWorlds" + File.separator : "") + "planet" + id + File.separator;
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
        config.set("world","planet"+id);
        config.set("creation-time",System.currentTimeMillis());
        config.set("last-activity-time",System.currentTimeMillis());
        config.set("name", MessageUtils.getLocaleMessage("creating-world.default-world-name").replace("%player%", owner.getName()));
        config.set("description", MessageUtils.getLocaleMessage("creating-world.default-world-description").replace("%player%", owner.getName()));
        config.set("icon", String.valueOf(Material.DIAMOND));
        config.set("sharing", String.valueOf(Planet.Sharing.PUBLIC));
        config.set("category", String.valueOf(PlanetInfo.Category.SANDBOX));
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
     * Creates planet's codeScript.yml file.
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
     Loads all planets to base. It contains planets from /unloadedWorlds/ and planets with loaded worlds.
     **/
    public static void loadPlanets() {
        OpenCreative.getPlugin().getLogger().info("Registering worlds to base...");
        try {
            File[] planetsFolders = getWorldsFolders(true);
            if (planetsFolders.length == 0) {
                OpenCreative.getPlugin().getLogger().info("No worlds have been detected.");
                return;
            }
            OpenCreative.getPlugin().getLogger().info("Found " + planetsFolders.length + " worlds, adding...");
            int corruptedWorlds = 0;
            int deprecatedWorlds = 0;
            long currentTime = System.currentTimeMillis();
            for (File planetFolder : planetsFolders) {
                String worldName = planetFolder.getPath().replace(Bukkit.getServer().getWorldContainer() + File.separator,"").replace("unloadedWorlds" + File.separator,"");
                if (!planetFolder.getPath().contains("unloadedWorlds")) {
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
                        id = Integer.parseInt(worldName.replace("planet",""));
                    } catch (NumberFormatException ignored) {}
                    Planet planet = new Planet(id);
                    if (planet.isCorrupted()) {
                        corruptedWorlds++;
                    } else if (currentTime- planet.getCreationTime() > 2592000000L) {
                        OfflinePlayer planetOwner = Bukkit.getOfflinePlayer(planet.getOwner());
                        if (planetOwner.getLastSeen() == 0 || currentTime-planetOwner.getLastSeen() > 2592000000L) {
                            deprecatedWorlds++;
                        }
                    }
                }
            }
            OpenCreative.getPlugin().getLogger().info("Loaded " + PlanetManager.getInstance().getPlanets().size() + " worlds for " + (System.currentTimeMillis()-currentTime) + " ms.");
            OpenCreative.getPlugin().getLogger().info(" Deprecated worlds: " + deprecatedWorlds);
            OpenCreative.getPlugin().getLogger().info(" Corrupted worlds: " + corruptedWorlds);
        } catch (Exception error) {
            sendCriticalErrorMessage("An error has occurred while loading worlds...",error);
        }
    }

    /**
     * Returns planet's folder, that stores planet's build world data, settings, script and players data.
     * @param planet planet to get folder.
     * @return planet's folder.
     */
    public static File getPlanetFolder(Planet planet) {
        return new File(getPlanetFolderPath(planet));
    }

    /**
     Returns development planet's folder. It contains world's map.
     **/
    public static File getDevPlanetFolder(DevPlanet devPlanet) {
        if (devPlanet.isLoaded()) {
            return new File(Bukkit.getServer().getWorldContainer() + File.separator + devPlanet.getWorldName() + File.separator);
        } else {
            return new File(Bukkit.getServer().getWorldContainer() + File.separator + "unloadedWorlds" + File.separator + devPlanet.getWorldName() + File.separator);
        }
    }

    /**
     Returns planet's settings.yml configuration.
     **/
    public static FileConfiguration getPlanetConfig(Planet planet) {
        File file = getPlanetConfigFile(planet);
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     Returns planet's settings.yml file.
     **/
    public static File getPlanetConfigFile(Planet planet) {
        return new File(getPlanetFolder(planet),"settings.yml");
    }

    /**
     Returns planet's codeScript.yml file.
     **/
    public static File getPlanetScriptFile(Planet planet) {
        File scriptFile = new File((getPlanetFolder(planet)),"codeScript.yml");
        if (!scriptFile.exists()) {
            createCodeScript(getPlanetFolder(planet).getPath(), planet.getWorldName());
        }
        return scriptFile;
    }

    /**
     Returns planet's variables.yml configuration.
     **/
    public static File getPlanetVariablesJson(Planet planet) {
        File variablesFile = new File(getPlanetFolder(planet),"variables.json");
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
     Returns player's data json from planet folder.
     **/
    public static File getPlayerDataJson(Planet planet, Player player) {
        File planetFolder = getPlanetFolder(planet);
        File folder = new File(planetFolder.getPath() + File.separator + "playersData");
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
     * Returns planets worlds folders.
     * @param includeUnloadedWorlds if true - includes unloaded worlds too, false - only loaded worlds.
     * @return planets worlds folders.
     */
    public static File[] getWorldsFolders(boolean includeUnloadedWorlds) {

        ArrayList<File> worldsFolders = new ArrayList<>();
        File serverDirectory = Bukkit.getServer().getWorldContainer();
        File[] serverDirectoryFiles = serverDirectory.listFiles();

        if (serverDirectoryFiles != null) {
            for (File file : serverDirectoryFiles) {
                if (file.getName().startsWith("plot")) {
                    File newFile = new File(file.getParent() + File.separator + file.getName().replace("plot","planet"));
                    file.renameTo(newFile);
                    file = newFile;
                }
                if (file.isDirectory() && file.getName().startsWith("planet")) worldsFolders.add(file);
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
                    if (file.getName().startsWith("plot")) {
                        File newFile = new File(file.getParent() + File.separator + file.getName().replace("plot","planet"));
                        file.renameTo(newFile);
                        file = newFile;                    }
                    if (file.isDirectory() && file.getName().startsWith("planet")) {
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
     * Unloads all loaded planets worlds into /unloadedWorlds/ directory.
     */
    public static void unloadPlanets() {
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
            PlanetManager.getInstance().clearPlanets();
        } catch (NullPointerException error) {
            sendCriticalErrorMessage("Error while unloading worls: " + error.getMessage());
        }
    }

    /**
     * Loads planet folder from /unloadedWorlds/ to server storage.
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
     * Unloads planet folder from server storage to /unloadedWorlds/ folder.
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
     * Sets parameter to Long value in planet's settings.
     * @param planet planet to set.
     * @param parameterPath path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlanetConfigParameter(Planet planet, String parameterPath, long parameterValue) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath,String.valueOf(parameterValue));
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.",error);
        }
    }

    /**
     * Sets parameter to Int value in planet's settings.
     * @param planet planet to set.
     * @param parameterPath path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlanetConfigParameter(Planet planet, String parameterPath, int parameterValue) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath,String.valueOf(parameterValue));
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.",error);
        }
    }

    /**
     * Sets parameter to Object value in planet's settings.
     * @param planet planet to set.
     * @param parameterPath path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlanetConfigParameter(Planet planet, String parameterPath, Object parameterValue) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath,parameterValue);
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.",error);
        }
    }

    /**
     * Sets parameter to String value in planet's settings.
     * @param planet planet to set.
     * @param parameterPath path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlanetConfigParameter(Planet planet, String parameterPath, String parameterValue) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath,parameterValue);
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.",error);
        }
    }

    /**
     * Sets parameter to List value in planet's settings.
     * @param planet planet to set.
     * @param parameterPath path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlanetConfigParameter(Planet planet, String parameterPath, List<String> parameterValue) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath,parameterValue);
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.",error);
        }
    }

    /**
     * Sets parameter to Set value in planet's settings.
     * @param planet planet to set.
     * @param parameterPath path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlanetConfigParameter(Planet planet, String parameterPath, Set<String> parameterValue) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath,new ArrayList<>(parameterValue));
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.",error);
        }
    }

    /**
     * Returns a specified list of players nicknames.
     * @param planet planet to get list.
     * @param type type of players list.
     * @return list of nicknames.
     */
    public static List<String> getPlayersFromPlanetList(Planet planet, Planet.PlayersType type) {
        return new ArrayList<>(getPlanetConfig(planet).getStringList(type.getPath()));
    }

    /**
     * Adds player to list, that located in planet's settings.yml file.
     * @param planet planet to add player.
     * @param nickname nickname of player.
     * @param type type of player list.
     * @return true - if successfully added, false - if failed.
     */
    public static boolean addPlayerInPlanetList(Planet planet, String nickname, Planet.PlayersType type) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        List<String> playersList = planetConfig.getStringList(type.getPath());
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
        setPlanetConfigParameter(planet,type.getPath(),playersList);
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
     * Returns file path of planet's world folder.
     * @param planet planet to get folder.
     * @return planet's folder path.
     */
    public static String getPlanetFolderPath(Planet planet) {
        return Bukkit.getWorldContainer().getPath() + File.separator + (!planet.isLoaded() ? "unloadedWorlds" + File.separator : "") + "planet" + planet.getId() + File.separator;
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
