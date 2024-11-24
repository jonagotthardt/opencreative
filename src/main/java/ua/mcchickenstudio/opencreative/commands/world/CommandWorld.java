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

package ua.mcchickenstudio.opencreative.commands.world;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menu.world.settings.WorldSettingsMenu;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.menu.world.WorldDeleteMobsMenu;
import ua.mcchickenstudio.opencreative.plots.PlotManager;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.plots.Plot;
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
                setCooldown(player, OpenCreative.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            }
            switch(args[0]) {
                case "delete":
                    if (sender instanceof Player player) {
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (sender.hasPermission("opencreative.delete")) {
                            if (plot.isOwner(player) || sender.hasPermission("opencreative.delete.bypass")) {
                                PlotManager.getInstance().deletePlot(plot, player);
                            }
                        } else {
                            sender.sendMessage(getLocaleMessage("no-perms"));
                        }
                    } else {
                        if (args.length == 1) {
                            sender.sendMessage(getLocaleMessage("too-few-args"));
                            return true;
                        }
                        Plot plot = PlotManager.getInstance().getPlotByWorldName(args[1]);
                        if (plot != null) {
                            OpenCreative.getPlugin().getLogger().info("Deleting a world " + args[1] + ", please wait...");
                            PlotManager.getInstance().deletePlot(plot,null);
                        } else {
                            OpenCreative.getPlugin().getLogger().warning("This world doesn't exists" + args[1]);
                        }
                    }
                    break;
                case "deletemobs":
                    if (sender instanceof Player player) {
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (plot != null && (plot.getOwner().equalsIgnoreCase(sender.getName()))) {
                            new WorldDeleteMobsMenu().open(player);
                        }
                    }
                    break;
                case "info":
                    if (sender instanceof Player player) {
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (plot == null) return true;
                        long now = System.currentTimeMillis();
                        sender.sendMessage(getLocaleMessage("world.info").replace("%name%", plot.getInformation().getDisplayName())
                                .replace("%id%", String.valueOf(plot.getId())).replace("%creation-time%", getElapsedTime(now, plot.getCreationTime()))
                                .replace("%activity-time%", getElapsedTime(now, plot.getLastActivityTime())).replace("%online%", String.valueOf(plot.getOnline()))
                                .replace("%builders%", plot.getWorldPlayers().getBuilders()).replace("%coders%", plot.getWorldPlayers().getDevelopers()).replace("%owner%", plot.getOwner())
                                .replace("%sharing%", plot.getSharing().getName()).replace("%mode%", plot.getMode().getName()).replace("%description%", plot.getInformation().getDescription()));
                        break;
                    }
                case "size":
                    if (sender instanceof Player player) {
                        if (!sender.hasPermission("opencreative.world.size")) return true;
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (plot == null) return true;
                        long settingsSize = getFileSize(new File(getPlotFolder(plot), "settings.yml"));
                        long scriptSize = getFileSize(new File(getPlotFolder(plot), "codeScript.yml"));
                        long variablesSize = getFileSize(new File(getPlotFolder(plot), "variables.json"));
                        long dataSize = getFolderSize(new File(getPlotFolder(plot), "playersData"));
                        long folderSize = getFolderSize(getPlotFolder(plot));
                        long devWorldSize = getFolderSize(getDevPlotFolder(plot.getDevPlot()));
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
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (plot == null) return true;
                        if (!plot.isLoaded()) return true;
                        int chunks = plot.getTerritory().getWorld().getChunkCount()
                                + (plot.getDevPlot().isLoaded() ? plot.getDevPlot().getWorld().getChunkCount() : 0);
                        int entities = plot.getTerritory().getWorld().getEntityCount()
                                + (plot.getDevPlot().isLoaded() ? plot.getDevPlot().getWorld().getEntityCount() : 0);

                        sender.sendMessage("");
                        sender.sendMessage(" Chunks: " + chunks);
                        sender.sendMessage(" Entities: " + entities);
                        sender.sendMessage("");
                    }
                    break;
                case "e", "experiment", "experiments":
                    if (sender instanceof Player player) {
                        if (!sender.hasPermission("opencreative.world.memory")) return true;
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (plot == null) return true;
                        if (args.length == 1) return true;
                        plot.getExperiments().handle(player, Arrays.copyOfRange(args,1,args.length));
                    }
                    break;
            }
        } else {
            if (sender instanceof Player player) {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return true;
                }
                if (plot.getOwner().equalsIgnoreCase(sender.getName())) {
                    new WorldSettingsMenu(plot,player).open(player);
                } else {
                    long now = System.currentTimeMillis();
                    sender.sendMessage(getLocaleMessage("world.info").replace("%name%", plot.getInformation().getDisplayName())
                            .replace("%id%", String.valueOf(plot.getId())).replace("%creation-time%",getElapsedTime(now,plot.getCreationTime()))
                            .replace("%activity-time%",getElapsedTime(now,plot.getLastActivityTime())).replace("%online%",String.valueOf(plot.getOnline()))
                            .replace("%builders%", plot.getWorldPlayers().getBuilders()).replace("%coders%", plot.getWorldPlayers().getDevelopers()).replace("%owner%",plot.getOwner())
                            .replace("%sharing%", plot.getSharing().getName()).replace("%mode%", plot.getMode().getName()).replace("%description%", plot.getInformation().getDescription()));
                }
            } else {
                OpenCreative.getPlugin().getLogger().info("Worlds Commands: ");
                OpenCreative.getPlugin().getLogger().info(" Delete a world: /world delete WorldID ");
            }
        }
        return true;
    }
}
