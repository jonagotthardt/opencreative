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

package ua.mcchickenstudio.opencreative.commands;

import ua.mcchickenstudio.opencreative.menu.CreativeMenu;
import ua.mcchickenstudio.opencreative.menu.world.browsers.WorldsBrowserMenu;
import ua.mcchickenstudio.opencreative.menu.world.browsers.WorldsPickerMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.bukkit.generator.WorldInfo;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.loadLocales;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

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
                setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
            }
            switch (args[0].toLowerCase()) {
                case "reload" -> {
                    if (!sender.hasPermission("opencreative.reload")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    sender.sendMessage(getLocaleMessage("creative.reloading"));
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 100, 2);
                    }
                    OpenCreative.getPlugin().reloadConfig();
                    OpenCreative.getSettings().load(OpenCreative.getPlugin().getConfig());
                    loadLocales();
                    sender.sendMessage(getLocaleMessage("creative.reloaded"));
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 100, 2);
                    }
                }
                case "resetlocale" -> {
                    if (!sender.hasPermission("opencreative.resetlocale")) {
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
                    Planet planet = PlanetManager.getInstance().getPlanetByWorldName("planet" + args[1]);
                    if (planet == null) {
                        sender.sendMessage(getLocaleMessage("no-planet-found"));
                        return true;
                    }
                    long now = System.currentTimeMillis();
                    sender.sendMessage(getLocaleMessage("world.info").replace("%name%", planet.getInformation().getDisplayName())
                            .replace("%id%", String.valueOf(planet.getId())).replace("%creation-time%",getElapsedTime(now, planet.getCreationTime()))
                            .replace("%activity-time%",getElapsedTime(now, planet.getLastActivityTime())).replace("%online%",String.valueOf(planet.getOnline()))
                            .replace("%builders%", planet.getWorldPlayers().getBuilders()).replace("%coders%", planet.getWorldPlayers().getDevelopers()).replace("%owner%", planet.getOwner())
                            .replace("%sharing%", planet.getSharing().getName()).replace("%mode%", planet.getMode().getName()).replace("%description%", planet.getInformation().getDescription()));
                }
                case "load" -> {
                    if (!sender.hasPermission("opencreative.world.load")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    Planet planet = PlanetManager.getInstance().getPlanetByWorldName("planet" + args[1].replace("dev",""));
                    if (planet == null) {
                        sender.sendMessage(getLocaleMessage("no-planet-found"));
                        return true;
                    }
                    if (!planet.isLoaded()) {
                        planet.getTerritory().load();
                        sender.sendMessage(getLocaleMessage("world.loaded").replace("%id%",args[1]));
                    } else {
                        sender.sendMessage(getLocaleMessage("world.already-loaded").replace("%id%",args[1]));
                    }
                }
                case "dev" -> {
                    if (!sender.hasPermission("opencreative.world.dev.visit")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    Planet planet = PlanetManager.getInstance().getPlanetByWorldName("planet" + args[1].replace("dev",""));
                    if (planet == null) {
                        sender.sendMessage(getLocaleMessage("no-planet-found"));
                        return true;
                    }
                    if (planet.getDevPlanet().isLoaded()) {
                        sender.sendMessage(getLocaleMessage("world.already-loaded").replace("%id%",args[1]));
                        return true;
                    }
                    if (!planet.isLoaded()) {
                        planet.getTerritory().load();
                    }
                    planet.connectToDevPlanet(player);
                    sender.sendMessage(getLocaleMessage("world.loaded").replace("%id%",args[1]));
                }
                case "creative-chat" -> {
                    if (!sender.hasPermission("opencreative.creative-chat")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    if ("disable".equalsIgnoreCase(args[1])) {
                        OpenCreative.getSettings().setCreativeChatEnabled(false);
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            onlinePlayer.sendMessage(getLocaleMessage("creative.creative-chat.disabled").replace("%player%",sender.getName()));
                        }
                    } else if ("enable".equalsIgnoreCase(args[1])) {
                        OpenCreative.getSettings().setCreativeChatEnabled(true);
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
                case "debug" -> {
                    if (!sender.hasPermission("opencreative.debug")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    if ("disable".equalsIgnoreCase(args[1])) {
                        OpenCreative.getSettings().setDebug(false);
                        sender.sendMessage(getLocaleMessage("creative.debug.disabled").replace("%player%",sender.getName()));
                    } else if ("enable".equalsIgnoreCase(args[1])) {
                        OpenCreative.getSettings().setDebug(true);
                        sender.sendMessage(getLocaleMessage("creative.debug.enabled").replace("%player%",sender.getName()));
                    }
                }
                case "locale", "lang", "language" -> {
                    if (!sender.hasPermission("opencreative.locale")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    if (localizationFileExists(args[1])) {
                        OpenCreative.getPlugin().getConfig().set("messages.locale",args[1]);
                        OpenCreative.getPlugin().saveConfig();
                        loadLocalizationFile();
                        sender.sendMessage(getLocaleMessage("creative.locale.changed"));
                    } else {
                        sender.sendMessage(getLocaleMessage("creative.locale.not-found"));
                    }

                }
                case "kick-all" -> {
                    if (!sender.hasPermission("opencreative.kick-all")) {
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
                    if (!sender.hasPermission("opencreative.maintenance")) {
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
                        OpenCreative.getPlugin().getLogger().info("Maintenance mode will be enabled after " + seconds + " seconds by " + sender.getName());
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
                                    OpenCreative.getSettings().setMaintenance(true);
                                    cancel();
                                }
                            }
                        }.runTaskTimer(OpenCreative.getPlugin(),0L,20L);
                    } else if ("end".equalsIgnoreCase(args[1])) {
                        OpenCreative.getSettings().setMaintenance(false);
                    }
                }
                case "unload" -> {
                    if (!sender.hasPermission("opencreative.world.unload")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    Planet planet = PlanetManager.getInstance().getPlanetByWorldName("planet" + args[1]);
                    if (planet == null) {
                        sender.sendMessage(getLocaleMessage("no-planet-found"));
                        return true;
                    }
                    if (planet.isLoaded()) {
                        planet.getTerritory().unload();
                        sender.sendMessage(getLocaleMessage("world.unloaded").replace("%id%",args[1]));
                    } else {
                        sender.sendMessage(getLocaleMessage("world.already-unloaded").replace("%id%",args[1]));
                    }
                }
                case "list" -> {
                    if (!sender.hasPermission("opencreative.list.loaded")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    List<String> worlds = Bukkit.getServer().getWorlds().stream()
                            .map(WorldInfo::getName)
                            .filter(name -> name.startsWith("planet"))
                            .toList();
                    sender.sendMessage(getLocaleMessage("creative.loaded-worlds-list")
                            .replace("%amount%",String.valueOf(worlds.size()))
                            + String.join(", ",worlds));
                }
                case "deprecated" -> {
                    if (!sender.hasPermission("opencreative.list.deprecated")) {
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
                    List<Planet> deprecatedWorlds = new ArrayList<>();
                    for (Planet planet : PlanetManager.getInstance().getPlanets()) {
                        long monthsInMillis = 2592000000L*months;
                        if (currentTime- planet.getCreationTime() > monthsInMillis) {
                            OfflinePlayer planetOwner = Bukkit.getOfflinePlayer(planet.getOwner());
                            if (planetOwner.getLastSeen() == 0 || currentTime-planetOwner.getLastLogin() > monthsInMillis) {
                                deprecatedWorlds.add(planet);
                            }
                        }
                    }
                    sender.sendMessage(getLocaleMessage("creative.deprecated-worlds.list")
                            .replace("%amount%",String.valueOf(deprecatedWorlds.size())));
                    String worldMessage = getLocaleMessage("creative.deprecated-worlds.world");
                    for (Planet planet : deprecatedWorlds) {
                        sender.sendMessage(Component.text(worldMessage
                                .replace("%id%", String.valueOf(planet.getId()))
                                .replace("%owner%", planet.getOwner())
                                .replace("%created%",getElapsedTime(currentTime, planet.getCreationTime()))
                                .replace("%seen%",getElapsedTime(currentTime,Bukkit.getOfflinePlayer(planet.getOwner()).getLastSeen())
                                )).clickEvent(ClickEvent.runCommand("/join " + planet.getId()))
                        );
                    }
                }
                case "corrupted" -> {
                    if (!sender.hasPermission("opencreative.list.corrupted")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    Set<Planet> corruptedPlanets = PlanetManager.getInstance().getCorruptedPlanets();
                    sender.sendMessage(getLocaleMessage("creative.corrupted-worlds.list")
                            .replace("%amount%",String.valueOf(corruptedPlanets.size())));
                    String worldMessage = getLocaleMessage("creative.corrupted-worlds.world");
                    for (Planet planet : corruptedPlanets) {
                        sender.sendMessage(Component.text(worldMessage
                                .replace("%id%", String.valueOf(planet.getId()))
                                ).clickEvent(ClickEvent.runCommand("/join " + planet.getId()))
                        );
                    }
                }
                case "print" -> {
                    if (!sender.hasPermission("opencreative.print")) {
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
                    if (!sender.hasPermission("opencreative.test")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (player == null) return true;
                    DevPlanet devPlanet = PlanetManager.getInstance().getDevPlanet(player);
                    if (devPlanet == null) return true;
                    player.sendMessage("Test of dev planet helper");
                }
                case "test2" -> {
                    if (!sender.hasPermission("opencreative.test")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (player == null) return true;
                    player.sendMessage("Test of worlds downloader");
                    WorldsBrowserMenu menu = new WorldsPickerMenu(player, new HashSet<>(PlanetManager.getInstance().getPlanets().stream().filter(planet -> planet.getInformation().isDownloadable()).toList()));
                    menu.open(player);
                }
                case "test3" -> {
                    if (!sender.hasPermission("opencreative.test")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    player.sendMessage("Test of legacy convertor");
                    //new PlayerToEntityConvertor(new ArrayList<>(PlanetManager.getInstance().getPlanets())).start();
                }
                case "template" -> {
                    if (!sender.hasPermission("opencreative.template")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (player == null) return true;
                    if (args.length == 1) return true;
                    File template = new File(OpenCreative.getPlugin().getDataPath()+File.separator+"templates"+File.separator+args[1]);
                    if (!template.exists()) {
                        sender.sendMessage("Template doesn't exists.");
                        return true;
                    }
                    int id = WorldUtils.generateWorldID();
                    File world = new File(Bukkit.getWorldContainer().getPath()+File.separator+"unloadedWorlds"+File.separator+"planet"+id+File.separator);
                    FileUtils.copyFilesToDirectory(template,world);
                    PlanetManager.getInstance().createPlanet(player, id, WorldUtils.WorldGenerator.FLAT);
                }
            }
        } else {
            String copyright = OpenCreative.getPlugin().getConfig().getString("messages.version","\n§7 Open§fCreative§b+ §7%version%§f: §f%codename% \n §cMcChicken Studio 2017-2025\n ");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', copyright.replace("%version%", OpenCreative.getVersion()).replace("%codename%", OpenCreative.getCodename())));
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
        if (!sender.hasPermission("opencreative.admin")) return null;
        if (args.length == 1) {
            tabCompleter.add("reload");
            tabCompleter.add("locale");
            tabCompleter.add("debug");
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
            }  else if ("debug".equalsIgnoreCase(args[0])) {
                tabCompleter.add("enable");
                tabCompleter.add("disable");
            } else if ("load".equalsIgnoreCase(args[0]) || "unload".equalsIgnoreCase(args[0])) {
                tabCompleter.addAll(PlanetManager.getInstance().getPlanets().stream().map(planet -> String.valueOf(planet.getId())).toList());
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
