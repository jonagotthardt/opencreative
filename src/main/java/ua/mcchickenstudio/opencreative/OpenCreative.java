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
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldListener;
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
import ua.mcchickenstudio.opencreative.listeners.CreativeListener;
import ua.mcchickenstudio.opencreative.listeners.entity.EntityDamageListener;
import ua.mcchickenstudio.opencreative.listeners.entity.EntitySpawnListener;
import ua.mcchickenstudio.opencreative.listeners.entity.EntityStateListener;
import ua.mcchickenstudio.opencreative.listeners.player.*;
import ua.mcchickenstudio.opencreative.listeners.world.BlockChangeListener;
import ua.mcchickenstudio.opencreative.listeners.world.RedstoneListener;
import ua.mcchickenstudio.opencreative.managers.blocks.BlocksManager;
import ua.mcchickenstudio.opencreative.managers.economy.Economy;
import ua.mcchickenstudio.opencreative.managers.packets.PacketManager;
import ua.mcchickenstudio.opencreative.managers.stability.DisabledWatchdog;
import ua.mcchickenstudio.opencreative.managers.stability.StabilityManager;
import ua.mcchickenstudio.opencreative.managers.updater.HangarUpdater;
import ua.mcchickenstudio.opencreative.managers.updater.Updater;
import ua.mcchickenstudio.opencreative.menus.Menus;
import ua.mcchickenstudio.opencreative.managers.space.Space;
import ua.mcchickenstudio.opencreative.managers.space.PlanetsManager;
import ua.mcchickenstudio.opencreative.settings.Settings;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;
import ua.mcchickenstudio.opencreative.utils.hooks.Metrics;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
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

    private Settings settings;
    private Economy economy;
    private Updater updater;
    private PacketManager packet;
    private PlanetsManager space;
    private StabilityManager watchdog;
    private BlocksManager blocks;

    private static final String version = "5.6.0";
    private static final String codename = "Well, it's possible";

    /**
     * Plugin load operations.
     * @see #onEnable
     */
    @Override
    public void onLoad() {
        getLogger().info(String.join("\n",
                        "",
                        "This software was made by Ukrainians, suffering from never-ending air alerts, explosions, and deaths.",
                        "We're AGAINST THE WAR. This software IS NOT DESIGNED for those who support killing and robbing another country.",
                        "",
                        "Let us have fun, like players who create their worlds...",
                        "McChicken Studio 2017–2025",
                        ""
        ));
    }

    /**
     Plugin startup operations. It registers commands, events; loads config, worlds, localization file.
     @see #onDisable
     **/
    @Override
    public void onEnable() {
        plugin = this;
        long startTime = System.currentTimeMillis();
        getLogger().info("Starting OpenCreative+ " + version + ": " + codename + ", please wait...");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,100,1));
            player.showTitle(Title.title(
                    Component.text("§fOpen§7Creative§b+ §7" + version), Component.text("§f" + codename + "..."),
                    Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(5), Duration.ofSeconds(0))
            ));
        }
        settings = new Settings();
        settings.load(getConfig());
        registerCommands();
        registerEvents();
        //Ticker.runTicker();
        saveDefaultConfig();

        space = new Space();
        space.init();

        FileUtils.loadLocales();
        PlayerUtils.loadPermissions();
        HookUtils.loadHooks();
        FileUtils.loadPlanets();
        //FileUtils.loadModules();

        economy = HookUtils.getEconomy();
        economy.init();
        updater = new HangarUpdater();
        updater.init();
        packet = HookUtils.getPacketManager();
        packet.init();
        watchdog = new DisabledWatchdog();
        watchdog.init();
        blocks = HookUtils.getBlocks();
        blocks.init();

        long loadedTime = System.currentTimeMillis()-startTime;
        for (Player player : Bukkit.getOnlinePlayers()) {
            teleportToLobby(player);
            getServer().sendActionBar(Component.text("§7Open§fCreative§b+ §7" + version + "§f is loaded for " + loadedTime + " ms."));
        }
        getLogger().info(String.join("\n",
                        "",
                        "OpenCreative+ " + version + ": " + codename + " is loaded for " + loadedTime + " ms.",
                        " Welcome to OpenCreative+ " + version + "!",
                        "  Running on " + Bukkit.getMinecraftVersion() + " server",
                        "  Current time " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()),
                        isChristmas() ? "  Ho-ho-ho! Merry Christmas, server owners! :-) ❆" :
                                        isHalloween() ? "  Spo-o-o-oky Halloween, server owners! O_o 🎃" : "",
                        "  " + codename,
                        "  Made by McChicken Studio 2017–2025",
                        ""
        ));
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
        FileUtils.unloadPlanets();
        getLogger().info(String.join("\n",
                        "",
                        "Goodbye from OpenCreative+",
                        "",
                        " " + codename,
                        "  Made by McChicken Studio 2017–2025",
                        ""
        ));
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
        commands.put("value",       CommandValue.class);
        //commands.put("module",      CommandModule.class);
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
        getLogger().info("OpenCreative+ registered " + (registeredCommands == commands.size() ? "all" : registeredCommands + "/" + commands.size()) +  " commands.");
    }

    /**
     * Registers event listeners in server.
     */
    private void registerEvents() {
        getLogger().info("Registering OpenCreative+ event listeners...");
        int registeredListeners = 0;
        Class<?>[] listeners = new Class[] {
                ChangedWorld.class,       EntitySpawnListener.class,  EntityDamageListener.class,
                JoinListener.class,       QuitListener.class,         RespawnListener.class,
                DeathListener.class,      TeleportListener.class,     MoveListener.class,
                ChatListener.class,       InteractListener.class,     DropItemListener.class,
                PlaceBlockListener.class, DestroyBlockListener.class, BucketListener.class,
                ClickListener.class,      RedstoneListener.class,     BlockChangeListener.class,
                Menus.class,              GameModeListener.class,     EntityStateListener.class,
                CreativeListener.class,   PotionListener.class
        };
        for (Class<?> listenerClass : listeners) {
            try {
                getServer().getPluginManager().registerEvents(
                        (Listener) listenerClass.getDeclaredConstructor().newInstance(), this
                );
                registeredListeners++;
            } catch (Exception exception) {
                sendCriticalErrorMessage("Couldn't register event listener: " + listenerClass.getSimpleName(), exception);
            }
        }
        try {
            new WorldListener().registerExecutors();
        } catch (Exception exception) {
            sendCriticalErrorMessage("Couldn't register executors", exception);
        }
        getLogger().info("OpenCreative+ registered " + (registeredListeners == listeners.length ? "all" : registeredListeners + "/" + listeners.length) + " event listeners.");
    }

    /**
     * Returns OpenCreative+ settings.
     * @return settings of plugin.
     */
    public static Settings getSettings() {
        return getPlugin().settings;
    }

    /**
     * Sets custom economy manager.
     * @param economy economy manager.
     */
    @SuppressWarnings("unused")
    public static void setEconomy(Economy economy) {
        getPlugin().getLogger().info("Now using economy manager: " + economy.getName());
        getPlugin().economy = economy;
    }

    /**
     * Gets economy manager, that has money operations for players.
     * @return economy manager.
     */
    public static Economy getEconomy() {
        return getPlugin().economy;
    }

    /**
     * Sets custom packet manager.
     * @param packetManager packet manager.
     */
    @SuppressWarnings("unused")
    public static void setPacketManager(PacketManager packetManager) {
        getPlugin().getLogger().info("Now using packet manager: " + packetManager.getName());
        getPlugin().packet = packetManager;
    }

    /**
     * Gets packet manager, that has packets modifiers methods.
     * @return packet manager.
     */
    public static PacketManager getPacketManager() {
        return getPlugin().packet;
    }

    /**
     * Sets custom blocks manager.
     * @param blocksManager blocks manager.
     */
    @SuppressWarnings("unused")
    public static void setBlocksManager(BlocksManager blocksManager) {
        getPlugin().getLogger().info("Now using blocks manager: " + blocksManager.getName());
        getPlugin().blocks = blocksManager;
    }

    /**
     * Gets blocks manager, that changes a lot
     * of blocks in world.
     * @return blocks manager.
     */
    public static BlocksManager getBlocksManager() {
        return getPlugin().blocks;
    }

    /**
     * Sets custom planets manager.
     * @param planetsManager planets manager.
     */
    @SuppressWarnings("unused")
    public static void setPlanetsManager(PlanetsManager planetsManager) {
        getPlugin().getLogger().info("Now using planets manager: " + planetsManager.getName());
        getPlugin().space = planetsManager;
    }

    /**
     * Gets planets manager, that stores planets in base
     * and has methods to create, find and delete them.
     * @return planets manager.
     */
    public static PlanetsManager getPlanetsManager() {
        return getPlugin().space;
    }

    /**
     * Sets custom stability manager.
     * @param stabilityManager planets manager.
     */
    @SuppressWarnings("unused")
    public static void setStability(StabilityManager stabilityManager) {
        getPlugin().getLogger().info("Now using stability manager: " + stabilityManager.getName());
        getPlugin().watchdog = stabilityManager;
    }

    /**
     * Gets stability manager, that checks server's
     * performance and makes sure everything is fine.
     * @return planets manager.
     */
    public static StabilityManager getStability() {
        return getPlugin().watchdog;
    }

    /**
     * Gets version of OpenCreative+.
     * @return version of plugin.
     */
    public static String getVersion() {
        return version;
    }

    /**
     * Gets update manager, that has methods to
     * check available updates for plugin.
     */
    public static Updater getUpdater() {
        return getPlugin().updater;
    }

    /**
     * Gets codename of current OpenCreative+ version.
     * @return codename of version.
     */
    public static String getCodename() {
        return codename;
    }

    /**
     * Checks if it's Christmas on plugin launch.
     * @return true - it's Christmas, false - not.
     */
    private static boolean isChristmas() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            return calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) == 25;
        } catch (Exception error) {
            return false;
        }
    }

    /**
     * Checks if it's Halloween on plugin launch.
     * @return true - it's Halloween, false - not.
     */
    private static boolean isHalloween() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            return calendar.get(Calendar.MONTH) == Calendar.OCTOBER && calendar.get(Calendar.DAY_OF_MONTH) == 31;
        } catch (Exception error) {
            return false;
        }
    }


}
