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

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.ClickEvent;
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
        if (args.length == 0) {
            sender.sendMessage(getLocaleMessage("locate.help"));
            return true;
        }

        String nickname = args[0];
        Player player = Bukkit.getPlayer(nickname);

        if (player == null) {
            sender.sendMessage(getLocaleMessage("locate.offline"));
            return true;
        }

        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);

        if (planet == null) {
            sender.sendMessage(getLocaleMessage("locate.offline"));
            return true;
        }

        sendLocateMessage(sender, player, planet);
        return true;
    }

    private void sendLocateMessage(CommandSender sender, Player player, Planet planet) {
        String locateMessage = parsePlanetLines(planet, parsePAPI(player,
                getLocaleMessage("locate.found").replace("%player%", player.getName())));
        String hoverText = parsePlanetLines(planet, getLocaleMessage("advertisement.hover"));
        String clickCommand = "/ad " + planet.getId();

        Component messageComponent = toComponent(locateMessage);
        Component hoverComponent = toComponent(hoverText);

        Component message = messageComponent
                .hoverEvent(HoverEvent.showText(hoverComponent))
                .clickEvent(ClickEvent.runCommand(clickCommand));

        sender.sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }
        return new ArrayList<>();
    }
}
