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

package mcchickenstudio.creative.commands;

import mcchickenstudio.creative.menu.CreativeMenu;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.Plugin;
import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.utils.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.FileUtils.loadLocales;
import static mcchickenstudio.creative.utils.MessageUtils.getElapsedTime;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.teleportToLobby;

public class CommandCreative implements CommandExecutor {

    final Plugin plugin = Main.getPlugin();
    final FileConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
                int cooldown = getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND);
                if (cooldown > 0) {
                    sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(cooldown)));
                    return true;
                }
                setCooldown(player, plugin.getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            }
            switch (args[0].toLowerCase()) {
                case ("reload"):
                    if (!sender.hasPermission("creative.reload")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    sender.sendMessage(getLocaleMessage("creative.reloading"));
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 100, 2);
                    }
                    plugin.reloadConfig();
                    loadLocales();
                    sender.sendMessage(getLocaleMessage("creative.reloaded"));
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 100, 2);
                    }
                    break;
                case ("resetlocale"):
                    if (!sender.hasPermission("creative.resetlocale")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    sender.sendMessage(getLocaleMessage("creative.resetting-locale"));
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 100, 2);
                    }                    FileUtils.resetLocales();
                    sender.sendMessage(getLocaleMessage("creative.reset-locale"));
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 100, 2);
                    }
                    break;
                case ("info"): {
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    Plot plot = PlotManager.getInstance().getPlotByWorldName("plot" + args[1]);
                    if (plot == null) {
                        sender.sendMessage(getLocaleMessage("no-plot-found"));
                        return true;
                    }
                    long now = System.currentTimeMillis();
                    sender.sendMessage(getLocaleMessage("world.info").replace("%name%", plot.getPlotName())
                            .replace("%id%", plot.worldID).replace("%creation-time%",getElapsedTime(now,plot.getCreationTime()))
                            .replace("%activity-time%",getElapsedTime(now,plot.getLastActivityTime())).replace("%online%",String.valueOf(plot.getOnline()))
                            .replace("%builders%",plot.getBuilders()).replace("%coders%",plot.getDevelopers()).replace("%owner%",plot.getOwner())
                            .replace("%sharing%", plot.getPlotSharing().getName()).replace("%mode%", plot.getPlotMode().getName()).replace("%description%", plot.getPlotDescription()));
                    break;
                }
                case ("load"): {
                    if (!sender.hasPermission("creative.load-world")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    Plot plot = PlotManager.getInstance().getPlotByWorldName("plot" + args[1]);
                    if (plot == null) {
                        sender.sendMessage(getLocaleMessage("no-plot-found"));
                        return true;
                    }
                    if (!plot.isLoaded) {
                        PlotManager.getInstance().loadPlot(plot);
                        sender.sendMessage(getLocaleMessage("world.loaded").replace("%id%",args[1]));
                    } else {
                        sender.sendMessage(getLocaleMessage("world.already-loaded").replace("%id%",args[1]));
                    }
                    break;
                }
                case ("maintenance"): {
                    if (!sender.hasPermission("creative.maintenance")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    if ("start".equalsIgnoreCase(args[1])) {
                        int seconds = 60;
                        if (args.length > 2) {
                            try {
                                seconds = Integer.parseInt(args[2]);
                            } catch (Exception ignored) {}
                        }
                        Main.getPlugin().getLogger().info("Maintenance mode will be enabled after " + seconds + " seconds by " + sender.getName());
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            onlinePlayer.playSound(onlinePlayer.getLocation(),Sound.BLOCK_BELL_USE,100,0.1f);
                            onlinePlayer.sendMessage(getLocaleMessage("creative.maintenance.starting-notification").replace("%time%",String.valueOf(seconds)));
                        }
                        int time = seconds;
                        new BukkitRunnable() {
                            int seconds = time;
                            @Override
                            public void run() {
                                if (seconds >= 1) {
                                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                        onlinePlayer.sendActionBar(getLocaleMessage("creative.maintenance.starting-in").replace("%time%",String.valueOf(seconds)));
                                    }
                                    if (seconds <= 3) {
                                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                            onlinePlayer.playSound(onlinePlayer.getLocation(),Sound.BLOCK_END_PORTAL_FRAME_FILL,100,2);
                                            onlinePlayer.sendMessage(getLocaleMessage("creative.maintenance.starting-in").replace("%time%",String.valueOf(seconds)));
                                        }
                                    }
                                    seconds--;
                                } else {
                                    Main.maintenance = true;
                                    Main.getPlugin().getConfig().set("maintenance",true);
                                    Main.getPlugin().saveConfig();
                                    Main.getPlugin().getLogger().info("Maintenance mode started! Unloading plots, please wait...");
                                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                        onlinePlayer.playSound(onlinePlayer.getLocation(),Sound.BLOCK_BEACON_POWER_SELECT,100,0.5f);
                                        onlinePlayer.sendMessage(getLocaleMessage("creative.maintenance.started"));
                                        for (Plot plot : PlotManager.getInstance().getPlots()) {
                                            if (plot.isLoaded) {
                                                for (Player player : plot.getPlayers()) {
                                                    teleportToLobby(player);
                                                }
                                            }
                                        }
                                    }
                                    cancel();
                                }
                            }
                        }.runTaskTimer(Main.getPlugin(),0L,20L);
                    } else if ("end".equalsIgnoreCase(args[1])) {
                        Main.maintenance = false;
                        Main.getPlugin().getConfig().set("maintenance",false);
                        Main.getPlugin().saveConfig();
                        Main.getPlugin().getLogger().info("Maintenance mode ended, now players can play in worlds.");
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            onlinePlayer.playSound(onlinePlayer.getLocation(),Sound.BLOCK_BEACON_POWER_SELECT,100,0.7f);
                            onlinePlayer.sendMessage(getLocaleMessage("creative.maintenance.ended"));
                        }
                    }
                    break;
                }
                case ("unload"): {
                    if (!sender.hasPermission("creative.unload-world")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    Plot plot = PlotManager.getInstance().getPlotByWorldName("plot" + args[1]);
                    if (plot == null) {
                        sender.sendMessage(getLocaleMessage("no-plot-found"));
                        return true;
                    }
                    if (plot.isLoaded) {
                        PlotManager.getInstance().unloadPlot(plot);
                        sender.sendMessage(getLocaleMessage("world.unloaded").replace("%id%",args[1]));
                    } else {
                        sender.sendMessage(getLocaleMessage("world.already-unloaded").replace("%id%",args[1]));
                    }
                    break;
                }
                case ("list"): {
                    if (!sender.hasPermission("creative.list")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    List<String> worlds = Bukkit.getServer().getWorlds().stream()
                            .map(WorldInfo::getName)
                            .filter(name -> name.startsWith("plot"))
                            .toList();
                    sender.sendMessage(getLocaleMessage("creative.loaded-worlds-list")
                            .replace("%amount%",String.valueOf(worlds.size()))
                            + String.join(", ",worlds));
                }
            }
        } else {
            String copyright = config.getString("messages.version");
            if (plugin == null || copyright == null || copyright.isEmpty()) {
                copyright = "\n§7 Open§fCreative§b+ §7%version%§f: §f%codename% \n §cMcChicken Studio 2017-2024\n ";
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', copyright.replace("%version%", Main.version).replace("%codename%",Main.codename)));
            if (sender instanceof Player player) {
                player.playSound(player.getLocation(),Sound.BLOCK_BEACON_ACTIVATE,100,2f);
                new CreativeMenu().open(player);
            }
        }
        return true;
    }
}
