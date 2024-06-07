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

import mcchickenstudio.creative.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import mcchickenstudio.creative.menu.WorldDeleteMobsMenu;
import mcchickenstudio.creative.menu.WorldSettingsMenu;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.plots.Plot;

import static mcchickenstudio.creative.commands.CommandAd.plugin;
import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.MessageUtils.getElapsedTime;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class CommandWorld implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (sender instanceof Player) {
                if (getCooldown(((Player) sender).getPlayer(), CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                    ((Player) sender).getPlayer().sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(((Player) sender).getPlayer(),CooldownUtils.CooldownType.GENERIC_COMMAND))));
                    return true;
                }
                setCooldown(((Player) sender).getPlayer(),plugin.getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            }
            // Delete
            switch(args[0]) {
                case "delete":
                    // Игрок
                    if (sender instanceof Player) {
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(((Player) sender).getPlayer());
                        if (sender.hasPermission("creative.delete")) {
                            if (plot.getOwner().equalsIgnoreCase(sender.getName())) {
                                PlotManager.getInstance().deletePlot(plot, ((Player) sender).getPlayer());
                            } else {
                                // Обход на удаление для тех, у кого есть право
                                if (sender.hasPermission("creative.deletebypass")) PlotManager.getInstance().deletePlot(plot, ((Player) sender).getPlayer());
                            }
                        } else {
                            sender.sendMessage(getLocaleMessage("no-perms"));
                        }
                    // Консоль
                    } else {
                        if (args.length == 1) return true;
                        Main.getPlugin().getLogger().info("Удаляем мир: " + args[1]);
                        if (Bukkit.getWorld(args[1]) != null) {
                            PlotManager.getInstance().deletePlot(PlotManager.getInstance().getPlotByWorld(Bukkit.getWorld(args[1])),null);
                        } else {
                            Main.getPlugin().getLogger().warning("Такой мир не существует " + args[1]);
                        }
                    }
                    break;
                case "deletemobs":
                    if (sender instanceof Player) {
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(((Player) sender).getPlayer());
                        if (plot != null && (plot.getOwner().equalsIgnoreCase(sender.getName()))) {
                            new WorldDeleteMobsMenu().open(((Player) sender).getPlayer());
                        }
                    }
                    break;
                case "info":
                    Plot plot = PlotManager.getInstance().getPlotByPlayer(((Player) sender).getPlayer());
                    if (plot == null) return true;
                    long now = System.currentTimeMillis();
                    sender.sendMessage(getLocaleMessage("world.info").replace("%name%",plot.plotName)
                            .replace("%id%", plot.worldID).replace("%creation-time%",getElapsedTime(now,plot.getCreationTime()))
                            .replace("%activity-time%",getElapsedTime(now,plot.getLastActivityTime())).replace("%online%",String.valueOf(plot.getOnline()))
                            .replace("%builders%",plot.getBuilders()).replace("%coders%",plot.getDevelopers()).replace("%owner%",plot.getOwner())
                            .replace("%sharing%",plot.plotSharing.getName()).replace("%mode%",plot.plotMode.getName()).replace("%description%", plot.plotDescription));
                    break;
            }
        } else {
            if (sender instanceof Player) {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(((Player) sender).getPlayer());
                if (plot == null) {
                    ((Player) sender).getPlayer().sendMessage(getLocaleMessage("only-in-world"));
                    return true;
                }
                if (plot.getOwner().equalsIgnoreCase(sender.getName())) {
                    WorldSettingsMenu.openInventory(((Player) sender).getPlayer());
                } else {
                    long now = System.currentTimeMillis();
                    sender.sendMessage(getLocaleMessage("world.info").replace("%name%",plot.plotName)
                            .replace("%id%", plot.worldID).replace("%creation-time%",getElapsedTime(now,plot.getCreationTime()))
                            .replace("%activity-time%",getElapsedTime(now,plot.getLastActivityTime())).replace("%online%",String.valueOf(plot.getOnline()))
                            .replace("%builders%",plot.getBuilders()).replace("%coders%",plot.getDevelopers()).replace("%owner%",plot.getOwner())
                            .replace("%sharing%",plot.plotSharing.getName()).replace("%mode%",plot.plotMode.getName()).replace("%description%", plot.plotDescription));
                }
            } else {
                Main.getPlugin().getLogger().info("Управление мирами: ");
                Main.getPlugin().getLogger().info(" Удалить мир: /world delete НАЗВАНИЕМИРА ");
            }
        }
        return true;
    }
}
