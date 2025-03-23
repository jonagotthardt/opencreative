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

package ua.mcchickenstudio.opencreative.commands.world;

import org.bukkit.Bukkit;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.jetbrains.annotations.NotNull;


import java.util.Collections;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>CommandJoin</h1>
 * This command is used to connect player to specified planet
 * by its numeric or text ID.
 * <p>
 * Available: For all players.
 */
public class CommandJoin implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (sender instanceof Player player) {
            if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
        }

        if (OpenCreative.getSettings().isMaintenance() && !sender.hasPermission("opencreative.maintenance.bypass")) {
            sender.sendMessage(getLocaleMessage("maintenance"));
            return true;
        }

        if (args.length == 2) {
            if (!sender.hasPermission("opencreative.join.others")) {
                sender.sendMessage(getLocaleMessage("no-perms"));
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(getLocaleMessage("not-found-player"));
                return true;
            }
            sender.sendMessage(getLocaleMessage("commands.join.connecting", player).replace("%id%",args[0]));
            if (!handlePlayerConnection(player, args[0])) {
                sender.sendMessage(getLocaleMessage("commands.join.failed", player));
            }
        } else if (args.length != 1) {
            sender.sendMessage(getLocaleMessage("commands.join.help"));
            return true;
        } else if (sender instanceof Player player) {
            handlePlayerConnection(player, args[0]);
        } else {
            sender.sendMessage(getLocaleMessage("only-players"));
        }

        return true;
    }

    protected boolean handlePlayerConnection(Player player, String planetId) {
        Planet foundPlanet = findPlanet(planetId);

        if (foundPlanet == null) {
            Sounds.PLAYER_FAIL.play(player);
            player.clearTitle();
            player.sendMessage(getLocaleMessage("no-planet-found", player));
            return false;
        }

        if (foundPlanet.equals(OpenCreative.getPlanetsManager().getPlanetByPlayer(player))) {
            player.sendMessage(getLocaleMessage("same-world", player));
            return false;
        }

        foundPlanet.connectPlayer(player);
        return true;
    }

    protected Planet findPlanet(String planetId) {
        if (OpenCreative.getPlanetsManager().getPlanets().isEmpty()) return null;

        for (Planet searchablePlanet : OpenCreative.getPlanetsManager().getPlanets()) {
            if (String.valueOf(searchablePlanet.getId()).equals(planetId) ||
                    searchablePlanet.getInformation().getCustomID().equalsIgnoreCase(planetId)) {
                return searchablePlanet;
            }
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return OpenCreative.getPlanetsManager().getPlanets().stream()
                    .map(planet -> planet.getInformation().getCustomID())
                    .toList();
        }
        return Collections.emptyList();
    }
}
