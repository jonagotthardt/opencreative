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

package ua.mcchickenstudio.opencreative.settings;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.plots.Plot;
import ua.mcchickenstudio.opencreative.plots.PlotManager;
import ua.mcchickenstudio.opencreative.settings.groups.Groups;

import java.util.HashSet;
import java.util.Set;

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

    private BukkitRunnable announcer;
    private PlayerListChanger listChanger = PlayerListChanger.FULL;

    private final Groups groups;

    private final Set<Integer> recommendedWorldsIDs = new HashSet<>();
    private final Set<String>  allowedResourcePackLinks = new HashSet<>();

    public Settings() {
        groups = new Groups();
    }

    /**
     * Loads settings values from configuration file.
     * @param config Configuration file.
     */
    public void load(FileConfiguration config) {
        allowedResourcePackLinks.clear();
        recommendedWorldsIDs.clear();
        listChanger = PlayerListChanger.fromString(config.getString("hide-from-tab","full"));
        recommendedWorldsIDs.addAll(config.getIntegerList("recommended-worlds"));
        allowedResourcePackLinks.addAll(config.getStringList("allowed-links.resource-pack"));
        debug = config.getBoolean("debug",false);
        maintenance = config.getBoolean("maintenance",false);
        groups.load();
        if (maintenance) {
            OpenCreative.getPlugin().getLogger().warning("Maintenance mode is still enabled in config.yml, to disable: /maintenance end");
        }
        if (debug) {
            OpenCreative.getPlugin().getLogger().warning("Debug Mode is enabled in config.yml, some logs will appear in console.");
        }
        checkDebugAnnouncer();
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

    public void setMaintenance(boolean maintenance) {
        if (this.maintenance == maintenance) return;
        this.maintenance = maintenance;
        OpenCreative.getPlugin().getConfig().set("maintenance",maintenance);
        OpenCreative.getPlugin().saveConfig();
        if (maintenance) {
            OpenCreative.getPlugin().getLogger().info("Maintenance mode started! Unloading plots, please wait...");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT,100,0.5f);
                onlinePlayer.sendMessage(getLocaleMessage("creative.maintenance.started"));
                for (Plot plot : PlotManager.getInstance().getPlots()) {
                    if (plot.isLoaded()) {
                        for (Player player : plot.getPlayers()) {
                            teleportToLobby(player);
                        }
                    }
                }
            }
        } else {
            OpenCreative.getPlugin().getLogger().info("Maintenance mode ended, now players can play in worlds.");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(),Sound.BLOCK_BEACON_POWER_SELECT,100,0.7f);
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
                    Bukkit.getServer().sendActionBar(Component.text("§fOpen§7Creative§b+ §3" + OpenCreative.getVersion() + "§7 Debug Mode. §fThings will be different."));
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
}
