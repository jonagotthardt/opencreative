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

import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menu.world.settings.EntitiesBrowserMenu;
import ua.mcchickenstudio.opencreative.menu.world.settings.WorldSettingsMenu;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getElapsedTime;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class CommandWorld implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use command");
            return true;
        }
        if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
            player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
            return true;
        }
        setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
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
                if (planet.isOwner(args[1])) {
                    sender.sendMessage(getLocaleMessage("same-player"));
                    return true;
                }
                Player playerToKick = Bukkit.getPlayer(args[1]);
                if (playerToKick == null || !planet.getPlayers().contains(playerToKick)) {
                    sender.sendMessage(getLocaleMessage("menus.world-settings-players.not-in-world"));
                    return true;
                }
                planet.getWorldPlayers().kickPlayer(playerToKick);
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
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
        if (planet == null) return null;
        if (!planet.isOwner(player)) return null;
        if (args.length == 1) {
            if (List.of("unban","unblacklist").contains(args[0].toLowerCase())) {
                tabCompleter.addAll(planet.getWorldPlayers().getBannedPlayers());
            } else if (List.of("ban","blacklist","kick").contains(args[0].toLowerCase())) {
                tabCompleter.addAll(planet.getPlayers().stream().filter(p -> !planet.isOwner(p)).map(Player::getName).toList());
            }
        }
        return tabCompleter;
    }
}
