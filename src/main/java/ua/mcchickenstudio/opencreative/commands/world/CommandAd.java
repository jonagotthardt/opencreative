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
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.events.plot.PlotAdvertisementEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.plots.PlotManager;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.plots.Plot;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.parsePlotLines;

public class CommandAd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (OpenCreative.maintenance && !player.hasPermission("opencreative.maintenance.bypass")) {
                player.sendMessage(getLocaleMessage("maintenance"));
                return true;
            }
            if (args.length == 1) {
                if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                    player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
                    return true;
                }
                setCooldown(player, OpenCreative.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
                if (!PlotManager.getInstance().getPlots().isEmpty()) {
                    Plot foundPlot = null;
                    for (Plot searchablePlot : PlotManager.getInstance().getPlots()) {
                        if (String.valueOf(searchablePlot.getId()).equals(args[0])) {
                            foundPlot = searchablePlot;
                            break;
                        } else if (searchablePlot.getInformation().getCustomID().equalsIgnoreCase(args[0])) {
                            foundPlot = searchablePlot;
                            break;
                        }
                    }
                    if (foundPlot != null) {
                        if (foundPlot.equals(PlotManager.getInstance().getPlotByPlayer(player))) {
                            player.sendMessage(getLocaleMessage("same-world",player));
                        } else {
                            foundPlot.connectPlayer(player);
                        }
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
                if (plot == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return true;
                }
                if (getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND) > 0) {
                    player.sendMessage(getLocaleMessage("advertisement.cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND))));
                    return true;
                }
                if (!(plot.getSharing() == Plot.Sharing.PUBLIC)) {
                    player.sendMessage(getLocaleMessage("advertisement.closed-world"));
                    return true;
                }
                PlotAdvertisementEvent event = new PlotAdvertisementEvent(plot,player);
                event.callEvent();
                if (event.isCancelled()) return true;
                setCooldown(player, OpenCreative.getPlugin().getConfig().getInt("cooldowns.advertisement"), CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND);
                EventRaiser.raiseAdvertisedEvent(player);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    TextComponent advertisement = new TextComponent(getLocaleMessage("advertisement.message",player).replace("%world%",plot.getInformation().getDisplayName()));
                    advertisement.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(parsePlotLines(plot,getLocaleMessage("advertisement.hover")))));
                    advertisement.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ad " + plot.getId()));
                        p.sendMessage(advertisement);
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> TabCompleter = new ArrayList<>();
            for (Plot plot : PlotManager.getInstance().getPlots()) {
                TabCompleter.add(plot.getInformation().getCustomID());
            }
            return TabCompleter;
        }
        return null;
    }

}
