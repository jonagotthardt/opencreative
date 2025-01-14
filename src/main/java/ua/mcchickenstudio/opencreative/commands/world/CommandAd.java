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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.events.planet.PlanetAdvertisementEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.events.planet.PlanetInviteEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.Collections;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public class CommandAd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (OpenCreative.getSettings().isMaintenance() && !player.hasPermission("opencreative.maintenance.bypass")) {
            player.sendMessage(getLocaleMessage("maintenance"));
            return true;
        }

        switch (args.length) {
            case 0:  // /ad
                handlePlanetAdvertisement(player);
                break;
            case 1:  // /ad [planet id]
                handlePlayerConnection(player, args[0]);
                break;
            case 2:  // /ad [planet id] [player]
                handlePlanetInvitation(player, args[0], args[1]);
                break;
        }

        return true;
    }

    private void handlePlanetAdvertisement(Player player) {
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);

        if (planet == null) {
            player.sendMessage(getLocaleMessage("only-in-world"));
            return;
        }

        if (getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND) > 0) {
            player.sendMessage(getLocaleMessage("advertisement.cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND))));
            return;
        }

        if (!(planet.getSharing() == Planet.Sharing.PUBLIC)) {
            player.sendMessage(getLocaleMessage("advertisement.closed-world"));
            return;
        }

        PlanetAdvertisementEvent event = new PlanetAdvertisementEvent(planet, player);
        event.callEvent();

        if (event.isCancelled()) return;

        if (OpenCreative.getEconomy().isEnabled()) {
            double playerBalance = OpenCreative.getEconomy().getBalance(player).doubleValue();
            double advertisementPrice = OpenCreative.getSettings().getGroups().getGroup(player).getAdvertisementPrice();

            if (playerBalance < advertisementPrice) {
                player.sendMessage(getLocaleMessage("advertisement.no-money", player)
                        .replace("%money%", String.valueOf(Math.round(advertisementPrice - playerBalance))));
                return;
            } else {
                OpenCreative.getEconomy().withdrawMoney(player, advertisementPrice);
            }
        }

        setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getAdvertisementCooldown(), CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND);
        EventRaiser.raiseAdvertisedEvent(player);

        Component advertisementMessage = createAdvertisementMessage(player, planet);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(advertisementMessage);
        }
    }

    private void handlePlayerConnection(Player player, String planetId) {
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

    private void handlePlanetInvitation(Player player, String planetId, String inviteeName) {
        Player inviteReceiver = Bukkit.getPlayer(inviteeName);

        if (inviteReceiver == null) {
            player.sendMessage(getLocaleMessage("no-player-found", player));
            return;
        }

        if (player.equals(inviteReceiver)) {
            player.sendMessage(getLocaleMessage("same-world", player));
            return;
        }

        Planet foundPlanet = findPlanet(planetId);

        if (foundPlanet == null) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 100, 2);
            player.clearTitle();
            player.sendMessage(getLocaleMessage("no-planet-found", player));
            return;
        }

        if (foundPlanet.getSharing() != Planet.Sharing.PUBLIC) {
            player.sendMessage(getLocaleMessage("advertisement.closed-world"));
            return;
        }

        if (getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND) > 0) {
            player.sendMessage(getLocaleMessage("advertisement.cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND))));
            return;
        }

        PlanetInviteEvent event = new PlanetInviteEvent(foundPlanet, player, inviteReceiver);
        event.callEvent();

        if (event.isCancelled()) return;

        setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getAdvertisementCooldown(), CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND);

        Component advertisementMessage = createAdvertisementMessage(player, foundPlanet);
        inviteReceiver.sendMessage(advertisementMessage);
        player.sendMessage(advertisementMessage);
    }

    private Component createAdvertisementMessage(Player player, Planet planet) {
        String advertisementText = getLocaleMessage("advertisement.message", player)
                .replace("%world%", planet.getInformation().getDisplayName());
        String hoverText = parsePlanetLines(planet, getLocaleMessage("advertisement.hover"));
        String clickCommand = "/ad " + planet.getId();

        Component advertisement = toComponent(advertisementText);
        Component hoverComponent = toComponent(hoverText);

        advertisement = advertisement
                .hoverEvent(HoverEvent.showText(hoverComponent))
                .clickEvent(ClickEvent.runCommand(clickCommand));

        return advertisement;
    }

    private Planet findPlanet(String planetId) {
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
        return switch (args.length) {
            case 1 -> PlanetManager.getInstance().getPlanets().stream()
                    .map(planet -> planet.getInformation().getCustomID())
                    .toList();
            case 2 -> Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
            default -> Collections.emptyList();
        };
    }
}

