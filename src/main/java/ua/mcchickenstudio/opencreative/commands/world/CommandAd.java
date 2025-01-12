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
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.events.planet.PlanetAdvertisementEvent;
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
import ua.mcchickenstudio.opencreative.events.planet.PlanetInviteEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.parsePlanetLines;

public class CommandAd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
            if (OpenCreative.getSettings().isMaintenance() && !player.hasPermission("opencreative.maintenance.bypass")) {
                player.sendMessage(getLocaleMessage("maintenance"));
                return true;
            }
            if (args.length >= 1) {
                if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                    player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
                    return true;
                }
                setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
                if (!PlanetManager.getInstance().getPlanets().isEmpty()) {
                    Planet foundPlanet = null;
                    for (Planet searchablePlanet : PlanetManager.getInstance().getPlanets()) {
                        if (String.valueOf(searchablePlanet.getId()).equals(args[0])) {
                            foundPlanet = searchablePlanet;
                            break;
                        } else if (searchablePlanet.getInformation().getCustomID().equalsIgnoreCase(args[0])) {
                            foundPlanet = searchablePlanet;
                            break;
                        }
                    }
                    if (foundPlanet != null) {
                        if (args.length == 2) {
                            Player inviteReceiver = Bukkit.getPlayer(args[1]);
                            if (inviteReceiver == null) {
                                player.sendMessage(getLocaleMessage("no-player-found",player));
                                return true;
                            }
                            if (player.equals(inviteReceiver)) {
                                player.sendMessage(getLocaleMessage("same-world",player));
                                return true;
                            }
                            if (foundPlanet.getSharing() != Planet.Sharing.PUBLIC) {
                                player.sendMessage(getLocaleMessage("advertisement.closed-world"));
                                return true;
                            }
                            if (getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND) > 0) {
                                player.sendMessage(getLocaleMessage("advertisement.cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND))));
                                return true;
                            }
                            PlanetInviteEvent event = new PlanetInviteEvent(foundPlanet,player,inviteReceiver);
                            event.callEvent();
                            if (event.isCancelled()) return true;
                            setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getAdvertisementCooldown(), CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND);
                            TextComponent advertisement = new TextComponent(getLocaleMessage("advertisement.message",player).replace("%world%", foundPlanet.getInformation().getDisplayName()));
                            advertisement.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(parsePlanetLines(foundPlanet,getLocaleMessage("advertisement.hover")))));
                            advertisement.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ad " + foundPlanet.getId()));
                            inviteReceiver.sendMessage(advertisement);
                            player.sendMessage(advertisement);
                        } else if (foundPlanet.equals(PlanetManager.getInstance().getPlanetByPlayer(player))) {
                            player.sendMessage(getLocaleMessage("same-world",player));
                        } else {
                            foundPlanet.connectPlayer(player);
                        }
                    } else {
                        player.playSound(player.getLocation(),Sound.BLOCK_ANVIL_DESTROY,100,2);
                        player.clearTitle();
                        player.sendMessage(getLocaleMessage("no-planet-found",player));
                    }
                } else {
                    player.playSound(player.getLocation(),Sound.BLOCK_ANVIL_DESTROY,100,2);
                    player.clearTitle();
                    player.sendMessage(getLocaleMessage("no-planet-found",player));
                }
            } else {
                if (planet == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return true;
                }
                if (getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND) > 0) {
                    player.sendMessage(getLocaleMessage("advertisement.cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND))));
                    return true;
                }
                if (!(planet.getSharing() == Planet.Sharing.PUBLIC)) {
                    player.sendMessage(getLocaleMessage("advertisement.closed-world"));
                    return true;
                }
                PlanetAdvertisementEvent event = new PlanetAdvertisementEvent(planet,player);
                event.callEvent();
                if (event.isCancelled()) return true;
                setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getAdvertisementCooldown(), CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND);
                if (OpenCreative.getEconomy().isEnabled()) {
                    double playerBalance = OpenCreative.getEconomy().getBalance(player).doubleValue();
                    double advertisementPrice = OpenCreative.getSettings().getGroups().getGroup(player).getAdvertisementPrice();
                    if (playerBalance < advertisementPrice) {
                        player.sendMessage(getLocaleMessage("advertisement.no-money",player).replace("%money%",String.valueOf(Math.round(advertisementPrice-playerBalance))));
                        return true;
                    } else {
                        OpenCreative.getEconomy().withdrawMoney(player,advertisementPrice);
                    }
                }
                EventRaiser.raiseAdvertisedEvent(player);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    TextComponent advertisement = new TextComponent(getLocaleMessage("advertisement.message",player).replace("%world%", planet.getInformation().getDisplayName()));
                    advertisement.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(parsePlanetLines(planet,getLocaleMessage("advertisement.hover")))));
                    advertisement.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ad " + planet.getId()));
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
            for (Planet planet : PlanetManager.getInstance().getPlanets()) {
                TabCompleter.add(planet.getInformation().getCustomID());
            }
            return TabCompleter;
        }
        return null;
    }

}
