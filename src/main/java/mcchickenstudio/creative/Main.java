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
import mcchickenstudio.creative.utils.HookUtils;
import mcchickenstudio.creative.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static mcchickenstudio.creative.utils.PlayerUtils.teleportToLobby;

public final class Main extends JavaPlugin {

    public static Main plugin;
    public static final String version = "1.5 Dev. Build 5";
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
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',"&f&lCREATIVE&b&l+" + version),
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
            player.sendActionBar(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7Open&fCreative&b+ &7" + version + "&f is loaded for " + loadedTime + " ms.")));
        }

        Main.getPlugin().getLogger().info("OpenCreative+ " + version + ": " + codename + " is loaded for " + loadedTime);
        Main.getPlugin().getLogger().info(" ");
        Main.getPlugin().getLogger().info(" Welcome to OpenCreative+ " + version + "!");
        Main.getPlugin().getLogger().info(" ");

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

    private void checkDebug() {
        if (getConfig().getBoolean("debug",false)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendActionBar(Component.text("§7OpenCreative+ " + version + " Debug Mode. §fShh.. let's not leak our hard work..."));
                    }
                }
            }.runTaskTimer(this,20L,20L);
        }
        debug = getConfig().getBoolean("debug",false);
    }

    private void registerCommands() {
        this.getLogger().info("Creative+ is registering commands...");
        getCommand("creative").setExecutor(new CommandCreative());
        getCommand("menu").setExecutor(new CommandMenu());
        getCommand("spawn").setExecutor(new CommandSpawn());
        getCommand("world").setExecutor(new CommandWorld());
        getCommand("chat").setExecutor(new CreativeChat());
        getCommand("join").setExecutor(new CommandJoin());
        getCommand("ad").setExecutor(new CommandAd());
        getCommand("join").setTabCompleter(new CommandTabJoin());
        getCommand("play").setExecutor(new CommandPlay());
        getCommand("build").setExecutor(new CommandBuild());
        getCommand("dev").setExecutor(new CommandDev());
        getCommand("like").setExecutor(new CommandLike());
        getCommand("dislike").setExecutor(new CommandDislike());
        this.getLogger().info("Creative+ registered all commands.");
    }

    private void registerEvents() {
        this.getLogger().info("Creative+ is registering events...");
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
        this.getLogger().info("Creative+ registered all events.");
    }

}
