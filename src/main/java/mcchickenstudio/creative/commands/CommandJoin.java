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
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.utils.CooldownUtils;

import static mcchickenstudio.creative.commands.CommandAd.plugin;

import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class CommandJoin implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (Main.maintenance && !player.hasPermission("creative.maintenance.bypass")) {
                player.sendMessage(getLocaleMessage("maintenance"));
                return true;
            }
            if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            setCooldown(player,plugin.getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (args.length == 1) {
                if (!PlotManager.getInstance().getPlots().isEmpty()) {
                    Plot foundPlot = null;
                    for (Plot searchablePlot : PlotManager.getInstance().getPlots()) {
                        if (searchablePlot.worldID.equals(args[0])) {
                            foundPlot = searchablePlot;
                            break;
                        } else if (searchablePlot.getPlotCustomID().equalsIgnoreCase(args[0])) {
                            foundPlot = searchablePlot;
                            break;
                        }
                    }
                    if (foundPlot != null) {
                        foundPlot.teleportPlayer(player);
                    } else {
                        player.playSound(player.getLocation(),Sound.BLOCK_ANVIL_DESTROY,100,2);
                        player.clearTitle();
                        player.sendMessage(getLocaleMessage("no-plot-found",player));
                    }
                } else {
                    player.playSound(player.getLocation(),Sound.BLOCK_ANVIL_DESTROY,100,2);
                    player.clearTitle();
                    player.sendMessage(getLocaleMessage("no-plot-found",player));
                }
            } else {
                player.sendMessage(getLocaleMessage("join-usage"));
            }
        }
        return true;
    }
}
