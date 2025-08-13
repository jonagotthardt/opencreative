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
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.events.planet.PlanetSharingChangeEvent;
import ua.mcchickenstudio.opencreative.menus.world.settings.EntitiesBrowserMenu;
import ua.mcchickenstudio.opencreative.menus.world.settings.WorldSettingsMenu;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.utils.world.platforms.DevPlatformer;
import ua.mcchickenstudio.opencreative.utils.world.platforms.DevPlatformers;


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

/**
 * <h1>WorldCommand</h1>
 * This command allows world owner to change world's settings.
 * <p>
 * Available: For world owners; some subcommands for all players in world.
 */
public class WorldCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return;
        }
        if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
            player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
            return;
        }
        setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) {
            player.sendMessage(getLocaleMessage("only-in-world"));
            return;
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
                    return;
                }
                if (isEntityInDevPlanet(player)) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
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
                    return;
                }
                if (isEntityInDevPlanet(player)) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                player.teleport(player.getWorld().getSpawnLocation());
                Sounds.WORLD_SETTINGS_SPAWN_TELEPORT.play(player);
            }
            case "close" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return;
                }
                if (planet.getSharing() != Planet.Sharing.PUBLIC) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                PlanetSharingChangeEvent planetEvent = new PlanetSharingChangeEvent(planet, planet.getSharing(), Planet.Sharing.PRIVATE);
                planetEvent.callEvent();
                if (planetEvent.isCancelled()) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                Sounds.WORLD_SETTINGS_SHARING_PRIVATE.play(player);
                planet.setSharing(Planet.Sharing.PRIVATE);
                player.sendMessage(getLocaleMessage("settings.world-sharing.disabled"));
            }
            case "open" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return;
                }
                if (planet.getSharing() == Planet.Sharing.PUBLIC) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                PlanetSharingChangeEvent planetEvent = new PlanetSharingChangeEvent(planet, planet.getSharing(), Planet.Sharing.PUBLIC);
                planetEvent.callEvent();
                if (planetEvent.isCancelled()) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                Sounds.WORLD_SETTINGS_SHARING_PUBLIC.play(player);
                planet.setSharing(Planet.Sharing.PUBLIC);
                player.sendMessage(getLocaleMessage("settings.world-sharing.enabled"));
            }
            case "whitelist", "white" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return;
                }
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                if (planet.isOwner(args[1])) {
                    sender.sendMessage(getLocaleMessage("same-player"));
                    return;
                }
                int limit = planet.getLimits().getWhitelistedLimit();
                if (planet.getWorldPlayers().getWhitelistedPlayers().size() > limit) {
                    sender.sendMessage(getLocaleMessage("world.players.white-list.limit").replace("%limit%",String.valueOf(limit)));
                    return;
                }
                Player playerToWhitelist = Bukkit.getPlayer(args[1]);
                if (playerToWhitelist == null || !planet.getPlayers().contains(playerToWhitelist)) {
                    sender.sendMessage(getLocaleMessage("menus.world-settings-players.not-in-world"));
                    return;
                }
                sender.sendMessage(getPlayerLocaleMessage("world.players.white-list.added", playerToWhitelist));
                planet.getWorldPlayers().banPlayer(args[1]);
            }
            case "ban", "block", "blacklist" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return;
                }
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                if (planet.isOwner(args[1])) {
                    sender.sendMessage(getLocaleMessage("same-player"));
                    return;
                }
                int limit = planet.getLimits().getBlacklistedLimit();
                if (planet.getWorldPlayers().getBannedPlayers().size() > limit) {
                    sender.sendMessage(getLocaleMessage("world.players.black-list.limit").replace("%limit%",String.valueOf(limit)));
                    return;
                }
                Player playerToBan = Bukkit.getPlayer(args[1]);
                if (playerToBan == null || !planet.getPlayers().contains(playerToBan)) {
                    sender.sendMessage(getLocaleMessage("menus.world-settings-players.not-in-world"));
                    return;
                }
                if (playerToBan.hasPermission("opencreative.world.ban.bypass")) {
                    sender.sendMessage(getPlayerLocaleMessage("world.players.black-list.cannot", playerToBan));
                    return;
                }
                sender.sendMessage(getPlayerLocaleMessage("world.players.black-list.added", playerToBan));
                planet.getWorldPlayers().banPlayer(args[1]);
            }
            case "kick" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return;
                }
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                List<Player> playersToKick = new ArrayList<>();
                if (List.of("*","@a").contains(args[1].toLowerCase())) {
                    playersToKick.addAll(planet.getPlayers());
                    playersToKick.remove(player);
                    sender.sendMessage(getLocaleMessage("world.players.kick.all"));
                } else {
                    if (planet.isOwner(args[1])) {
                        sender.sendMessage(getLocaleMessage("same-player"));
                        return;
                    }
                    Player playerToKick = Bukkit.getPlayer(args[1]);
                    if (playerToKick == null || !planet.getPlayers().contains(playerToKick)) {
                        sender.sendMessage(getLocaleMessage("menus.world-settings-players.not-in-world"));
                        return;
                    }
                    if (playerToKick.hasPermission("opencreative.world.kick.bypass")) {
                        sender.sendMessage(getPlayerLocaleMessage("world.players.kick.cannot", playerToKick));
                        return;
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
                    return;
                }
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                if (planet.isOwner(args[1])) {
                    sender.sendMessage(getLocaleMessage("same-player"));
                    return;
                }
                if (!planet.getWorldPlayers().isBanned(args[1])) {
                    sender.sendMessage(getLocaleMessage("menus.world-settings-players.not-in-world"));
                    return;
                }
                planet.getWorldPlayers().unbanPlayer(args[1]);
                sender.sendMessage(getPlayerLocaleMessage("world.players.black-list.removed",Bukkit.getOfflinePlayer(args[1])));
            }
            case "unwhitelist", "unwhite" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return;
                }
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                if (planet.isOwner(args[1])) {
                    sender.sendMessage(getLocaleMessage("same-player"));
                    return;
                }
                if (!planet.getWorldPlayers().isWhitelisted(args[1])) {
                    sender.sendMessage(getLocaleMessage("menus.world-settings-players.not-in-world"));
                    return;
                }
                planet.getWorldPlayers().removeFromWhitelist(args[1]);
                sender.sendMessage(getPlayerLocaleMessage("world.players.white-list.removed",Bukkit.getOfflinePlayer(args[1])));
            }
            case "exp", "e", "experiment", "experiments" -> {
                if (!planet.isOwner(player)) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return;
                }
                if (!sender.hasPermission("opencreative.world.experiments")) {
                    sender.sendMessage(getLocaleMessage("no-perms"));
                    return;
                }
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                planet.getExperiments().handle(player, Arrays.copyOfRange(args,1,args.length));
            }
            case "size" -> {
                if (!sender.hasPermission("opencreative.world.size")) {
                    sender.sendMessage(getLocaleMessage("no-perms"));
                    return;
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
                    return;
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
            case "platformer" -> {
                if (!sender.hasPermission("opencreative.world.set-platformer")) {
                    sender.sendMessage(getLocaleMessage("no-perms"));
                    return;
                }
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                String platformerID = args[1].toLowerCase();
                DevPlatformer platformer = DevPlatformers.getInstance().getById(platformerID);
                if (platformerID.equals("reset") || platformerID.equals("default")) {
                    platformer = OpenCreative.getDevPlatformer();
                    planet.getDevPlanet().setPlatformerID(platformer.getID());
                    sender.sendMessage(getLocaleMessage("world.platformer.reset")
                            .replace("%id%", platformer.getID()));
                    if (planet.getDevPlanet().isLoaded()) {
                        platformer.setWorldBorder(planet.getDevPlanet());
                        planet.getDevPlanet().displayWorldBorders();
                    }
                    return;
                }
                if (platformer == null) {
                    sender.sendMessage(getLocaleMessage("world.platformer.not-found")
                            .replace("%id%", platformerID));
                    return;
                }
                planet.getDevPlanet().setPlatformerID(platformer.getID());
                sender.sendMessage(getLocaleMessage("world.platformer.set")
                        .replace("%id%", platformer.getID()));
                if (planet.getDevPlanet().isLoaded()) {
                    platformer.setWorldBorder(planet.getDevPlanet());
                    planet.getDevPlanet().displayWorldBorders();
                }
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
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return null;
        List<String> tabCompleter = new ArrayList<>();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) return null;
        if (!planet.isOwner(player)) return null;
        if (args.length == 1) {
            tabCompleter.addAll(List.of((planet.getSharing() == Planet.Sharing.PUBLIC ? "close" : "open"),
                    "kick","ban","unban","spawn","setspawn","whitelist","unwhitelist"));
        } else if (args.length == 2) {
            if (List.of("unban","unblacklist").contains(args[0].toLowerCase())) {
                tabCompleter.addAll(planet.getWorldPlayers().getBannedPlayers().stream().filter(p -> p.startsWith(args[1])).toList());
            } else if (List.of("unwhite","unwhitelist").contains(args[0].toLowerCase())) {
                tabCompleter.addAll(planet.getWorldPlayers().getWhitelistedPlayers().stream().filter(p -> p.startsWith(args[1])).toList());
            } else if (List.of("ban","blacklist","kick","whitelist","white").contains(args[0].toLowerCase())) {
                if (args[0].equalsIgnoreCase("kick")) tabCompleter.add("*");
                tabCompleter.addAll(planet.getPlayers().stream().filter(p -> !planet.isOwner(p) && p.getName().startsWith(args[1])).map(Player::getName).toList());
            } else if (args[0].equalsIgnoreCase("platformer") && player.hasPermission("opencreative.world.set-platformer")) {
                tabCompleter.addAll(DevPlatformers.getInstance().getPlatformersIDs());
            }
        }
        return tabCompleter;
    }
}
