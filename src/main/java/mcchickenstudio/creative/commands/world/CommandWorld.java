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

package mcchickenstudio.creative.commands.world;

import mcchickenstudio.creative.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import mcchickenstudio.creative.menu.world.WorldDeleteMobsMenu;
import mcchickenstudio.creative.menu.world.settings.WorldSettingsMenu;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.plots.Plot;
import org.jetbrains.annotations.NotNull;


import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.MessageUtils.getElapsedTime;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class CommandWorld implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            if (sender instanceof Player player) {
                if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                    player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
                    return true;
                }
                setCooldown(player,Main.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            }
            switch(args[0]) {
                case "delete":
                    if (sender instanceof Player player) {
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (sender.hasPermission("creative.delete")) {
                            if (plot.isOwner(player) || sender.hasPermission("creative.delete.bypass")) {
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
                            Main.getPlugin().getLogger().info("Deleting a world " + args[1] + ", please wait...");
                            PlotManager.getInstance().deletePlot(plot,null);
                        } else {
                            Main.getPlugin().getLogger().warning("This world doesn't exists" + args[1]);
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
                                .replace("%id%", plot.worldID).replace("%creation-time%", getElapsedTime(now, plot.getCreationTime()))
                                .replace("%activity-time%", getElapsedTime(now, plot.getLastActivityTime())).replace("%online%", String.valueOf(plot.getOnline()))
                                .replace("%builders%", plot.getBuilders()).replace("%coders%", plot.getDevelopers()).replace("%owner%", plot.getOwner())
                                .replace("%sharing%", plot.getPlotSharing().getName()).replace("%mode%", plot.getPlotMode().getName()).replace("%description%", plot.getInformation().getDescription()));
                        break;
                    }
            }
        } else {
            if (sender instanceof Player player) {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return true;
                }
                if (plot.getOwner().equalsIgnoreCase(sender.getName())) {
                    WorldSettingsMenu.openInventory(player);
                } else {
                    long now = System.currentTimeMillis();
                    sender.sendMessage(getLocaleMessage("world.info").replace("%name%", plot.getInformation().getDisplayName())
                            .replace("%id%", plot.worldID).replace("%creation-time%",getElapsedTime(now,plot.getCreationTime()))
                            .replace("%activity-time%",getElapsedTime(now,plot.getLastActivityTime())).replace("%online%",String.valueOf(plot.getOnline()))
                            .replace("%builders%",plot.getBuilders()).replace("%coders%",plot.getDevelopers()).replace("%owner%",plot.getOwner())
                            .replace("%sharing%", plot.getPlotSharing().getName()).replace("%mode%", plot.getPlotMode().getName()).replace("%description%", plot.getInformation().getDescription()));
                }
            } else {
                Main.getPlugin().getLogger().info("Worlds Commands: ");
                Main.getPlugin().getLogger().info(" Delete a world: /world delete WorldID ");
            }
        }
        return true;
    }
}
