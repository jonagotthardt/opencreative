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

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.plots.Plot;


import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class CommandAd implements CommandExecutor {

    @NotNull
    final static Plugin plugin = Main.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender,Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (args.length == 1) {
                if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                    player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
                    return true;
                }
                setCooldown(player,plugin.getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
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
                if (plot == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return true;
                }
                if (getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND) > 0) {
                    player.sendMessage(getLocaleMessage("advertisement.cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND))));
                    return true;
                }
                if (!(plot.plotSharing == Plot.Sharing.PUBLIC)) {
                    player.sendMessage(getLocaleMessage("advertisement.closed-world"));
                    return true;
                }
                setCooldown(player,plugin.getConfig().getInt("cooldowns.advertisement"), CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND);

                EventRaiser.raiseAdvertisedEvent(player);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    TextComponent advertisement = new TextComponent(getLocaleMessage("advertisement.message",player).replace("%world%",plot.getPlotName()));
                    advertisement.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getLocaleMessage("advertisement.hover"))));
                    advertisement.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ad " + player.getWorld().getName().replace("plot","").replace("dev","")));
                        p.sendMessage(advertisement);
                }
            }
        }

        return true;
    }

}
