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

package ua.mcchickenstudio.opencreative.utils;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.indev.modules.Module;
import ua.mcchickenstudio.opencreative.indev.modules.ModuleManager;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetInfo;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningErrorMessage;

/**
 * <h1>FileUtils</h1>
 * This class contains utils for creating, reading, modifying
 * and removing files for worlds.
 */
public class FileUtils {

    /**
     * Creates planet's settings.yml file.
     * @param id        Planet's ID.
     * @param owner     Owner of new world.
     */
    public static void createWorldSettings(int id, Player owner, World.Environment environment) {
        String worldFolderPath = getPlanetsStorageFolder().getPath() + File.separator  + "planet" + id + File.separator;
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
        config.set("owner-uuid", owner.getUniqueId().toString());
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
                sendCriticalErrorMessage("Couldn't create a codeScript.yml for planet " + getPlanetIdFromName(worldName) + " because of IOException. Maybe it is already exists? " + error.getMessage());
                return;
            }
        }
        worldFile.set("world",getPlanetIdFromName(worldName));
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
     Loads all planets to base.
     **/
    public static void loadPlanets() {
        OpenCreative.getPlugin().getLogger().info("Registering worlds to base...");
        try {
            convertOldPlanetFolders();
            File[] planetsFolders = getWorldsFolders();
            if (planetsFolders.length == 0) {
                OpenCreative.getPlugin().getLogger().info("No worlds have been detected.");
                return;
            }
            OpenCreative.getPlugin().getLogger().info("Found " + planetsFolders.length + " worlds, adding...");
            int corruptedWorlds = 0;
            int deprecatedWorlds = 0;
            long currentTime = System.currentTimeMillis();
            for (File planetFolder : planetsFolders) {
                String worldName = planetFolder.getPath().replace(Bukkit.getServer().getWorldContainer() + File.separator,"").replace("planets" + File.separator,"");
                if (!worldName.endsWith("dev")) {
                    OpenCreative.getPlugin().getLogger().info("Adding world " + worldName + " to base...");
                    int id = -1;
                    try {
                        id = Integer.parseInt(worldName.replace("planet",""));
                    } catch (NumberFormatException ignored) {}
                    if (id == -1) continue;
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
     Loads all modules to base.
     **/
    public static void loadModules() {
        OpenCreative.getPlugin().getLogger().info("Registering modules to base...");
        try {
            File[] modulesList = getModulesStorageFolder().listFiles();
            if (modulesList == null) {
                OpenCreative.getPlugin().getLogger().info("No modules have been detected.");
                return;
            }
            OpenCreative.getPlugin().getLogger().info("Found " + modulesList.length + " modules, adding...");
            long currentTime = System.currentTimeMillis();
            for (File moduleFile : getModulesFiles()) {
                String moduleName = moduleFile.getPath()
                        .replace(Bukkit.getServer().getWorldContainer() + File.separator,"")
                        .replace("modules" + File.separator,"")
                        .replace(".yml","");
                OpenCreative.getPlugin().getLogger().info("Adding module " + moduleName + " to base...");
                int id = -1;
                try {
                    id = Integer.parseInt(moduleName.replace("module",""));
                } catch (NumberFormatException ignored) {}
                if (id == -1) continue;
                Module module = new Module(id);
                ModuleManager.getInstance().registerModule(module);
            }
            OpenCreative.getPlugin().getLogger().info("Loaded " + ModuleManager.getInstance().getModules().size() + " modules for " + (System.currentTimeMillis()-currentTime) + " ms.");
        } catch (Exception error) {
            sendCriticalErrorMessage("An error has occurred while loading modules...",error);
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
        return new File(devPlanet.getWorldName() + File.separator);
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
     * Returns folders of all planets worlds.
     * @return planets worlds folders.
     */
    public static File[] getWorldsFolders() {
        List<File> worldsFolders = new ArrayList<>();
        File planetsFolder = getPlanetsStorageFolder();
        if (!planetsFolder.exists()) {
            planetsFolder.mkdirs();
        }
        File[] planetsWorlds = planetsFolder.listFiles();
        if (planetsWorlds == null) {
            return worldsFolders.toArray(new File[0]);
        }
        for (File file : planetsWorlds) {
            if (isPlanetFolder(file)) worldsFolders.add(file);
        }
        return worldsFolders.toArray(new File[0]);
    }

    /**
     * Returns folders of all modules yaml files.
     * @return modules files.
     */
    public static File[] getModulesFiles() {
        List<File> modules = new ArrayList<>();
        File modulesFolder = getModulesStorageFolder();
        if (!modulesFolder.exists()) {
            modulesFolder.mkdirs();
        }
        File[] modulesFiles = modulesFolder.listFiles();
        if (modulesFiles == null) {
            return modules.toArray(new File[0]);
        }
        for (File moduleFile : modulesFiles) {
            if (moduleFile.isDirectory()) continue;
            if (!moduleFile.getName().endsWith(".yml")) continue;
            modules.add(moduleFile);
        }
        return modules.toArray(new File[0]);
    }

    /**
     * Returns folders of planets worlds that are
     * stored in server container or /unloadedWorlds/ folder.
     */
    public static void convertOldPlanetFolders() {
        File serverDirectory = Bukkit.getServer().getWorldContainer();
        File[] serverDirectoryFiles = serverDirectory.listFiles();
        int count = 0;
        if (serverDirectoryFiles != null) {
            for (File file : serverDirectoryFiles) {
                if (convertOldPlanetFolder(file)) count++;
            }
        }
        File unloadedWorldsFolder = new File(serverDirectory + File.separator + "unloadedWorlds" + File.separator);
        if (unloadedWorldsFolder.exists()) {
            File[] unloadedWorlds = unloadedWorldsFolder.listFiles();
            if (unloadedWorlds != null) {
                for (File file : unloadedWorlds) {
                    if (convertOldPlanetFolder(file)) count++;
                }
                unloadedWorlds = unloadedWorldsFolder.listFiles();
            }
            if (unloadedWorlds.length == 0) {
                unloadedWorldsFolder.delete();
            }
        }
        if (count > 0) {
            OpenCreative.getPlugin().getLogger().info("Converted " + count + " old worlds!");
        }

    }

    /**
     * Returns a new renamed folder if planet is "plot",
     * otherwise it will return same folder.
     * @param folder planet folder to convert.
     * @return renamed or same planet folder.
     */
    public static boolean convertOldPlanetFolder(File folder) {
        try {
            boolean converted = false;
            if (folder.getName().startsWith("plot")) {
                OpenCreative.getPlugin().getLogger().info("Renaming " + folder.getName() + " to " + folder.getName().replace("plot","planet") + "...");
                File newFile = new File(folder.getParent() + File.separator + folder.getName().replace("plot","planet"));
                folder.renameTo(newFile);
                folder = newFile;
                converted = true;
            }
            if (folder.getPath().contains("planet") && !folder.getPath().contains("planets")) {
                OpenCreative.getPlugin().getLogger().info("Moving " + folder.getName() + " to planets folder...");
                File newFolder = new File(getPlanetsStorageFolder().getPath() + File.separator + folder.getName());
                copyFilesToDirectory(folder,newFolder);
                deleteFolder(folder);
                folder = newFolder;
                converted = true;
            }
            return converted;
        } catch (Exception error) {
            sendCriticalErrorMessage("Can't rename from plot to planet: " + folder.getName(),error);
            return false;
        }
    }

    /**
     * Checks if specified folder is directory of planet world.
     * @param folder folder to check.
     * @return true - if folder is planet world, false - not.
     */
    public static boolean isPlanetFolder(File folder) {
        return folder.isDirectory() && folder.getName().startsWith("planet") && folder.getPath().contains(getPlanetsStorageFolder().getPath());
    }

    /**
     * Unloads all loaded planets worlds.
     */
    public static void unloadPlanets() {
        OpenCreative.getPlugin().getLogger().info("Unloading worlds, please wait...");
        try {
            for (Planet planet : PlanetManager.getInstance().getPlanets()) {
                if (planet.isLoaded()) {
                    OpenCreative.getPlugin().getLogger().info("Unloading planet " + planet.getId() + "...");
                    planet.getTerritory().unload();
                } else if (planet.getDevPlanet().isLoaded()) {
                    OpenCreative.getPlugin().getLogger().info("Unloading planet dev " + planet.getId() + "...");
                    planet.getDevPlanet().unload();
                }
            }
            PlanetManager.getInstance().clearPlanets();
        } catch (Exception error) {
            sendCriticalErrorMessage("Error while unloading worlds.",error);
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
        if (directory.equals(Bukkit.getWorldContainer())) return;
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
     * Returns module's configuration.
     **/
    public static FileConfiguration getModuleConfig(Module module) {
        return YamlConfiguration.loadConfiguration(getModuleConfigFile(module.getId()));
    }

    /**
     * Returns module's config file.
     * @return file of module's config.
     */
    public static File getModuleConfigFile(int id) {
        return new File(getModulesStorageFolder(),"module"+id+".yml");
    }

    /**
     * Returns folder that stores all modules folders.
     * @return modules folder.
     */
    public static File getModulesStorageFolder() {
        return new File(Bukkit.getWorldContainer().getPath() + File.separator + "modules" + File.separator);
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
        return getPlanetsStorageFolder().getPath() + File.separator + "planet" + planet.getId() + File.separator;
    }

    /**
     * Returns folder that stores all planets folders.
     * @return planets folder.
     */
    public static File getPlanetsStorageFolder() {
        return new File(Bukkit.getWorldContainer().getPath() + File.separator + "planets" + File.separator);
    }

    public static String getPlanetIdFromName(String name) {
        return name
                .replace(Bukkit.getServer().getWorldContainer().getPath().replace("\\","/") + "/","")
                .replace("planets/planet","");
    }

}
