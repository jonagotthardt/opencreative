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

import ua.mcchickenstudio.opencreative.coding.blocks.events.CEListener;
import ua.mcchickenstudio.opencreative.commands.*;
import ua.mcchickenstudio.opencreative.commands.minecraft.*;
import ua.mcchickenstudio.opencreative.commands.world.CommandAd;
import ua.mcchickenstudio.opencreative.commands.world.CommandEnvironment;
import ua.mcchickenstudio.opencreative.commands.world.CommandJoin;
import ua.mcchickenstudio.opencreative.commands.world.modes.CommandBuild;
import ua.mcchickenstudio.opencreative.commands.world.modes.CommandDev;
import ua.mcchickenstudio.opencreative.commands.world.modes.CommandPlay;
import ua.mcchickenstudio.opencreative.commands.world.reputation.CommandDislike;
import ua.mcchickenstudio.opencreative.commands.world.reputation.CommandLike;
import ua.mcchickenstudio.opencreative.commands.world.CommandWorld;
import ua.mcchickenstudio.opencreative.listeners.entity.EntityDamage;
import ua.mcchickenstudio.opencreative.listeners.entity.EntitySpawn;
import ua.mcchickenstudio.opencreative.listeners.player.*;
import ua.mcchickenstudio.opencreative.listeners.world.BlockChanged;
import ua.mcchickenstudio.opencreative.listeners.world.BlockRedstone;
import ua.mcchickenstudio.opencreative.menu.Menus;
import ua.mcchickenstudio.opencreative.settings.Settings;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;
import ua.mcchickenstudio.opencreative.utils.hooks.Metrics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
 */
public final class OpenCreative extends JavaPlugin {

    private static OpenCreative plugin;
    private static Settings settings;

    public static final String version = "5.0 Pre-release";
    public static final String codename = "Things will be different";
    public static boolean maintenance = false;
    public static boolean debug = false;

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

        saveDefaultConfig();
        FileUtils.loadLocales();
        PlayerUtils.loadPermissions();
        HookUtils.loadHooks();
        FileUtils.loadPlots();
        settings = new Settings();
        settings.load(getConfig());
        checkDebug();

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
        for (Player player: Bukkit.getOnlinePlayers()) {
            player.sendMessage(Component.text("§f\n§f Shutting down §7Open§fCreative§b+ §7" + version + "§f, please wait...\n§f"));
            teleportToLobby(player);
        }
        FileUtils.unloadPlots();
        this.getLogger().info("OpenCreative+ is disabled.");
    }


    /**
     Get a plugin instance for operations with it. For example: for accessing config.yml.
     @return plugin instance.
     **/
    public static OpenCreative getPlugin() {
        return plugin;
    }

    /**
     * Checks if debug mode is enabled in config.yml.
     */
    private void checkDebug() {
        maintenance = getConfig().getBoolean("maintenance",false);
        if (maintenance) {
            getLogger().warning("Maintenance mode is still enabled in config.yml, to disable: /maintenance end");
        }
        debug = getConfig().getBoolean("debug",false);
        if (debug) {
            getLogger().warning("Debug Mode is enabled in config.yml, some logs will appear in console.");
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendActionBar(Component.text("§fOpen§7Creative§b+ §3" + version + "§7 Debug Mode. §fThings will be different."));
                    }
                }
            }.runTaskTimer(this,20L,20L);
        }
    }

    /**
     * Registers commands and their tab completer in server.
     */
    private void registerCommands() {
        this.getLogger().info("Registering OpenCreative+ commands...");
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
                } catch (Exception error) {
                    sendCriticalErrorMessage("Couldn't register command " + commandName,error);
                }
            } else {
                sendCriticalErrorMessage("Couldn't get command with name " + commandName + ", it is null. Maybe it doesn't exist in plugins.yml?");
            }
        }
        this.getLogger().info("OpenCreative+ registered all " + commands.size() + " commands.");
    }

    /**
     * Registers event listeners in server.
     */
    private void registerEvents() {
        this.getLogger().info("Registering OpenCreative+ event listeners...");
        getServer().getPluginManager().registerEvents(new ChangedWorld(), this);
        getServer().getPluginManager().registerEvents(new EntitySpawn(), this);
        getServer().getPluginManager().registerEvents(new EntityDamage(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawn(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeath(), this);
        getServer().getPluginManager().registerEvents(new PlayerTeleport(), this);
        getServer().getPluginManager().registerEvents(new PlayerMove(), this);
        getServer().getPluginManager().registerEvents(new PlayerChat(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItem(), this);
        getServer().getPluginManager().registerEvents(new PlayerPlaceBlock(), this);
        getServer().getPluginManager().registerEvents(new PlayerBreakBlock(), this);
        getServer().getPluginManager().registerEvents(new PlayerBucket(), this);
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new BlockRedstone(), this);
        getServer().getPluginManager().registerEvents(new BlockChanged(), this);
        getServer().getPluginManager().registerEvents(new Menus(), this);
        getServer().getPluginManager().registerEvents(new CEListener(), this);
        getServer().getPluginManager().registerEvents(new GameModeChange(), this);
        this.getLogger().info("OpenCreative+ registered all event listeners.");
    }

    public static Settings getSettings() {
        return settings;
    }
}
