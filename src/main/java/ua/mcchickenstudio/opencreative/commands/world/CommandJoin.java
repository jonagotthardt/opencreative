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

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.jetbrains.annotations.NotNull;


import java.util.Collections;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class CommandJoin implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (OpenCreative.getSettings().isMaintenance() && !player.hasPermission("opencreative.maintenance.bypass")) {
            player.sendMessage(getLocaleMessage("maintenance"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(getLocaleMessage("join-usage"));
            return true;
        }

        handlePlayerConnection(player, args[0]);

        return true;
    }

    protected void handlePlayerConnection(Player player, String planetId) {
        if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
            player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND))));
            return;
        }
        setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);

        Planet foundPlanet = findPlanet(planetId);

        if (foundPlanet == null) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 100, 2);
            player.clearTitle();
            player.sendMessage(getLocaleMessage("no-planet-found", player));
            return;
        }

        if (foundPlanet.equals(PlanetManager.getInstance().getPlanetByPlayer(player))) {
            player.sendMessage(getLocaleMessage("same-world", player));
            return;
        }

        foundPlanet.connectPlayer(player);
    }

    protected Planet findPlanet(String planetId) {
        if (PlanetManager.getInstance().getPlanets().isEmpty()) return null;

        for (Planet searchablePlanet : PlanetManager.getInstance().getPlanets()) {
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
            return PlanetManager.getInstance().getPlanets().stream()
                    .map(planet -> planet.getInformation().getCustomID())
                    .toList();
        }
        return Collections.emptyList();
    }
}
