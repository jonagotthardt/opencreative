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

package ua.mcchickenstudio.opencreative;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ua.mcchickenstudio.opencreative.coding.blocks.events.CEListener;
import ua.mcchickenstudio.opencreative.commands.*;
import ua.mcchickenstudio.opencreative.commands.minecraft.*;
import ua.mcchickenstudio.opencreative.commands.world.CommandAd;
import ua.mcchickenstudio.opencreative.commands.world.CommandEnvironment;
import ua.mcchickenstudio.opencreative.commands.world.CommandJoin;
import ua.mcchickenstudio.opencreative.commands.world.CommandWorld;
import ua.mcchickenstudio.opencreative.commands.world.modes.CommandBuild;
import ua.mcchickenstudio.opencreative.commands.world.modes.CommandDev;
import ua.mcchickenstudio.opencreative.commands.world.modes.CommandPlay;
import ua.mcchickenstudio.opencreative.commands.world.reputation.CommandDislike;
import ua.mcchickenstudio.opencreative.commands.world.reputation.CommandLike;
import ua.mcchickenstudio.opencreative.listeners.entity.EntityDamage;
import ua.mcchickenstudio.opencreative.listeners.entity.EntitySpawn;
import ua.mcchickenstudio.opencreative.listeners.player.*;
import ua.mcchickenstudio.opencreative.listeners.world.BlockChanged;
import ua.mcchickenstudio.opencreative.listeners.world.BlockRedstone;
import ua.mcchickenstudio.opencreative.managers.economy.Economy;
import ua.mcchickenstudio.opencreative.menu.Menus;
import ua.mcchickenstudio.opencreative.settings.Settings;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;
import ua.mcchickenstudio.opencreative.utils.core.Ticker;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;
import ua.mcchickenstudio.opencreative.utils.hooks.Metrics;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;

/**
 * This class represents OpenCreative+ java plugin for PaperMC.
 * Only for loading, enabling and disabling plugin. Contains
 * general information about plugin's version and codename.
 * @author McChicken Studio
 */
public final class OpenCreative extends JavaPlugin {

    private static OpenCreative plugin;
    private static Settings settings;
    private static Economy economy;

    private static final String version = "5.0 Pre-release";
    private static final String codename = "Things will be different";

    /**
     * Plugin load operations.
     * @see #onEnable
     */
    @Override
    public void onLoad() {
        getLogger().info(" ");
        getLogger().info("This software was made by ukrainians, that are suffering from never-ending air alerts, explosions and people's deaths.");
        getLogger().info("We're AGAINST THE WAR. This software IS NOT DESIGNED for people, who support killing and robbing another country.");
        getLogger().info(" ");
        getLogger().info("Let us having fun, like players that create their worlds...");
        getLogger().info("McChicken Studio 2017-2024");
        getLogger().info(" ");
    }

    /**
     Plugin startup operations. It registers commands, events; loads config, worlds, localization file.
     @see #onDisable
     **/
    @Override
    public void onEnable() {
        plugin = this;
        long startTime = System.currentTimeMillis();
        this.getLogger().info("Starting OpenCreative+ " + version + ": " + codename + ", please wait...");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,100,1));
            player.showTitle(Title.title(
                    Component.text("§fOpen§7Creative§b+ §7" + version), Component.text("§f" + codename + "..."),
                    Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(5), Duration.ofSeconds(0))
            ));
        }
        registerCommands();
        registerEvents();
        Ticker.runTicker();
        saveDefaultConfig();
        settings = new Settings();
        settings.load(getConfig());

        FileUtils.loadLocales();
        PlayerUtils.loadPermissions();
        HookUtils.loadHooks();
        FileUtils.loadPlots();

        economy = HookUtils.getEconomy();
        economy.init();

        long loadedTime = System.currentTimeMillis()-startTime;
        for (Player player : Bukkit.getOnlinePlayers()) {
            teleportToLobby(player);
            getServer().sendActionBar(Component.text("§7Open§fCreative§b+ §7" + version + "§f is loaded for " + loadedTime + " ms."));
        }

        getLogger().info("OpenCreative+ " + version + ": " + codename + " is loaded for " + loadedTime + " ms.");
        getLogger().info(" ");
        getLogger().info(" Welcome to OpenCreative+ " + version + "!");
        getLogger().info(" ");
        getLogger().info("  Running on " + Bukkit.getMinecraftVersion() + " server");
        getLogger().info("  Current time " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        getLogger().info(" ");
        getLogger().info("  " + codename);
        getLogger().info("  Made by McChicken Studio 2017-2024");
        getLogger().info(" ");
        new Metrics(this, 22001);

    }

    /**
     Plugin shutdown operations. It unloads worlds when plugin is being disabled.
     @see #onEnable
     **/
    @Override
    public void onDisable() {
        getLogger().info("Shutting down OpenCreative+, please wait...");
        for (Player player: Bukkit.getOnlinePlayers()) {
            player.sendMessage(Component.text("§f\n§f Shutting down §7Open§fCreative§b+ §7" + version + "§f, please wait...\n§f"));
            teleportToLobby(player);
        }
        FileUtils.unloadPlots();
        getLogger().info(" ");
        getLogger().info(" Goodbye from OpenCreative+");
        getLogger().info(" ");
        getLogger().info(" " + codename);
        getLogger().info("  Made by McChicken Studio 2017-2024");
        getLogger().info(" ");
    }


    /**
     Get a plugin instance for operations with it. For example: for accessing config.yml.
     @return plugin instance.
     **/
    public static OpenCreative getPlugin() {
        return plugin;
    }

    /**
     * Registers commands and their tab completer in server.
     */
    private void registerCommands() {
        this.getLogger().info("Registering OpenCreative+ commands...");
        int registeredCommands = 0;
        Map<String,Class<? extends CommandExecutor>> commands = new HashMap<>();
        commands.put("creative",    CommandCreative.class);
        commands.put("spawn",       CommandSpawn.class);
        commands.put("menu",        CommandMenu.class);
        commands.put("world",       CommandWorld.class);
        commands.put("chat",        CreativeChat.class);
        commands.put("join",        CommandJoin.class);
        commands.put("ad",          CommandAd.class);
        commands.put("play",        CommandPlay.class);
        commands.put("build",       CommandBuild.class);
        commands.put("dev",         CommandDev.class);
        commands.put("environment", CommandEnvironment.class);
        commands.put("like",        CommandLike.class);
        commands.put("dislike",     CommandDislike.class);
        commands.put("locate",      CommandLocate.class);
        commands.put("gamemode",    CommandGamemode.class);
        commands.put("give",        CommandGive.class);
        commands.put("teleport",    CommandTeleport.class);
        commands.put("edit",        CommandEdit.class);
        commands.put("playsound",   CommandPlaySound.class);
        commands.put("stopsound",   CommandStopSound.class);
        commands.put("time",        CommandTime.class);
        commands.put("weather",     CommandWeather.class);
        for (String commandName : commands.keySet()) {
            PluginCommand command = getCommand(commandName);
            if (command != null) {
                try {
                    command.setExecutor(commands.get(commandName).getDeclaredConstructor().newInstance());
                    registeredCommands++;
                } catch (Exception error) {
                    sendCriticalErrorMessage("Couldn't register command " + commandName,error);
                }
            } else {
                sendCriticalErrorMessage("Couldn't get command with name " + commandName + ", it is null. Maybe it doesn't exist in plugins.yml?");
            }
        }
        this.getLogger().info("OpenCreative+ registered " + (registeredCommands == commands.size() ? "all" : registeredCommands + "/" + commands.size()) +  " commands.");
    }

    /**
     * Registers event listeners in server.
     */
    private void registerEvents() {
        this.getLogger().info("Registering OpenCreative+ event listeners...");
        int registeredListeners = 0;
        Class<?>[] listeners = new Class[] {
                ChangedWorld.class,     EntitySpawn.class,      EntityDamage.class,
                PlayerJoin.class,       PlayerQuit.class,       PlayerRespawn.class,
                PlayerDeath.class,      PlayerTeleport.class,   PlayerMove.class,
                PlayerChat.class,       PlayerInteract.class,   PlayerDropItem.class,
                PlayerPlaceBlock.class, PlayerBreakBlock.class, PlayerBucket.class,
                InventoryClick.class,   BlockRedstone.class,    BlockChanged.class,
                Menus.class,            CEListener.class,       GameModeChange.class,
        };
        for (Class<?> listenerClass : listeners) {
            try {
                getServer().getPluginManager().registerEvents(
                        (Listener) listenerClass.getDeclaredConstructor().newInstance(), this
                );
                registeredListeners++;
            } catch (Exception exception) {
                sendCriticalErrorMessage("Couldn't register event listener: " + listenerClass.getSimpleName(),exception);
            }
        }
        this.getLogger().info("OpenCreative+ registered " + (registeredListeners == listeners.length ? "all" : registeredListeners + "/" + listeners.length) + " event listeners.");
    }

    /**
     * Returns OpenCreative+ settings.
     * @return settings of plugin.
     */
    public static Settings getSettings() {
        return settings;
    }

    /**
     * Sets custom economy manager.
     * @param economy economy manager.
     */
    @SuppressWarnings("unused")
    public static void setEconomy(Economy economy) {
        getPlugin().getLogger().info("Now using economy manager: " + economy.getName());
        OpenCreative.economy = economy;
    }

    /**
     * Gets economy manager, that has money operations for players.
     * @return economy manager.
     */
    public static Economy getEconomy() {
        return economy;
    }

    /**
     * Gets version of OpenCreative+.
     * @return version of plugin.
     */
    public static String getVersion() {
        return version;
    }

    /**
     * Gets codename of current OpenCreative+ version.
     * @return codename of version.
     */
    public static String getCodename() {
        return codename;
    }


}
