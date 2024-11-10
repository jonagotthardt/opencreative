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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.bukkit.generator.WorldInfo;
import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.utils.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.FileUtils.loadLocales;
import static mcchickenstudio.creative.utils.MessageUtils.getElapsedTime;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.hidePlayerInTab;
import static mcchickenstudio.creative.utils.PlayerUtils.teleportToLobby;

public class CommandCreative implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
                int cooldown = getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND);
                if (cooldown > 0) {
                    sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(cooldown)));
                    return true;
                }
                setCooldown(player, Main.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            }
            switch (args[0].toLowerCase()) {
                case "reload" -> {
                    if (!sender.hasPermission("creative.reload")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    sender.sendMessage(getLocaleMessage("creative.reloading"));
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 100, 2);
                    }
                    Main.getPlugin().reloadConfig();
                    Main.getSettings().load(Main.getPlugin().getConfig());
                    loadLocales();
                    sender.sendMessage(getLocaleMessage("creative.reloaded"));
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 100, 2);
                    }
                }
                case "resetlocale" -> {
                    if (!sender.hasPermission("creative.resetlocale")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    sender.sendMessage(getLocaleMessage("creative.resetting-locale"));
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 100, 2);
                    }
                    FileUtils.resetLocales();
                    sender.sendMessage(getLocaleMessage("creative.reset-locale"));
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 100, 2);
                    }
                }
                case "info" -> {
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
                    sender.sendMessage(getLocaleMessage("world.info").replace("%name%", plot.getInformation().getDisplayName())
                            .replace("%id%", String.valueOf(plot.getId())).replace("%creation-time%",getElapsedTime(now,plot.getCreationTime()))
                            .replace("%activity-time%",getElapsedTime(now,plot.getLastActivityTime())).replace("%online%",String.valueOf(plot.getOnline()))
                            .replace("%builders%", plot.getWorldPlayers().getBuilders()).replace("%coders%", plot.getWorldPlayers().getDevelopers()).replace("%owner%",plot.getOwner())
                            .replace("%sharing%", plot.getPlotSharing().getName()).replace("%mode%", plot.getMode().getName()).replace("%description%", plot.getInformation().getDescription()));
                }
                case "load" -> {
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
                    if (!plot.isLoaded()) {
                        PlotManager.getInstance().loadPlot(plot);
                        sender.sendMessage(getLocaleMessage("world.loaded").replace("%id%",args[1]));
                    } else {
                        sender.sendMessage(getLocaleMessage("world.already-loaded").replace("%id%",args[1]));
                    }
                }
                case "creative-chat" -> {
                    if (!sender.hasPermission("creative.creative-chat")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    if ("disable".equalsIgnoreCase(args[1])) {
                        CreativeChat.setChatEnabled(false);
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            onlinePlayer.sendMessage(getLocaleMessage("creative.creative-chat.disabled").replace("%player%",sender.getName()));
                        }
                    } else if ("enable".equalsIgnoreCase(args[1])) {
                        CreativeChat.setChatEnabled(true);
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            onlinePlayer.sendMessage(getLocaleMessage("creative.creative-chat.enabled").replace("%player%",sender.getName()));
                        }
                    } if ("clear".equalsIgnoreCase(args[1])) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            onlinePlayer.sendMessage("\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n  \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n ");
                            onlinePlayer.sendMessage(getLocaleMessage("creative.creative-chat.cleared").replace("%player%",sender.getName()));
                        }
                    }
                }
                case "kick-all" -> {
                    if (!sender.hasPermission("creative.kick-all")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 3) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    String nickname = args[2];
                    if ("starts".equalsIgnoreCase(args[1])) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (onlinePlayer.getName().toLowerCase().startsWith(nickname.toLowerCase())) {
                                onlinePlayer.kick();
                            }
                        }
                    } else if ("ends".equalsIgnoreCase(args[1])) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (onlinePlayer.getName().toLowerCase().endsWith(nickname.toLowerCase())) {
                                onlinePlayer.kick();
                            }
                        }
                    } else if ("contains".equalsIgnoreCase(args[1])) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (onlinePlayer.getName().toLowerCase().contains(nickname.toLowerCase())) {
                                onlinePlayer.kick();
                            }
                        }
                    } else if ("ignore".equalsIgnoreCase(args[1])) {
                        List<String> nicknames = new ArrayList<>(List.of(args)).subList(1,args.length);
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            boolean ignore = false;
                            for (String nick : nicknames) {
                                if (nick.equalsIgnoreCase(onlinePlayer.getName()) || onlinePlayer.getName().equalsIgnoreCase(sender.getName())) {
                                    ignore = true;
                                }
                            }
                            if (!ignore) {
                                onlinePlayer.kick();
                            }
                        }
                    }
                }
                case "maintenance" -> {
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
                                            if (plot.isLoaded()) {
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
                }
                case "unload" -> {
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
                    if (plot.isLoaded()) {
                        PlotManager.getInstance().unloadPlot(plot);
                        sender.sendMessage(getLocaleMessage("world.unloaded").replace("%id%",args[1]));
                    } else {
                        sender.sendMessage(getLocaleMessage("world.already-unloaded").replace("%id%",args[1]));
                    }
                }
                case "list" -> {
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
                case "deprecated" -> {
                    if (!sender.hasPermission("creative.list.deprecated")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    int months = 1;
                    if (args.length >= 2) {
                        try {
                            months = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ignored) {}
                    }
                    if (months < 1) months = 1;
                    long currentTime = System.currentTimeMillis();
                    List<Plot> deprecatedWorlds = new ArrayList<>();
                    for (Plot plot : PlotManager.getInstance().getPlots()) {
                        long monthsInMillis = 2592000000L*months;
                        if (currentTime-plot.getCreationTime() > monthsInMillis) {
                            OfflinePlayer plotOwner = Bukkit.getOfflinePlayer(plot.getOwner());
                            if (plotOwner.getLastSeen() == 0 || currentTime-plotOwner.getLastLogin() > monthsInMillis) {
                                deprecatedWorlds.add(plot);
                            }
                        }
                    }
                    sender.sendMessage(getLocaleMessage("creative.deprecated-worlds.list")
                            .replace("%amount%",String.valueOf(deprecatedWorlds.size())));
                    String worldMessage = getLocaleMessage("creative.deprecated-worlds.world");
                    for (Plot plot : deprecatedWorlds) {
                        sender.sendMessage(Component.text(worldMessage
                                .replace("%id%", String.valueOf(plot.getId()))
                                .replace("%owner%",plot.getOwner())
                                .replace("%created%",getElapsedTime(currentTime,plot.getCreationTime()))
                                .replace("%seen%",getElapsedTime(currentTime,Bukkit.getOfflinePlayer(plot.getOwner()).getLastSeen())
                                )).clickEvent(ClickEvent.runCommand("/join " + plot.getId()))
                        );
                    }
                }
                case "corrupted" -> {
                    if (!sender.hasPermission("creative.list.corrupted")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    Set<Plot> corruptedPlots = PlotManager.getInstance().getCorruptedPlots();
                    sender.sendMessage(getLocaleMessage("creative.corrupted-worlds.list")
                            .replace("%amount%",String.valueOf(corruptedPlots.size())));
                    String worldMessage = getLocaleMessage("creative.corrupted-worlds.world");
                    for (Plot plot : corruptedPlots) {
                        sender.sendMessage(Component.text(worldMessage
                                .replace("%id%", String.valueOf(plot.getId()))
                                ).clickEvent(ClickEvent.runCommand("/join " + plot.getId()))
                        );
                    }
                }
                case "print" -> {
                    if (!sender.hasPermission("creative.print")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    sender.sendMessage(getLocaleMessage(args[1]));
                }
                case "test" -> {
                    if (!Main.debug) {
                        return true;
                    }
                    if (!sender.hasPermission("creative.test")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (player != null) {
                        hidePlayerInTab(player,player);
                    }
                }
            }
        } else {
            String copyright = Main.getPlugin().getConfig().getString("messages.version","\n§7 Open§fCreative§b+ §7%version%§f: §f%codename% \n §cMcChicken Studio 2017-2024\n ");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', copyright.replace("%version%", Main.version).replace("%codename%",Main.codename)));
            if (sender instanceof Player player) {
                player.playSound(player.getLocation(),Sound.BLOCK_BEACON_ACTIVATE,100,2f);
                new CreativeMenu().open(player);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (args.length == 1) {
            tabCompleter.add("reload");
            tabCompleter.add("maintenance");
            tabCompleter.add("load");
            tabCompleter.add("unload");
            tabCompleter.add("resetlocale");
            tabCompleter.add("creative-chat");
            tabCompleter.add("kick-all");
            tabCompleter.add("list");
            tabCompleter.add("deprecated");
            tabCompleter.add("corrupted");
        } else if (args.length == 2) {
            if ("maintenance".equalsIgnoreCase(args[0])) {
                tabCompleter.add("start");
                tabCompleter.add("end");
            } else if ("kick-all".equalsIgnoreCase(args[0])) {
                tabCompleter.add("starts");
                tabCompleter.add("ends");
                tabCompleter.add("contains");
                tabCompleter.add("ignore");
            } else if ("creative-chat".equalsIgnoreCase(args[0])) {
                tabCompleter.add("enable");
                tabCompleter.add("disable");
                tabCompleter.add("clear");
            }  else if ("load".equalsIgnoreCase(args[0]) || "unload".equalsIgnoreCase(args[0])) {
                tabCompleter.addAll(PlotManager.getInstance().getPlots().stream().map(plot -> String.valueOf(plot.getId())).toList());
            }
        } else if (args.length == 3) {
            if ("start".equalsIgnoreCase(args[1])) {
                tabCompleter.add("120");
                tabCompleter.add("60");
                tabCompleter.add("30");
                tabCompleter.add("15");
            }
        }
        return tabCompleter;
    }
}
