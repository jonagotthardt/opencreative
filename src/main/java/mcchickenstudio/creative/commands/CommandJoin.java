/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package mcchickenstudio.creative.commands;

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
        if (sender instanceof Player) {
            if (getCooldown(((Player) sender).getPlayer(), CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                ((Player) sender).getPlayer().sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(((Player) sender).getPlayer(),CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            setCooldown(((Player) sender).getPlayer(),plugin.getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            Player player = ((Player) sender).getPlayer();
            if (args.length == 1) {
                if (!PlotManager.getInstance().getPlots().isEmpty()) {
                    Plot foundPlot = null;
                    for (Plot searchablePlot : PlotManager.getInstance().getPlots()) {
                        if (searchablePlot.worldID.equals(args[0])) {
                            foundPlot = searchablePlot;
                            break;
                        } else if (searchablePlot.plotCustomID.equalsIgnoreCase(args[0])) {
                            foundPlot = searchablePlot;
                            break;
                        }
                    }
                    if (foundPlot != null) {
                        foundPlot.teleportPlayer(player);
                    } else {
                        player.playSound(player.getLocation(),Sound.valueOf("BLOCK_ANVIL_DESTROY"),100,2);
                        player.clearTitle();
                        player.sendMessage(getLocaleMessage("no-plot-found",player));
                    }
                } else {
                    player.playSound(player.getLocation(),Sound.valueOf("BLOCK_ANVIL_DESTROY"),100,2);
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
