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

package mcchickenstudio.creative.utils;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCriticalErrorMessage;
import static mcchickenstudio.creative.utils.ErrorUtils.sendWarningErrorMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.teleportToLobby;

public class FileUtils {

    @NotNull
    public static final Plugin plugin = Main.getPlugin();

    /**
     * Creates plot's settings.yml file.
     *
     * @param worldName Name of new world (plot31, plot103)
     * @param player    Owner of new world
     */
    public static void createWorldSettings(final String worldName, final Player player, WorldCreator creator) {
        final String worldFolderPath = Bukkit.getServer().getWorldContainer() + File.separator + worldName + File.separator;
        final File file = new File(worldFolderPath, "settings.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException error) {
                sendCriticalErrorMessage("Couldn't create a settings.yml for world " + worldName + " because of IOException. Maybe it is already exists? " + error.getMessage());
                return;
            }
        }
        final FileConfiguration worldFile = YamlConfiguration.loadConfiguration(file);
        worldFile.createSection("owner");
        worldFile.set("owner", player.getName());
        worldFile.createSection("owner-group");
        worldFile.set("owner-group",PlayerUtils.getGroup(player));
        worldFile.createSection("environment");
        worldFile.set("environment", creator.environment().name());
        worldFile.createSection("world");
        worldFile.set("world",worldName);
        worldFile.createSection("creation-time");
        worldFile.set("creation-time",System.currentTimeMillis());
        worldFile.createSection("last-activity-time");
        worldFile.set("last-activity-time",System.currentTimeMillis());
        worldFile.createSection("name");
        worldFile.set("name", MessageUtils.getLocaleMessage("creating-world.default-world-name").replace("%player%", player.getName()));
        worldFile.createSection("description");
        worldFile.set("description", MessageUtils.getLocaleMessage("creating-world.default-world-description").replace("%player%", player.getName()));
        worldFile.createSection("icon");
        worldFile.set("icon", String.valueOf(Material.DIAMOND));
        worldFile.createSection("sharing");
        worldFile.set("sharing", String.valueOf(Plot.Sharing.PUBLIC));
        worldFile.createSection("category");
        worldFile.set("category", String.valueOf(Plot.Category.SANDBOX));
        worldFile.createSection("customID");
        worldFile.set("customID",worldName.replace("plot",""));
        worldFile.createSection("players.unique");
        worldFile.set("players.unique", new ArrayList<String>());
        worldFile.createSection("players.liked");
        worldFile.set("players.liked", new ArrayList<String>());
        worldFile.createSection("players.builders.trusted");
        worldFile.set("players.builders.trusted", new ArrayList<String>());
        worldFile.createSection("players.builders.not-trusted");
        worldFile.set("players.builders.not-trusted", new ArrayList<String>());
        worldFile.createSection("players.developers.trusted");
        worldFile.set("players.developers.trusted", new ArrayList<String>());
        worldFile.createSection("players.developers.not-trusted");
        worldFile.set("players.developers.not-trusted", new ArrayList<String>());
        worldFile.createSection("players.whitelist");
        worldFile.set("players.whitelist", new ArrayList<String>());
        worldFile.createSection("players.blacklist");
        worldFile.set("players.blacklist", new ArrayList<String>());
        worldFile.createSection("flags");
        Map<String,Integer> flags = new HashMap<>();
        worldFile.set("flags",flags);
        try {
            worldFile.save(file);
        } catch (IOException | IllegalArgumentException error) {
            sendCriticalErrorMessage("Couldn't save world settings.yml for " + worldName + " because of " + error.getClass().getName() + " " + error.getMessage());
        }
    }

    /**
     * Creates plot's codeScript.yml file.
     **/
    public static void createCodeScript(final String path, final String worldName) {
        final File file = new File(path, "codeScript.yml");
        final FileConfiguration worldFile = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException error) {
                sendCriticalErrorMessage("Couldn't create a codeScript.yml for world " + worldName + " because of IOException. Maybe it is already exists? " + error.getMessage());
                return;
            }
        }
        worldFile.createSection("world");
        worldFile.set("world",worldName);
        worldFile.createSection("creation-time");
        worldFile.set("creation-time",System.currentTimeMillis());
        worldFile.createSection("last-activity-time");
        worldFile.set("last-activity-time",System.currentTimeMillis());
        worldFile.createSection("code");
        try {
            worldFile.save(file);
        } catch (IOException | IllegalArgumentException error) {
            sendCriticalErrorMessage("Couldn't save world codeScript.yml for " + worldName + " because of " + error.getClass().getName() + " " + error.getMessage());
        }
    }

    /**
     * Creates plot's codeScript.yml file.
     **/
    public static void createDevPlotConfig(final String path, final String worldName) {
        final File file = new File(path, "settings.yml");
        final FileConfiguration worldFile = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException error) {
                sendCriticalErrorMessage("Couldn't create a settings.yml for world " + worldName + " because of IOException. Maybe it is already exists? " + error.getMessage());
                return;
            }
        }
        worldFile.createSection("world");
        worldFile.set("world",worldName);
        worldFile.createSection("creation-time");
        worldFile.set("creation-time",System.currentTimeMillis());
        worldFile.createSection("container");
        worldFile.set("container",Material.CHEST.name());
        worldFile.createSection("container");
        try {
            worldFile.save(file);
        } catch (IOException | IllegalArgumentException error) {
            sendCriticalErrorMessage("Couldn't save world settings.yml for " + worldName + " because of " + error.getClass().getName() + " " + error.getMessage());
        }
    }

    /**
     Loads localization file from Creative/locales/ folder. If no localization file was found, then it creates a new one.
     **/
    public static void loadLocales() {
        Main.getPlugin().getLogger().info("Loading Creative localization file...");
        final File folder = new File(plugin.getDataFolder() + File.separator + "locales" + File.separator);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                sendCriticalErrorMessage("Couldn't create directory for locales... " + folder.getPath());
            }
        }
        final String selectedLang = plugin.getConfig().getString("messages.locale","en");
        final File file = new File(folder.getPath() + File.separator + selectedLang + ".yml");
        if (!file.exists()) {
            setDefaultLocales();
            plugin.getConfig().set("messages.locale","en");
        }
        MessageUtils.loadLocalizationFile();
        Main.getPlugin().getLogger().info("Loaded Creative localization file...");

    }

    private static void setDefaultLocales() {
        try {
            plugin.saveResource("locales" + File.separator + "olden.yml",false);
            plugin.saveResource("locales" + File.separator + "ru.yml",false);
        } catch (IllegalArgumentException error) {
            sendWarningErrorMessage("Couldn't save default localization file (resource) " + error.getClass().getName() + " " + error.getMessage());
        }
        plugin.saveConfig();
    }

    /**
     Resets localization file from Creative/locales/ folder. If localization file is detected in folder, then it will be removed and replaced with plugin's new one.
     **/
    public static void resetLocales() {
        Main.getPlugin().getLogger().info("Resseting Creative localization file...");
        final File folder = new File(plugin.getDataFolder() + File.separator + "locales" + File.separator);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                sendCriticalErrorMessage("Couldn't create directory for locales... " + folder.getPath());
            }
        }
        final String selectedLang = plugin.getConfig().getString("messages.locale","en");
        final File file = new File(folder.getPath() + File.separator + selectedLang + ".yml");
        if (file.exists()) {
            file.delete();
        }
        setDefaultLocales();
        MessageUtils.loadLocalizationFile();
        Main.getPlugin().getLogger().info("Reset Creative localization file!");
    }

    // Загрузка шаблонов миров

    /**
     Loads all plots to base. It contains plots from /unloadedWorlds/ and plots with loaded worlds.
     **/
    public static void loadPlots() {
        Main.getPlugin().getLogger().info("Creative+ is adding worlds to base...");
        try {
            File[] plotsFolders = getWorldsFolders(true);
            // Если папка миров существует
            if (plotsFolders.length > 0) {
                Main.getPlugin().getLogger().info("Found " + plotsFolders.length + " worlds, adding...");
                int corruptedWorlds = 0;
                int deprecatedWorlds = 0;
                long currentTime = System.currentTimeMillis();
                for (File plotFolder : plotsFolders) {
                    String worldName = plotFolder.getPath().replace(Bukkit.getServer().getWorldContainer() + File.separator,"").replace("unloadedWorlds" + File.separator,"");
                    // Отгруженные миры добавляются в базу
                    if (plotFolder.getPath().contains("unloadedWorlds")) {
                        Main.getPlugin().getLogger().info("Adding unloaded world " + worldName + " to base...");
                        // Если мир находился в директории сервера, то его
                        // переносят в папку отгруженных миров и добавляют в базу
                    } else {
                        Main.getPlugin().getLogger().info("Moving loaded world " + worldName + " to unloadedWorlds folder...");
                        World world = Bukkit.getWorld(worldName);
                        if (world != null) {
                            for (Player player : world.getPlayers()) {
                                teleportToLobby(player);
                            }
                        }
                        unloadWorldFolder(worldName,true);
                        Main.getPlugin().getLogger().info("Adding unloaded world " + worldName + " to base...");
                    }
                    if (!worldName.endsWith("dev")) {
                        Plot plot = new Plot(worldName);
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
                Main.getPlugin().getLogger().info("Loaded " + PlotManager.getInstance().getPlots().size() + " worlds for " + (System.currentTimeMillis()-currentTime) + " ms.");
                Main.getPlugin().getLogger().info(" Deprecated worlds: " + deprecatedWorlds);
                Main.getPlugin().getLogger().info(" Corrupted worlds: " + corruptedWorlds);
            } else {
                Main.getPlugin().getLogger().info("No worlds have been detected.");
            }
        } catch (Exception error) {
            sendCriticalErrorMessage("An error has occurred while loading worlds... " + error.getMessage());
        }
    }

    /**
     Returns plot's folder. It contains world's map, settings.yml and codeScript.yml.
     **/
    public static File getPlotFolder(Plot plot) {
        try {
            if (plot.isLoaded) {
                return new File(Bukkit.getServer().getWorldContainer() + File.separator + plot.worldName);
            } else {
                return new File(Bukkit.getServer().getWorldContainer() + File.separator + "unloadedWorlds" + File.separator + plot.worldName);
            }
        } catch (NullPointerException error) {
            ErrorUtils.sendPlotErrorMessage(plot,"Папка плота не обнаружена. " + error.getMessage());
            return null;
        }
    }

    /**
     Returns development plot's folder. It contains world's map.
     **/
    public static File getDevPlotFolder(DevPlot devPlot) {
        try {
            if (devPlot.isLoaded()) {
                return new File(Bukkit.getServer().getWorldContainer() + File.separator + devPlot.worldName + File.separator);
            } else {
                return new File(Bukkit.getServer().getWorldContainer() + File.separator + "unloadedWorlds" + File.separator + devPlot.worldName + File.separator);
            }
        } catch (NullPointerException error) {
            ErrorUtils.sendPlotErrorMessage(devPlot.getPlot(),"Папка плота разработчика не обнаружена. " + error.getMessage());
            return null;
        }
    }

    /**
     Returns plot's settings.yml configuration.
     **/
    public static FileConfiguration getPlotConfig(Plot plot) {
        try {
            File file = new File(getPlotFolder(plot), "settings.yml");
            return YamlConfiguration.loadConfiguration(file);
        } catch (NullPointerException error) {
            ErrorUtils.sendPlotErrorMessage(plot,"Not found settings.yml for plot :( " + error.getMessage());
            return null;
        }
    }

    /**
     Returns development plot's settings.yml configuration.
     **/
    public static FileConfiguration getDevPlotConfig(DevPlot plot) {
        try {
            File file = new File(getDevPlotFolder(plot), "settings.yml");
            return YamlConfiguration.loadConfiguration(file);
        } catch (NullPointerException error) {
            ErrorUtils.sendPlotErrorMessage(plot.getPlot(),"Not found settings.yml for development plot :(. " + error.getMessage());
            return null;
        }
    }

    /**
     Returns plot's settings.yml file.
     **/
    public static File getPlotConfigFile(Plot plot) {
        try {
            return new File((getPlotFolder(plot)),"settings.yml");
        } catch (NullPointerException error) {
            ErrorUtils.sendPlotErrorMessage(plot,"Файл settings.yml плота не обнаружен. " + error.getMessage());
            return null;
        }
    }

    /**
     Returns plot's codeScript.yml file.
     **/
    public static File getPlotScriptFile(Plot plot) {
        File scriptFile = new File((getPlotFolder(plot)),"codeScript.yml");
        if (scriptFile.exists()) return scriptFile;
        else {
            createCodeScript(getPlotFolder(plot).getPath(), plot.worldName);
            return getPlotScriptFile(plot);
        }
    }

    /**
     Returns plot's variables.yml configuration.
     **/
    public static File getPlotVariablesJson(Plot plot) {
        File variablesFile = new File((getPlotFolder(plot)),"variables.json");
        if (variablesFile.exists()) {
            return variablesFile;
        } else {
            try {
                variablesFile.createNewFile();
                return variablesFile;
            } catch (IOException error) {
                return null;
            }
        }
    }

    /**
     Returns player's data json from plot folder.
     **/
    public static File getPlayerDataJson(Plot plot, Player player) {
        File plotFolder = getPlotFolder(plot);
        if (plotFolder == null) {
            return null;
        }
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
     Sets value in player's data json file.
     **/
    public static boolean setPlayerData(Plot plot, Player player, String path, Object value) {
        File playerDataJson = getPlayerDataJson(plot,player);
        if (playerDataJson == null) {
            return false;
        }
        try (FileWriter file = new FileWriter(playerDataJson.getPath())) {
            JSONObject objItem = new JSONObject();
            if (value instanceof ItemStack) {
                final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                final BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(arrayOutputStream);
                objectOutputStream.writeObject(value);
                value = Base64Coder.encodeLines(arrayOutputStream.toByteArray());
            }
            objItem.put(path, value);
            file.write(objItem.toString());
            return true;
        } catch (Exception e){
            sendCriticalErrorMessage("Couldn't not save player data " + plot.worldName + " " + player.getName() + " " + path + " " + value,e);
            return false;
        }
   }

    /**
     Sets value in player's data json file.
     **/
    public static boolean addPlayerDataElement(Plot plot, Player player, String path, Object value) {
        File playerDataJson = getPlayerDataJson(plot,player);
        if (playerDataJson == null) {
            return false;
        }
        try {
            if (value instanceof ItemStack) {
                final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                final BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(arrayOutputStream);
                objectOutputStream.writeObject(value);
                value = Base64Coder.encodeLines(arrayOutputStream.toByteArray());
            }
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            if (playerDataJson.length()  > 0) {
                JSONParser jsonParser = new JSONParser();
                jsonObject = (JSONObject) jsonParser.parse(new FileReader(playerDataJson));
                Object object = jsonObject.get(path);
                jsonArray = (JSONArray) object;
            }
            if (!jsonArray.contains(value)) {
                jsonArray.add(value);
            }
            jsonObject.put(path,jsonArray);
            FileWriter file = new FileWriter(playerDataJson.getPath());
            file.write(jsonObject.toString());
            file.close();
            return true;
        } catch (Exception e){
            sendCriticalErrorMessage("Couldn't not save player data " + plot.worldName + " " + player.getName() + " " + path + " " + value,e);
            return false;
        }
    }

    /**
     * Checks array from player data json contains value or not.
     **/
    public static boolean playerDataContainsElement(Plot plot, Player player, String path, Object value) {
        File playerDataJson = getPlayerDataJson(plot,player);
        if (playerDataJson == null) {
            return false;
        }
        try {
            if (value instanceof ItemStack) {
                final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                final BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(arrayOutputStream);
                objectOutputStream.writeObject(value);
                value = Base64Coder.encodeLines(arrayOutputStream.toByteArray());
            }
            if (playerDataJson.length()  > 0) {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(playerDataJson));
                Object object = jsonObject.get(path);
                JSONArray jsonArray = (JSONArray) object;
                return jsonArray.contains(value);
            } else {
                return false;
            }
        } catch (Exception e){
            sendCriticalErrorMessage("Couldn't not get from player data " + plot.worldName + " " + player.getName() + " " + path + " " + value,e);
            return false;
        }
    }

    /**
     Returns plots folders. If "includeUnloadedWorlds" is true, then it will include unloaded plots.
     **/
    public static File[] getWorldsFolders(boolean includeUnloadedWorlds) {

        ArrayList<File> worldsFolders = new ArrayList<>();
        File serverDirectory = Bukkit.getServer().getWorldContainer();
        File[] serverDirectoryFiles = serverDirectory.listFiles();

        // Получаем загруженные миры
        if (serverDirectoryFiles != null) {
            for (File file : serverDirectoryFiles) {
                if (file.isDirectory() && file.getName().startsWith("plot")) worldsFolders.add(file);
            }
        } else {
            sendCriticalErrorMessage("При попытке получить файлы с директории сервера они оказались null.");
        }

        // Получаем отгруженные миры
        if (includeUnloadedWorlds) {
            File unloadedWorldsFolder = new File(serverDirectory + File.separator + "unloadedWorlds" + File.separator);
            File[] unloadedWorlds = unloadedWorldsFolder.listFiles();

            if (unloadedWorlds != null) {
                File unloadedWorldFolder = new File(serverDirectory + File.separator + "unloadedWorlds" + File.separator);
                if (!unloadedWorldFolder.exists()) if (!unloadedWorldFolder.mkdirs()) sendCriticalErrorMessage("Не получилось создать директорию " + unloadedWorldFolder.getPath());
                for (File file : unloadedWorlds) {
                    if (file.isDirectory() && file.getName().startsWith("plot")) {
                        worldsFolders.add(file);
                    }
                }
            } else {
                sendCriticalErrorMessage("При попытке получить папки отгруженных миров они оказались null.");
            }
        }
        return worldsFolders.toArray(new File[0]);
    }

    /**
     Unload all plots into /unloadedWorlds/ folder.
     **/
    public static void unloadPlots() {
        Main.getPlugin().getLogger().info("Creative+ is unloading worlds, please wait...");
        try {
            File[] worldsFiles = getWorldsFolders(false);
            if (worldsFiles.length > 0) {
                for (File file : worldsFiles) {
                    String worldName = file.getPath().replace(Bukkit.getServer().getWorldContainer() + File.separator,"");
                        try {
                            Main.getPlugin().getLogger().info("Unloading Creative world " + worldName + "...");
                            Bukkit.unloadWorld(worldName,true);
                            unloadWorldFolder(worldName,true);
                        } catch (Exception error) {
                            Main.getPlugin().getLogger().severe("An error has occurred when unloading world " + worldName + ": " + error.getMessage());
                        }
                }
            } else {
                Main.getPlugin().getLogger().info("No worlds been detected ;(");
            }
            PlotManager.getInstance().clearPlots();
        } catch (NullPointerException error) {
            sendCriticalErrorMessage("Error while unloading worls: " + error.getMessage());
        }
    }

    /**
     Loads plot folder from /unloadedWorlds/ to server storage.
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
     Unloads plot folder from server storage to /unloadedWorlds/ folder.
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
                sendCriticalErrorMessage("При попытке удалить папку мира произошла ошибка " + error.getMessage());
            }
        }
    }

    // Перенести папку готового мира в директорию сервера

    /**
     Copy input files into output directory.
     **/
    public static boolean copyFilesToDirectory(File input, File output) {
        try {
            File[] inputFiles = input.listFiles();
            if (!output.exists()) {
                if (!output.mkdirs()) sendCriticalErrorMessage("Не удалось создать директорию " + output.getPath());
            }
            if (inputFiles != null) {
                for (File worldFile : inputFiles) {
                    if (worldFile.isDirectory()) org.apache.commons.io.FileUtils.copyDirectoryToDirectory(worldFile,output);
                    else org.apache.commons.io.FileUtils.copyFileToDirectory(worldFile,output);
                }
            }
            return true;
        } catch (IOException error) {
            Main.getPlugin().getLogger().severe("При попытке скопировать файл произошла ошибка IOException. Файл откуда: " + input.getPath() + " Место куда: " + output.getPath() + " Ошибка: " + error.getMessage());
            return false;
        }
    }

    /**
     Delete world folder.
     **/
    public static void deleteWorld(final File path) {
        if (path.exists()) {
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(path);
            } catch (Exception error) {
                sendCriticalErrorMessage("Couldn't delete a folder with path " + path.getPath() + " because of " + error.getMessage());
            }
        }
    }

    /**
     Set plot's settings.yml parameter to long value.
     **/
    public static void setPlotConfigParameter(Plot plot, String parameterPath, long parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        if (plotConfig != null && plotConfigFile != null) {
            plotConfig.createSection(parameterPath);
            plotConfig.set(parameterPath,String.valueOf(parameterValue));
            try {
                plotConfig.save(plotConfigFile);
            } catch (IOException error) {
                sendCriticalErrorMessage("При сохранении конфига плота в файл произошла ошибка " + error.getMessage());
            }
        } else {
            ErrorUtils.sendPlotErrorMessage(plot,"Не удалось получить файл конфига плота либо сам конфиг");
        }
    }

    /**
     Set plot's settings.yml parameter to int value.
     **/
    public static void setPlotConfigParameter(Plot plot, String parameterPath, int parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        if (plotConfig != null && plotConfigFile != null) {
            plotConfig.createSection(parameterPath);
            plotConfig.set(parameterPath,String.valueOf(parameterValue));
            try {
                plotConfig.save(plotConfigFile);
            } catch (IOException error) {
                sendCriticalErrorMessage("При сохранении конфига плота в файл произошла ошибка " + error.getMessage());
            }
        } else {
            ErrorUtils.sendPlotErrorMessage(plot,"Не удалось получить файл конфига плота либо сам конфиг");
        }
    }

    /**
     Set plot's settings.yml parameter to object value.
     **/
    public static void setPlotConfigParameter(Plot plot, String parameterPath, Object parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        if (plotConfig != null && plotConfigFile != null) {
            plotConfig.createSection(parameterPath);
            plotConfig.set(parameterPath,String.valueOf(parameterValue));
            try {
                plotConfig.save(plotConfigFile);
            } catch (IOException error) {
                sendCriticalErrorMessage("При сохранении конфига плота в файл произошла ошибка " + error.getMessage());
            }
        } else {
            ErrorUtils.sendPlotErrorMessage(plot,"Не удалось получить файл конфига плота либо сам конфиг");
        }
    }

    /**
     Set plot's settings.yml parameter to String value.
     **/
    public static void setPlotConfigParameter(Plot plot, String parameterPath, String parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        if (plotConfig != null && plotConfigFile != null) {
            plotConfig.createSection(parameterPath);
            plotConfig.set(parameterPath,parameterValue);
            try {
                plotConfig.save(plotConfigFile);
            } catch (IOException error) {
                sendCriticalErrorMessage("При сохранении конфига плота в файл произошла ошибка " + error.getMessage());
            }
        } else {
            ErrorUtils.sendPlotErrorMessage(plot,"Не удалось получить файл конфига плота либо сам конфиг");
        }
    }

    /**
     Set plot's settings.yml parameter to List(String) value.
     **/
    public static void setPlotConfigParameter(Plot plot, String parameterPath, List<String> parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        if (plotConfig != null && plotConfigFile != null) {
            plotConfig.createSection(parameterPath);
            plotConfig.set(parameterPath,parameterValue);
            try {
                plotConfig.save(plotConfigFile);
            } catch (IOException error) {
                sendCriticalErrorMessage("При сохранении конфига плота в файл произошла ошибка " + error.getMessage());
            }
        } else {
            ErrorUtils.sendPlotErrorMessage(plot,"Не удалось получить файл конфига плота либо сам конфиг");
        }
    }

    public static void setPlotConfigParameter(Plot plot, String parameterPath, Set<String> parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        if (plotConfig != null && plotConfigFile != null) {
            plotConfig.createSection(parameterPath);
            plotConfig.set(parameterPath,new ArrayList<>(parameterValue));
            try {
                plotConfig.save(plotConfigFile);
            } catch (IOException error) {
                sendCriticalErrorMessage("При сохранении конфига плота в файл произошла ошибка " + error.getMessage());
            }
        } else {
            ErrorUtils.sendPlotErrorMessage(plot,"Не удалось получить файл конфига плота либо сам конфиг");
        }
    }

    /**
     Set plot's settings.yml parameter to Map(String, String) value.
     **/
    public static void setPlotConfigParameter(Plot plot, String parameterPath, Map<String, String> parameterValue) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        File plotConfigFile = getPlotConfigFile(plot);
        if (plotConfig != null && plotConfigFile != null) {
            plotConfig.createSection(parameterPath);
            plotConfig.set(parameterPath,parameterValue);
            try {
                plotConfig.save(plotConfigFile);
            } catch (IOException error) {
                sendCriticalErrorMessage("При сохранении конфига плота в файл произошла ошибка " + error.getMessage());
            }
        } else {
            ErrorUtils.sendPlotErrorMessage(plot,"Не удалось получить файл конфига плота либо сам конфиг");
        }
    }

    /**
     Returns list with players nicknames, that follows PlayersType.
     **/
    public static List<String> getPlayersFromPlotConfig(Plot plot, Plot.PlayersType type) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        if (plotConfig != null) {
            return new ArrayList<>(plotConfig.getStringList(type.getPath()));
        } else {
            sendCriticalErrorMessage("При попытке получить список игроков из файла конфига плота " + plot.worldName + " произошла ошибка. Тип: " + type.toString() + " Конфиг плота оказался null.");
            return new ArrayList<>();
        }
    }

    /**
     Adds player to player list into plot settings.yml with specified PlayersType.
     **/
    public static boolean addPlayerToListInPlotConfig(Plot plot, String nickname, Plot.PlayersType type) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        if (plotConfig != null) {
            List<String> newPlayersPlotConfigList = plotConfig.getStringList(type.getPath());
            newPlayersPlotConfigList.add(nickname);
            setPlotConfigParameter(plot,type.getPath(),newPlayersPlotConfigList);
            return true;
        } else {
            sendCriticalErrorMessage("При попытке добавить игрока в список файла конфига плота " + plot.worldName + " произошла ошибка. Никнейм: " + nickname + " Тип: " + type.toString() + " Конфиг плота оказался null.");
            return false;
        }
    }

    /**
     Remove player from player list in plot's settings.yml with specified PlayersType.
     **/
    public static void removePlayerFromListInPlotConfig(Plot plot, String nickname, Plot.PlayersType type) {
        FileConfiguration plotConfig = getPlotConfig(plot);
        if (plotConfig != null) {
            List<String> newPlayersPlotConfigList = plotConfig.getStringList(type.getPath());
            newPlayersPlotConfigList.remove(nickname);
            setPlotConfigParameter(plot,type.getPath(),newPlayersPlotConfigList);
        } else {
            sendCriticalErrorMessage("При попытке убрать игрока из списка файла конфига плота " + plot.worldName + " произошла ошибка. Никнейм: " + nickname + " Тип: " + type.toString() + " Конфиг плота оказался null.");
        }
    }

}
