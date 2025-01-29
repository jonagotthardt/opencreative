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

package ua.mcchickenstudio.opencreative.settings;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import ua.mcchickenstudio.opencreative.settings.groups.Groups;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;

/**
 * This class represents Settings, that stores
 * values which are used in plugin.
 */
public class Settings {

    private boolean debug = false;
    private boolean maintenance = false;
    private boolean creativeChatEnabled = true;

    private boolean consoleCriticalErrors = true;
    private boolean consoleNotFoundMessage = true;
    private boolean consoleWarnings = true;

    private boolean lobbyClearInventory = true;

    private BukkitRunnable announcer;
    private PlayerListChanger listChanger = PlayerListChanger.FULL;

    private int worldCreationMinSeconds = 30;
    private int worldReputationMinSeconds = 300;

    private String customIdPattern = "^[a-zA-Zа-яА-Я0-9_]+$";
    private int customIdMinLength = 2;
    private int customIdMaxLength = 16;

    private int worldNameMinLength = 4;
    private int worldNameMaxLength = 30;

    private int worldDescriptionMinLength = 4;
    private int worldDescriptionMaxLength = 256;

    private final Groups groups;
    private final Commands commands;
    private final Set<Integer> recommendedWorldsIDs = new HashSet<>();
    private final Set<String> allowedResourcePackLinks = new HashSet<>();

    private final Map<Sounds,SettingsSound> sounds = new HashMap<>();

    public Settings() {
        groups = new Groups();
        commands = new Commands();
    }

    /**
     * Loads settings values from configuration file.
     * @param config Configuration file.
     */
    public void load(FileConfiguration config) {

        allowedResourcePackLinks.clear();
        recommendedWorldsIDs.clear();
        sounds.clear();

        listChanger = PlayerListChanger.fromString(config.getString("hide-from-tab","full"));
        recommendedWorldsIDs.addAll(config.getIntegerList("recommended-worlds"));
        allowedResourcePackLinks.addAll(config.getStringList("allowed-links.resource-pack"));

        debug = config.getBoolean("debug",false);
        maintenance = config.getBoolean("maintenance",false);
        consoleCriticalErrors = config.getBoolean("messages.critical-errors",true);
        consoleNotFoundMessage = config.getBoolean("messages.not-found",true);
        consoleWarnings = config.getBoolean("messages.warnings",true);
        lobbyClearInventory = config.getBoolean("lobby.clear-inventory",true);

        worldCreationMinSeconds = config.getInt("requirements.world-creation.played-seconds",30);
        worldReputationMinSeconds = config.getInt("requirements.world-reputation.creation-seconds",300);

        customIdPattern = config.getString("requirements.world-custom-id.pattern","^[a-zA-Zа-яА-Я0-9_]+$");
        customIdMinLength = config.getInt("requirements.world-custom-id.min-length",2);
        customIdMaxLength = config.getInt("requirements.world-custom-id.max-length",16);

        worldNameMinLength = config.getInt("requirements.world-name.min-length",2);
        worldNameMaxLength = config.getInt("requirements.world-name.max-length",16);

        worldDescriptionMinLength = config.getInt("requirements.world-description.min-length",2);
        worldDescriptionMaxLength = config.getInt("requirements.world-description.max-length",256);

        groups.load();
        commands.load();

        String soundsTheme = config.getString("sounds.theme","default");
        loadSounds(config, soundsTheme);

        if (maintenance) {
            OpenCreative.getPlugin().getLogger().warning("Maintenance mode is still enabled in config.yml, to disable: /maintenance end");
        }
        if (debug) {
            OpenCreative.getPlugin().getLogger().warning("Debug Mode is enabled in config.yml, some logs will appear in console.");
        }
        checkDebugAnnouncer();
    }

    private void loadSounds(FileConfiguration config, String soundsTheme) {
        sounds.clear();
        ConfigurationSection soundsSection = config.getConfigurationSection("sounds." + soundsTheme);
        if (soundsSection != null ) {
            for (String key : soundsSection.getKeys(false)) {
                try {
                    Sounds type = Sounds.valueOf(key.toUpperCase().replace("-","_"));
                    String sound = soundsSection.getString(key+".name","");
                    float pitch = (float) soundsSection.getDouble(key+".name",1.0f);
                    sounds.put(type,new SettingsSound(sound,pitch));
                } catch (Exception ignored) {
                    sendWarningErrorMessage("Sound " + key.toLowerCase() + " doesn't exists.");
                }
            }
        }
        if (!sounds.isEmpty()) {
            OpenCreative.getPlugin().getLogger().info("Added " + sounds.size() + " custom sounds");
        }
    }

    public boolean setSoundsTheme(String theme) {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        if (config.getConfigurationSection("sounds."+theme) == null) {
            return false;
        }
        loadSounds(config, theme);
        OpenCreative.getPlugin().getConfig().set("sounds.theme",theme);
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        if (this.debug == debug) return;
        this.debug = debug;
        OpenCreative.getPlugin().getConfig().set("debug",debug);
        OpenCreative.getPlugin().saveConfig();
        OpenCreative.getPlugin().getLogger().info("Debug mode is " + (debug ? "enabled." : "disabled."));
        checkDebugAnnouncer();
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public boolean isLobbyClearInventory() {
        return lobbyClearInventory;
    }

    public void setMaintenance(boolean maintenance) {
        if (this.maintenance == maintenance) return;
        this.maintenance = maintenance;
        OpenCreative.getPlugin().getConfig().set("maintenance",maintenance);
        OpenCreative.getPlugin().saveConfig();
        if (maintenance) {
            OpenCreative.getPlugin().getLogger().info("Maintenance mode started! Unloading planets, please wait...");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Sounds.MAINTENANCE_START.play(onlinePlayer);
                onlinePlayer.sendMessage(getLocaleMessage("creative.maintenance.started"));
                for (Planet planet : PlanetManager.getInstance().getPlanets()) {
                    if (planet.isLoaded()) {
                        for (Player player : planet.getPlayers()) {
                            teleportToLobby(player);
                        }
                    }
                }
            }
        } else {
            OpenCreative.getPlugin().getLogger().info("Maintenance mode ended, now players can play in worlds.");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Sounds.MAINTENANCE_END.play(onlinePlayer);
                onlinePlayer.sendMessage(getLocaleMessage("creative.maintenance.ended"));
            }
        }

    }

    /**
     * Checks if debug mode is enabled in config.yml.
     */
    private void checkDebugAnnouncer() {
        if (announcer != null) {
            announcer.cancel();
        }
        if (debug) {
            announcer = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().sendActionBar(Component.text("§fOpen§7Creative§b+ §3" + OpenCreative.getVersion() + "§7 Debug Mode. §fWell, it's possible."));
                }
            };
            announcer.runTaskTimer(OpenCreative.getPlugin(),20L,20L);
        } else {
            announcer = null;
        }
    }

    public Set<String> getAllowedResourcePackLinks() {
        return allowedResourcePackLinks;
    }

    public Set<Integer> getRecommendedWorldsIDs() {
        return recommendedWorldsIDs;
    }

    public void setCreativeChatEnabled(boolean creativeChatEnabled) {
        this.creativeChatEnabled = creativeChatEnabled;
    }

    public boolean isCreativeChatEnabled() {
        return creativeChatEnabled;
    }

    public PlayerListChanger getListChanger() {
        return listChanger;
    }

    public enum PlayerListChanger {

        SPECTATOR,
        FULL,
        NONE;

        public static PlayerListChanger fromString(String string) {
            for (PlayerListChanger changer : PlayerListChanger.values()) {
                if (string.equalsIgnoreCase(changer.name())) {
                    return changer;
                }
            }
            return FULL;
        }
    }

    public Groups getGroups() {
        return groups;
    }

    public Commands getCommands() {
        return commands;
    }

    public boolean isConsoleCriticalErrors() {
        return consoleCriticalErrors;
    }

    public boolean isConsoleWarnings() {
        return consoleWarnings;
    }

    public boolean isConsoleNotFoundMessage() {
        return consoleNotFoundMessage;
    }

    public String getCustomIdPattern() {
        return customIdPattern;
    }

    public int getCustomIdMaxLength() {
        return customIdMaxLength;
    }

    public int getCustomIdMinLength() {
        return customIdMinLength;
    }

    public int getWorldDescriptionMaxLength() {
        return worldDescriptionMaxLength;
    }

    public int getWorldDescriptionMinLength() {
        return worldDescriptionMinLength;
    }

    public int getWorldNameMaxLength() {
        return worldNameMaxLength;
    }

    public int getWorldNameMinLength() {
        return worldNameMinLength;
    }

    public int getWorldCreationMinSeconds() {
        return worldCreationMinSeconds;
    }

    public int getWorldReputationMinSeconds() {
        return worldReputationMinSeconds;
    }

    public Map<Sounds, SettingsSound> getSounds() {
        return sounds;
    }
}
