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


import java.io.File;
import java.util.Arrays;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getElapsedTime;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class CommandWorld implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            if (sender instanceof Player player) {
                if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                    player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
                    return true;
                }
                setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
            }
            switch(args[0]) {
                case "delete":
                    if (sender instanceof Player player) {
                        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
                        if (sender.hasPermission("opencreative.delete")) {
                            if (planet.isOwner(player) || sender.hasPermission("opencreative.delete.bypass")) {
                                PlanetManager.getInstance().deletePlanet(planet, player);
                            }
                        } else {
                            sender.sendMessage(getLocaleMessage("no-perms"));
                        }
                    } else {
                        if (args.length == 1) {
                            sender.sendMessage(getLocaleMessage("too-few-args"));
                            return true;
                        }
                        Planet planet = PlanetManager.getInstance().getPlanetByWorldName(args[1]);
                        if (planet != null) {
                            OpenCreative.getPlugin().getLogger().info("Deleting a world " + args[1] + ", please wait...");
                            PlanetManager.getInstance().deletePlanet(planet,sender);
                        } else {
                            OpenCreative.getPlugin().getLogger().warning("This world doesn't exists" + args[1]);
                        }
                    }
                    break;
                case "deletemobs", "mobs", "entities":
                    if (sender instanceof Player player) {
                        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
                        if (planet != null && planet.getWorldPlayers().canBuild(player)) {
                            new EntitiesBrowserMenu(player,planet).open(player);
                        }
                    }
                    break;
                case "info":
                    if (sender instanceof Player player) {
                        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
                        if (planet == null) return true;
                        long now = System.currentTimeMillis();
                        sender.sendMessage(getLocaleMessage("world.info").replace("%name%", planet.getInformation().getDisplayName())
                                .replace("%id%", String.valueOf(planet.getId())).replace("%creation-time%", getElapsedTime(now, planet.getCreationTime()))
                                .replace("%activity-time%", getElapsedTime(now, planet.getLastActivityTime())).replace("%online%", String.valueOf(planet.getOnline()))
                                .replace("%builders%", planet.getWorldPlayers().getBuilders()).replace("%coders%", planet.getWorldPlayers().getDevelopers()).replace("%owner%", planet.getOwner())
                                .replace("%sharing%", planet.getSharing().getName()).replace("%mode%", planet.getMode().getName()).replace("%description%", planet.getInformation().getDescription()));
                        break;
                    }
                case "size":
                    if (sender instanceof Player player) {
                        if (!sender.hasPermission("opencreative.world.size")) return true;
                        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
                        if (planet == null) return true;
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
                    break;
                case "ram", "mem", "tps", "memory":
                    if (sender instanceof Player player) {
                        if (!sender.hasPermission("opencreative.world.memory")) return true;
                        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
                        if (planet == null) return true;
                        if (!planet.isLoaded()) return true;
                        int chunks = planet.getTerritory().getWorld().getChunkCount()
                                + (planet.getDevPlanet().isLoaded() ? planet.getDevPlanet().getWorld().getChunkCount() : 0);
                        int entities = planet.getTerritory().getWorld().getEntityCount()
                                + (planet.getDevPlanet().isLoaded() ? planet.getDevPlanet().getWorld().getEntityCount() : 0);

                        sender.sendMessage("");
                        sender.sendMessage(" Chunks: " + chunks);
                        sender.sendMessage(" Entities: " + entities);
                        sender.sendMessage("");
                    }
                    break;
                case "e", "experiment", "experiments":
                    if (sender instanceof Player player) {
                        if (!sender.hasPermission("opencreative.world.memory")) return true;
                        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
                        if (planet == null) return true;
                        if (args.length == 1) return true;
                        planet.getExperiments().handle(player, Arrays.copyOfRange(args,1,args.length));
                    }
                    break;
            }
        } else {
            if (sender instanceof Player player) {
                Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
                if (planet == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return true;
                }
                if (planet.getOwner().equalsIgnoreCase(sender.getName())) {
                    new WorldSettingsMenu(planet,player).open(player);
                } else {
                    long now = System.currentTimeMillis();
                    sender.sendMessage(getLocaleMessage("world.info").replace("%name%", planet.getInformation().getDisplayName())
                            .replace("%id%", String.valueOf(planet.getId())).replace("%creation-time%",getElapsedTime(now, planet.getCreationTime()))
                            .replace("%activity-time%",getElapsedTime(now, planet.getLastActivityTime())).replace("%online%",String.valueOf(planet.getOnline()))
                            .replace("%builders%", planet.getWorldPlayers().getBuilders()).replace("%coders%", planet.getWorldPlayers().getDevelopers()).replace("%owner%", planet.getOwner())
                            .replace("%sharing%", planet.getSharing().getName()).replace("%mode%", planet.getMode().getName()).replace("%description%", planet.getInformation().getDescription()));
                }
            } else {
                OpenCreative.getPlugin().getLogger().info("Worlds Commands: ");
                OpenCreative.getPlugin().getLogger().info(" Delete a world: /world delete WorldID ");
            }
        }
        return true;
    }
}
