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

package ua.mcchickenstudio.opencreative.commands;

import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public class CommandLocate implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            String nickname = args[0];
            Player player = Bukkit.getPlayer(nickname);
            if (player != null) {
                Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
                if (planet != null) {
                    String message = parsePlanetLines(planet,parsePAPI(player,getLocaleMessage("locate.found").replace("%player%",player.getName())));
                    TextComponent advertisement = new TextComponent(message);
                    advertisement.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(parsePlanetLines(planet,getLocaleMessage("advertisement.hover")))));
                    advertisement.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ad " + planet.getId()));
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

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }
        return new ArrayList<>();
    }
}
