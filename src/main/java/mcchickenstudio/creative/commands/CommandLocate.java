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

import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static mcchickenstudio.creative.utils.MessageUtils.*;

public class CommandLocate implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command,  String label, String[] args) {
        if (args.length > 0) {
            String nickname = args[0];
            Player player = Bukkit.getPlayer(nickname);
            if (player != null) {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) {
                    String message = parsePlotLines(plot,parsePAPI(player,getLocaleMessage("locate.found").replace("%player%",player.getName())));
                    TextComponent advertisement = new TextComponent(message);
                    advertisement.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(parsePlotLines(plot,getLocaleMessage("advertisement.hover")))));
                    advertisement.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ad " + plot.worldID));
                    sender.sendMessage(advertisement);
                } else {
                    sender.sendMessage(getLocaleMessage("locate.offline"));
                }
            } else {
                sender.sendMessage(getLocaleMessage("locate.offline"));
            }
        } else {
            sender.sendMessage(getLocaleMessage("locate.help"));
        }
        return true;
    }
}
