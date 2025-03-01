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

package ua.mcchickenstudio.opencreative.commands.world;

import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.events.planet.PlanetSharingChangeEvent;
import ua.mcchickenstudio.opencreative.menus.world.settings.EntitiesBrowserMenu;
import ua.mcchickenstudio.opencreative.menus.world.settings.WorldSettingsMenu;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInDevPlanet;

public class CommandWorld implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return true;
        }
        if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
            player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
            return true;
        }
        setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) {
            player.sendMessage(getLocaleMessage("only-in-world"));
            return true;
        }
        String arg = args.length == 0 ? "" : args[0].toLowerCase();
        switch (arg) {
            case "deletemobs", "mobs", "entities" -> {
                if (planet.getWorldPlayers().canBuild(player)) {
                    new EntitiesBrowserMenu(player,planet).open(player);
                } else {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                }
            }
            case "setspawn" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                if (isEntityInDevPlanet(player)) {
                    Sounds.PLAYER_FAIL.play(player);
                    return true;
                }
                player.getWorld().setSpawnLocation(player.getLocation());
                player.showTitle(Title.title(
                        toComponent(getLocaleMessage("settings.world-spawn.title")), toComponent(getLocaleMessage("settings.world-spawn.subtitle")),
                        Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(2), Duration.ofMillis(130))
                ));
                Sounds.WORLD_SETTINGS_SPAWN_SET.play(player);
            }
            case "spawn" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                if (isEntityInDevPlanet(player)) {
                    Sounds.PLAYER_FAIL.play(player);
                    return true;
                }
                player.teleport(player.getWorld().getSpawnLocation());
                Sounds.WORLD_SETTINGS_SPAWN_TELEPORT.play(player);
            }
            case "close" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                if (planet.getSharing() != Planet.Sharing.PUBLIC) {
                    Sounds.PLAYER_FAIL.play(player);
                    return true;
                }
                PlanetSharingChangeEvent planetEvent = new PlanetSharingChangeEvent(planet, planet.getSharing(), Planet.Sharing.PRIVATE);
                planetEvent.callEvent();
                if (planetEvent.isCancelled()) {
                    Sounds.PLAYER_FAIL.play(player);
                    return true;
                }
                Sounds.WORLD_SETTINGS_SHARING_PRIVATE.play(player);
                planet.setSharing(Planet.Sharing.PRIVATE);
                player.sendMessage(getLocaleMessage("settings.world-sharing.disabled"));
            }
            case "open" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                if (planet.getSharing() == Planet.Sharing.PUBLIC) {
                    Sounds.PLAYER_FAIL.play(player);
                    return true;
                }
                PlanetSharingChangeEvent planetEvent = new PlanetSharingChangeEvent(planet, planet.getSharing(), Planet.Sharing.PUBLIC);
                planetEvent.callEvent();
                if (planetEvent.isCancelled()) {
                    Sounds.PLAYER_FAIL.play(player);
                    return true;
                }
                Sounds.WORLD_SETTINGS_SHARING_PUBLIC.play(player);
                planet.setSharing(Planet.Sharing.PUBLIC);
                player.sendMessage(getLocaleMessage("settings.world-sharing.enabled"));
            }
            case "ban", "block", "blacklist" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return true;
                }
                if (planet.isOwner(args[1])) {
                    sender.sendMessage(getLocaleMessage("same-player"));
                    return true;
                }
                Player playerToBan = Bukkit.getPlayer(args[1]);
                if (playerToBan == null || !planet.getPlayers().contains(playerToBan)) {
                    sender.sendMessage(getLocaleMessage("menus.world-settings-players.not-in-world"));
                    return true;
                }
                if (playerToBan.hasPermission("opencreative.world.ban.bypass")) {
                    sender.sendMessage(getLocaleMessage("world.players.black-list.cannot", playerToBan));
                    return true;
                }
                sender.sendMessage(getLocaleMessage("world.players.black-list.added", playerToBan));
                planet.getWorldPlayers().banPlayer(args[1]);
            }
            case "kick" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return true;
                }
                List<Player> playersToKick = new ArrayList<>();
                if (List.of("*","@a").contains(args[1].toLowerCase())) {
                    playersToKick.addAll(planet.getPlayers());
                    playersToKick.remove(player);
                    sender.sendMessage(getLocaleMessage("world.players.kick.all"));
                } else {
                    if (planet.isOwner(args[1])) {
                        sender.sendMessage(getLocaleMessage("same-player"));
                        return true;
                    }
                    Player playerToKick = Bukkit.getPlayer(args[1]);
                    if (playerToKick == null || !planet.getPlayers().contains(playerToKick)) {
                        sender.sendMessage(getLocaleMessage("menus.world-settings-players.not-in-world"));
                        return true;
                    }
                    if (playerToKick.hasPermission("opencreative.world.kick.bypass")) {
                        sender.sendMessage(getLocaleMessage("world.players.kick.cannot", playerToKick));
                        return true;
                    }
                    playersToKick.add(playerToKick);
                }
                for (Player playerToKick : playersToKick) {
                    planet.getWorldPlayers().kickPlayer(playerToKick);
                }
            }
            case "unban", "unblacklist" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return true;
                }
                if (planet.isOwner(args[1])) {
                    sender.sendMessage(getLocaleMessage("same-player"));
                    return true;
                }
                if (!planet.getWorldPlayers().isBanned(args[1])) {
                    sender.sendMessage(getLocaleMessage("menus.world-settings-players.not-in-world"));
                    return true;
                }
                planet.getWorldPlayers().unbanPlayer(args[1]);
                sender.sendMessage(getLocaleMessage("world.players.black-list.removed",Bukkit.getOfflinePlayer(args[1])));
            }
            case "exp", "e", "experiment", "experiments" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                if (!sender.hasPermission("opencreative.world.experiments")) {
                    sender.sendMessage(getLocaleMessage("no-perms"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return true;
                }
                planet.getExperiments().handle(player, Arrays.copyOfRange(args,1,args.length));
            }
            case "size" -> {
                if (!sender.hasPermission("opencreative.world.size")) {
                    sender.sendMessage(getLocaleMessage("no-perms"));
                    return true;
                }
                long settingsSize = getFileSize(new File(getPlanetFolder(planet), "settings.yml"));
                long scriptSize = getFileSize(new File(getPlanetFolder(planet), "codeScript.yml"));
                long variablesSize = getFileSize(new File(getPlanetFolder(planet), "variables.json"));
                long dataSize = getFolderSize(new File(getPlanetFolder(planet), "playersData"));
                long folderSize = getFolderSize(getPlanetFolder(planet));
                long devWorldSize = getFolderSize(getDevPlanetFolder(planet.getDevPlanet()));
                long worldSize = folderSize-dataSize-variablesSize-scriptSize-settingsSize;
                sender.sendMessage(getLocaleMessage("world.size")
                        .replace("%total%",FileUtils.byteCountToDisplaySize(folderSize+devWorldSize))
                        .replace("%world%",FileUtils.byteCountToDisplaySize(worldSize))
                        .replace("%script%",FileUtils.byteCountToDisplaySize(scriptSize))
                        .replace("%variables%",FileUtils.byteCountToDisplaySize(variablesSize))
                        .replace("%dev%",FileUtils.byteCountToDisplaySize(devWorldSize))
                        .replace("%data%",FileUtils.byteCountToDisplaySize(dataSize))
                        .replace("%settings%",FileUtils.byteCountToDisplaySize(settingsSize)));
            }
            case "mem", "tps", "memory" -> {
                if (!sender.hasPermission("opencreative.world.memory")) {
                    sender.sendMessage(getLocaleMessage("no-perms"));
                    return true;
                }
                int chunks = planet.getTerritory().getWorld().getChunkCount()
                        + (planet.getDevPlanet().isLoaded() ? planet.getDevPlanet().getWorld().getChunkCount() : 0);
                int entities = planet.getTerritory().getWorld().getEntityCount()
                        + (planet.getDevPlanet().isLoaded() ? planet.getDevPlanet().getWorld().getEntityCount() : 0);

                sender.sendMessage("");
                sender.sendMessage(" Chunks: " + chunks);
                sender.sendMessage(" Entities: " + entities);
                sender.sendMessage("");
            }
            case "info" -> sendPlanetInfo(player,planet);
            default -> {
                if (planet.isOwner(player)) {
                    new WorldSettingsMenu(planet,player).open(player);
                } else {
                    sendPlanetInfo(player,planet);
                }
            }
        }
        return true;
    }

    private void sendPlanetInfo(CommandSender sender, Planet planet) {
        long now = System.currentTimeMillis();
        sender.sendMessage(getLocaleMessage("world.info").replace("%name%", planet.getInformation().getDisplayName())
                .replace("%id%", String.valueOf(planet.getId())).replace("%creation-time%",getElapsedTime(now, planet.getCreationTime()))
                .replace("%activity-time%",getElapsedTime(now, planet.getLastActivityTime())).replace("%online%",String.valueOf(planet.getOnline()))
                .replace("%builders%", planet.getWorldPlayers().getBuilders()).replace("%coders%", planet.getWorldPlayers().getDevelopers()).replace("%owner%", planet.getOwner())
                .replace("%sharing%", planet.getSharing().getName()).replace("%mode%", planet.getMode().getName()).replace("%description%", planet.getInformation().getDescription()));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return null;
        List<String> tabCompleter = new ArrayList<>();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) return null;
        if (!planet.isOwner(player)) return null;
        if (args.length == 1) {
            tabCompleter.addAll(List.of((planet.getSharing() == Planet.Sharing.PUBLIC ? "close" : "open"),
                    "kick","ban","unban","spawn","setspawn"));
        } else if (args.length == 2) {
            if (List.of("unban","unblacklist").contains(args[0].toLowerCase())) {
                tabCompleter.addAll(planet.getWorldPlayers().getBannedPlayers());
            } else if (List.of("ban","blacklist","kick").contains(args[0].toLowerCase())) {
                if (args[0].equalsIgnoreCase("kick")) tabCompleter.add("*");
                tabCompleter.addAll(planet.getPlayers().stream().filter(p -> !planet.isOwner(p)).map(Player::getName).toList());
            }
        }
        return tabCompleter;
    }
}
