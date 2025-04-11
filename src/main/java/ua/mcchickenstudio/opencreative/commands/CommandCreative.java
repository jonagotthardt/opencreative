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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.indev.ExecutorTypeSelectionMenu;
import ua.mcchickenstudio.opencreative.indev.Items;
import ua.mcchickenstudio.opencreative.indev.MenusCategorySelectionMenu;
import ua.mcchickenstudio.opencreative.indev.modules.Module;
import ua.mcchickenstudio.opencreative.menus.CreativeMenu;
import ua.mcchickenstudio.opencreative.menus.world.WorldModerationMenu;
import ua.mcchickenstudio.opencreative.menus.world.browsers.WorldsBrowserMenu;
import ua.mcchickenstudio.opencreative.menus.world.browsers.WorldsPickerMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.loadLocales;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.setPlanetConfigParameter;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>CommandCreative</h1>
 * This command has special tools only for server admins.
 * Can change plugin's behaviour, change settings or
 * manipulate with worlds.
 * <p>
 * Available: For server admins with specific permissions.
 */
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
                        Sounds.RELOADING.play(player);
                    }
                    OpenCreative.getPlugin().reloadConfig();
                    OpenCreative.getSettings().load(OpenCreative.getPlugin().getConfig());
                    loadLocales();
                    sender.sendMessage(getLocaleMessage("creative.reloaded"));
                    if (player != null) {
                        Sounds.RELOADED.play(player);
                    }
                }
                case "resetlocale" -> {
                    if (!sender.hasPermission("opencreative.resetlocale")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    sender.sendMessage(getLocaleMessage("creative.resetting-locale"));
                    if (player != null) {
                        Sounds.RELOADING.play(player);
                    }
                    FileUtils.resetLocales();
                    sender.sendMessage(getLocaleMessage("creative.reset-locale"));
                    if (player != null) {
                        Sounds.RELOADED.play(player);
                    }
                }
                case "info" -> {
                    if (!sender.hasPermission("opencreative.info")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorldName("./planets/planet" + args[1]);
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
                case "delete" -> {
                    if (!sender.hasPermission("opencreative.delete")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorldName("./planets/planet" + args[1]);
                    if (planet == null) {
                        sender.sendMessage(getLocaleMessage("no-planet-found"));
                        return true;
                    }
                    OpenCreative.getPlugin().getLogger().info("Deleting a world " + args[1] + ", please wait...");
                    OpenCreative.getPlanetsManager().deletePlanet(planet);
                }
                case "moderate", "moderation" -> {
                    if (player == null) return true;
                    if (!sender.hasPermission("opencreative.moderation.menus")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    Planet planet;
                    if (args.length == 1) {
                        planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                        if (planet == null) {
                            sender.sendMessage(getLocaleMessage("too-few-args"));
                            return true;
                        }
                    } else {
                        planet = OpenCreative.getPlanetsManager().getPlanetById(args[1]);
                    }
                    if (planet == null) {
                        sender.sendMessage(getLocaleMessage("no-planet-found"));
                        return true;
                    }
                    new WorldModerationMenu(planet).open(player);
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
                    Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorldName("./planets/planet" + args[1].replace("dev",""));
                    if (planet == null) {
                        sender.sendMessage(getLocaleMessage("no-planet-found"));
                        return true;
                    }
                    if (!planet.isLoaded()) {
                        planet.getTerritory().load();
                        sender.sendMessage(getLocaleMessage("world.loaded").replace("%id%",args[1]));
                    } else if (args[1].contains("dev") && !planet.getDevPlanet().isLoaded()) {
                        planet.getDevPlanet().loadDevPlanetWorld();
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
                    Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorldName("./planets/planet" + args[1].replace("dev",""));
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
                    if ("disable".equalsIgnoreCase(args[1]) || "off".equalsIgnoreCase(args[1])) {
                        OpenCreative.getSettings().setDebug(false);
                        sender.sendMessage(getLocaleMessage("creative.debug.disabled").replace("%player%",sender.getName()));
                    } else if ("enable".equalsIgnoreCase(args[1]) || "on".equalsIgnoreCase(args[1])) {
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
                case "sounds", "soundtheme", "soundstheme" -> {
                    if (!sender.hasPermission("opencreative.sounds.theme")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    if (OpenCreative.getSettings().setSoundsTheme(args[1])) {
                        sender.sendMessage(getLocaleMessage("creative.sounds.set").replace("%theme%",args[1]));
                        if (player != null) {
                            Sounds.LOBBY.play(player);
                        }
                    } else {
                        sender.sendMessage(getLocaleMessage("creative.sounds.not-found").replace("%theme%",args[1]));
                    }
                }
                case "sound", "playsound" -> {
                    if (!sender.hasPermission("opencreative.sounds.play")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    try {
                        Sounds sound = Sounds.valueOf(args[1].toUpperCase());
                        sound.play(sender);
                    } catch (Exception e) {
                        return false;
                    }
                }
                case "item", "items" -> {
                    if (!sender.hasPermission("opencreative.items.get") && !sender.hasPermission("opencreative.items.set")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (player == null) {
                        return true;
                    }
                    if (args.length < 3) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return true;
                    }
                    Items itemType = null;
                    try {
                        itemType = Items.valueOf(args[2].toUpperCase());
                    } catch (Exception error) {
                        Sounds.PLAYER_FAIL.play(player);
                        return true;
                    }
                    switch (args[1].toLowerCase()) {
                        case "get" -> player.getInventory().addItem(itemType.get());
                        case "set" -> {
                            ItemStack item = player.getInventory().getItemInMainHand();
                            if (item.isEmpty()) {
                                Sounds.PLAYER_FAIL.play(player);
                                return true;
                            }
                            OpenCreative.getSettings().setCustomItem(itemType,item);
                        }
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
                            Sounds.MAINTENANCE_NOTIFY.play(onlinePlayer);
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
                                            Sounds.MAINTENANCE_COUNT.play(onlinePlayer);
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
                    Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorldName("./planets/planet" + args[1]);
                    if (planet == null) {
                        sender.sendMessage(getLocaleMessage("no-planet-found"));
                        return true;
                    }
                    if (planet.isLoaded()) {
                        planet.getTerritory().unload();
                        sender.sendMessage(getLocaleMessage("world.unloaded").replace("%id%",args[1]));
                    } else if (args[1].contains("dev") && planet.getDevPlanet().isLoaded()) {
                        planet.getDevPlanet().unload();
                        sender.sendMessage(getLocaleMessage("world.unloaded").replace("%id%",args[1]));
                    } else {
                        sender.sendMessage(getLocaleMessage("world.already-unloaded").replace("%id%",args[1]));
                    }
                }
                case "update", "updates", "checkupdate" -> {
                    if (!sender.hasPermission("opencreative.update")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    OpenCreative.getUpdater().checkUpdates().thenAccept(
                            version -> {
                                if (version.isEmpty()) {
                                    sender.sendMessage(getLocaleMessage("creative.updates.up-to-date")
                                            .replace("%version%",OpenCreative.getPlugin().getPluginMeta().getVersion()));
                                } else {
                                    sender.sendMessage(getLocaleMessage("creative.updates.available")
                                            .replace("%new%",version)
                                            .replace("%old%",OpenCreative.getPlugin().getPluginMeta().getVersion()));
                                }
                            }
                    ).exceptionally(e -> {
                        sender.sendMessage(getLocaleMessage("creative.updates.cant-check"));
                        return null;
                    });
                }
                case "list" -> {
                    if (!sender.hasPermission("opencreative.list.loaded")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    List<String> worlds = Bukkit.getServer().getWorlds().stream()
                            .filter(WorldUtils::isPlanet)
                            .map(WorldUtils::getPlanetIdFromName)
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
                    for (Planet planet : OpenCreative.getPlanetsManager().getPlanets()) {
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
                    if (args.length < 3) {
                        if (!sender.hasPermission("opencreative.list.corrupted")) {
                            sender.sendMessage(getLocaleMessage("no-perms"));
                            return true;
                        }
                        Set<Planet> corruptedPlanets = OpenCreative.getPlanetsManager().getCorruptedPlanets();
                        sender.sendMessage(getLocaleMessage("creative.corrupted-worlds.list")
                                .replace("%amount%",String.valueOf(corruptedPlanets.size())));
                        String worldMessage = getLocaleMessage("creative.corrupted-worlds.world");
                        for (Planet planet : corruptedPlanets) {
                            sender.sendMessage(Component.text(worldMessage
                                            .replace("%id%", String.valueOf(planet.getId()))
                                    ).clickEvent(ClickEvent.runCommand("/oc corrupted " + planet.getId() + " join"))
                            );
                        }
                        return true;
                    }
                    if (!sender.hasPermission("opencreative.corrupted.recovery")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    int id = -1;
                    try {
                        id = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {}
                    if (id < 0) return true;
                    Planet foundPlanet = null;
                    for (Planet planet : OpenCreative.getPlanetsManager().getCorruptedPlanets()) {
                        if (planet.getId() == id) {
                            foundPlanet = planet;
                            break;
                        }
                    }
                    if (foundPlanet == null) {
                        sender.sendMessage(getLocaleMessage("no-planet-found"));
                        return true;
                    }
                    String action = args[2];
                    switch (action.toLowerCase()) {
                        case "teleport", "tp", "join", "load" -> {
                            if (player == null) return true;
                            foundPlanet.getTerritory().load();
                            foundPlanet.connectPlayer(player);
                        }
                        case "unload" -> {
                            if (player == null) return true;
                            foundPlanet.getTerritory().unload();
                        }
                        case "owner", "setowner" -> {
                            if (args.length < 4) {
                                sender.sendMessage(getLocaleMessage("too-few-args"));
                                return true;
                            }
                            sender.sendMessage(getLocaleMessage("creative.corrupted-worlds.set-owner").replace("%replace%",args[3]));
                            if (foundPlanet.getCreationTime() == 0) setPlanetConfigParameter(foundPlanet,"creation-time",System.currentTimeMillis());
                            if (foundPlanet.getLastActivityTime() == 0) setPlanetConfigParameter(foundPlanet,"last-activity-time",System.currentTimeMillis());
                            foundPlanet.setOwner(args[3]);
                            OpenCreative.getPlanetsManager().getCorruptedPlanets().remove(foundPlanet);
                            Planet planet = new Planet(foundPlanet.getId());
                            OpenCreative.getPlanetsManager().registerPlanet(planet);
                        }
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
                case "stability" -> {
                    if (!sender.hasPermission("opencreative.stability")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    player.sendMessage(getLocaleMessage("creative.stability.actionbar")
                            .replace("%memory%", OpenCreative.getStability().getMemoryState().getLocalized())
                            .replace("%storage%", OpenCreative.getStability().getStorageState().getLocalized())
                            .replace("%tps%", OpenCreative.getStability().getTicksState().getLocalized())
                            .replace("%database%", OpenCreative.getStability().getDatabaseState().getLocalized())
                    );
                }
                case "test" -> {
                    if (!sender.hasPermission("opencreative.test")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (player == null) return true;
                    if (args.length == 1) return true;
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        player.sendMessage("only dev planet");
                        return true;
                    }
                    DevPlatform platform = devPlanet.getPlatformInLocation(player.getLocation());
                    if (platform == null) return true;
                    Module module = new Module(1);
                    module.place(devPlanet, player);
                }
                case "test3" -> {
                    if (!sender.hasPermission("opencreative.test")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return true;
                    }
                    if (player == null) return true;
                    player.sendMessage("Test of worlds downloader");
                    WorldsBrowserMenu menu = new WorldsPickerMenu(player, new HashSet<>(OpenCreative.getPlanetsManager().getPlanets().stream().filter(planet -> planet.getInformation().isDownloadable()).toList()));
                    menu.open(player);
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
                    if (!OpenCreative.getStability().isFine()) {
                        player.sendMessage(getLocaleMessage("creative.stability.cannot"));
                        Sounds.PLAYER_FAIL.play(player);
                        return true;
                    }
                    int id = WorldUtils.generateWorldID();
                    File world = new File(Bukkit.getWorldContainer().getPath()+File.separator+"planets"+File.separator+"planet"+id+File.separator);
                    FileUtils.copyFilesToDirectory(template,world);
                    OpenCreative.getPlanetsManager().createPlanet(player, id, WorldUtils.WorldGenerator.FLAT);
                }
                default -> {
                    String copyright = OpenCreative.getPlugin().getConfig().getString("messages.version","\n§7 Open§fCreative§b+ §7%version%§f: §f%codename% \n §cMcChicken Studio 2017-2025\n ");
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', copyright.replace("%version%", OpenCreative.getVersion()).replace("%codename%", OpenCreative.getCodename())));
                    if (player != null) {
                        Sounds.OPENCREATIVE.play(player);
                        new CreativeMenu().open(player);
                    }
                }
            }
        } else {
            String copyright = OpenCreative.getPlugin().getConfig().getString("messages.version","\n§7 Open§fCreative§b+ §7%version%§f: §f%codename% \n §cMcChicken Studio 2017-2025\n ");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', copyright.replace("%version%", OpenCreative.getVersion()).replace("%codename%", OpenCreative.getCodename())));
            if (sender instanceof Player player) {
                Sounds.OPENCREATIVE.play(player);
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
            tabCompleter.add("update");
            tabCompleter.add("moderation");
            tabCompleter.add("reload");
            tabCompleter.add("locale");
            tabCompleter.add("debug");
            tabCompleter.add("maintenance");
            tabCompleter.add("stability");
            tabCompleter.add("load");
            tabCompleter.add("unload");
            tabCompleter.add("resetlocale");
            tabCompleter.add("creative-chat");
            tabCompleter.add("kick-all");
            tabCompleter.add("list");
            tabCompleter.add("deprecated");
            tabCompleter.add("corrupted");
            tabCompleter.add("sound");
            tabCompleter.add("sounds");
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
            } else if ("load".equalsIgnoreCase(args[0]) || "unload".equalsIgnoreCase(args[0]) || "moderation".equalsIgnoreCase(args[0])) {
                tabCompleter.addAll(OpenCreative.getPlanetsManager().getPlanets().stream().map(planet -> String.valueOf(planet.getId())).toList());
            } else if ("corrupted".equalsIgnoreCase(args[0])) {
                tabCompleter.addAll(OpenCreative.getPlanetsManager().getCorruptedPlanets().stream().map(planet -> String.valueOf(planet.getId())).toList());
            } else if ("locale".equalsIgnoreCase(args[0])) {
                tabCompleter.add("en");
                tabCompleter.add("ru");
            } else if ("sounds".equalsIgnoreCase(args[0])) {
                ConfigurationSection config = OpenCreative.getPlugin().getConfig().getConfigurationSection("sounds");
                if (config == null) return null;
                tabCompleter.addAll(config.getKeys(false));
                tabCompleter.remove("theme");
            } else if ("sound".equalsIgnoreCase(args[0]) || "playsound".equalsIgnoreCase(args[0])) {
                tabCompleter.addAll(Arrays.stream(Sounds.values()).map(s -> s.name().toLowerCase()).filter(s -> s.startsWith(args[1].toLowerCase())).toList());
            } else if ("item".equalsIgnoreCase(args[0]) || "items".equalsIgnoreCase(args[0])) {
                tabCompleter.addAll(Arrays.stream(Items.values()).map(s -> s.name().toLowerCase()).filter(s -> s.startsWith(args[1].toLowerCase())).toList());
            }
        } else if (args.length == 3) {
            if ("start".equalsIgnoreCase(args[1])) {
                tabCompleter.add("120");
                tabCompleter.add("60");
                tabCompleter.add("30");
                tabCompleter.add("15");
            } else if ("corrupted".equalsIgnoreCase(args[0])) {
                tabCompleter.add("owner");
                tabCompleter.add("join");
                tabCompleter.add("unload");
            }
        }
        return tabCompleter;
    }
}
