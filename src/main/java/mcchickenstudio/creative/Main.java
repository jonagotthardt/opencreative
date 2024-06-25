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

package mcchickenstudio.creative;

import mcchickenstudio.creative.coding.blocks.events.CEListener;
import mcchickenstudio.creative.commands.*;
import mcchickenstudio.creative.events.*;
import mcchickenstudio.creative.menu.Menus;
import mcchickenstudio.creative.utils.FileUtils;
import mcchickenstudio.creative.utils.hooks.HookUtils;
import mcchickenstudio.creative.utils.PlayerUtils;
import mcchickenstudio.creative.utils.hooks.Metrics;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCriticalErrorMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.teleportToLobby;

public final class Main extends JavaPlugin {

    public static Main plugin;
    public static final String version = "1.5 Preview 3";
    public static final String codename = "Things will be different";
    public static boolean debug = false;

    /**
     * Plugin load operations.
     * @see #onEnable
     */
    @Override
    public void onLoad() {
        getLogger().info("This software was made by ukrainians, that are suffering from never-ending air alerts, explosions and people's deaths. We're AGAINST THE WAR. This software IS NOT DESIGNED for people, who support killing and robbing another country. Let us having fun, like players that create their worlds...");
    }

    /**
     Plugin startup operations. It registers commands, events; loads config, worlds, localization file.
     @see #onDisable
     **/
    @Override
    public void onEnable() {
        plugin = this;
        long startTime = System.currentTimeMillis();
        this.getLogger().info("Loading OpenCreative+ " + version + ": " + codename + ", please wait...");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,100,1));
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',"&fOpen&7Creative&b+ &7" + version),
                    ChatColor.translateAlternateColorCodes('&',"&f" + codename + "..."),0,100,0);
        }
        registerCommands();
        registerEvents();

        saveDefaultConfig();
        FileUtils.loadLocales();
        PlayerUtils.loadPermissions();
        HookUtils.loadHooks();
        FileUtils.loadPlots();
        checkDebug();

        long loadedTime = System.currentTimeMillis()-startTime;
        for (Player player : Bukkit.getOnlinePlayers()) {
            teleportToLobby(player);
            getServer().sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Open&fCreative&b+ &7" + version + "&f is loaded for " + loadedTime + " ms.")));
        }

        Main.getPlugin().getLogger().info("OpenCreative+ " + version + ": " + codename + " is loaded for " + loadedTime);
        Main.getPlugin().getLogger().info(" ");
        Main.getPlugin().getLogger().info(" Welcome to OpenCreative+ " + version + "!");
        Main.getPlugin().getLogger().info(" ");
        new Metrics(this, 22001);

    }

    /**
     Plugin shutdown operations. It unloads worlds when plugin is being disabled.
     @see #onEnable
     **/
    @Override
    public void onDisable() {
        for (Player player: Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f\n&f Shutting down &7Open&fCreative&b+ &7" + version + "&f, please wait...\n&f"));
            teleportToLobby(player);
        }
        FileUtils.unloadPlots();
        this.getLogger().info("OpenCreative+ is disabled.");
    }


    /**
     Get a plugin instance for operations with it. For example: for accessing config.yml.
     @return plugin instance.
     **/
    public static Main getPlugin() {
        return plugin;
    }

    /**
     * Checks if debug mode is enabled in config.yml.
     * @return true - if debug mode is enabled, false - disabled.
     */
    private boolean checkDebug() {
        if (getConfig().getBoolean("debug",false)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendActionBar(Component.text("§fOpen§7Creative§b+ §3" + version + "§7 Debug Mode. §fShh.. let's not leak our hard work..."));
                    }
                }
            }.runTaskTimer(this,20L,20L);
        }
        debug = getConfig().getBoolean("debug",false);
        return debug;
    }

    private void registerCommands() {
        this.getLogger().info("Registering OpenCreative+ commands...");
        Map<String,Class<? extends CommandExecutor>> commands = new HashMap<>();
        commands.put("creative",CommandCreative.class);
        commands.put("spawn",   CommandSpawn.class);
        commands.put("menu",    CommandMenu.class);
        commands.put("world",   CommandWorld.class);
        commands.put("chat",    CreativeChat.class);
        commands.put("join",    CommandJoin.class);
        commands.put("ad",      CommandAd.class);
        commands.put("play",    CommandPlay.class);
        commands.put("build",   CommandBuild.class);
        commands.put("dev",     CommandDev.class);
        commands.put("like",    CommandLike.class);
        commands.put("dislike", CommandDislike.class);
        for (String commandName : commands.keySet()) {
            PluginCommand command = getCommand(commandName);
            if (command != null) {
                try {
                    command.setExecutor(commands.get(commandName).newInstance());
                } catch (IllegalAccessException | InstantiationException ignored) {
                    sendCriticalErrorMessage("Couldn't register command " + commandName + ", because of " + ignored.getMessage());
                }
            } else {
                sendCriticalErrorMessage("Couldn't get command with name " + commandName + ", it is null. Maybe it doesn't exist in plugins.yml?");
            }
        }
        this.getLogger().info("OpenCreative+ registered all commands.");
    }

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
        this.getLogger().info("OpenCreative+ registered all event listeners.");
    }

}
