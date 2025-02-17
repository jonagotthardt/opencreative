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

package ua.mcchickenstudio.opencreative.commands.world.modes;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.events.planet.PlanetModeChangeEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld.removePlayerWithLocation;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;


import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;

public class CommandBuild implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            if (planet == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
            if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (args.length == 0) {
                removePlayerWithLocation(player);
                if (planet.getMode() != Planet.Mode.BUILD) {
                    if (planet.getWorldPlayers().canBuild(player)) {
                        Player planetOwner = Bukkit.getPlayer(planet.getOwner());
                        if (!planet.getWorldPlayers().isTrustedBuilder(player)) {
                            if (planetOwner == null) {
                                sender.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                                return true;
                            }
                            Planet ownerPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(planetOwner);
                            if (!(ownerPlanet == planet)) {
                                sender.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                                return true;
                            }
                        }
                        PlanetModeChangeEvent event = new PlanetModeChangeEvent(planet, planet.getMode(), Planet.Mode.BUILD,player);
                        event.callEvent();
                        if (event.isCancelled()) {
                            return true;
                        }
                        planet.setMode(Planet.Mode.BUILD);
                        if (isEntityInDevPlanet(player)) {
                            clearPlayer(player);
                            player.teleport(planet.getTerritory().getWorld().getSpawnLocation());
                            if (planet.isOwner(sender.getName())) {
                                player.getInventory().setItem(8,createItem(Material.COMPASS,1,"items.developer.world-settings"));
                            }
                            planet.getTerritory().showBorders(player);
                            giveBuildPermissions(player);
                            player.setGameMode(GameMode.CREATIVE);
                        }
                    } else {
                        sender.sendMessage(getLocaleMessage("not-owner"));
                    }
                } else {
                    clearPlayer(player);
                    player.showTitle(Title.title(
                            toComponent(getLocaleMessage("world.build-mode.title")), toComponent(getLocaleMessage("world.build-mode.subtitle")),
                            Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
                    ));
                    player.teleport(planet.getTerritory().getWorld().getSpawnLocation());
                    Sounds.WORLD_MODE_BUILD.play(player);
                    if (planet.getWorldPlayers().canBuild(player)) {
                        Player planetOwner = Bukkit.getPlayer(planet.getOwner());
                        if (planet.getWorldPlayers().getBuildersNotTrusted().contains(sender.getName())) {
                            if (planetOwner == null) {
                                sender.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                                return true;
                            }
                            Planet ownerPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(planetOwner);
                            if (!(ownerPlanet == planet)) {
                                sender.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                                return true;
                            }
                        }
                        if (planet.isOwner(sender.getName())) {
                            ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
                            player.getInventory().setItem(8,worldSettingsItem);
                        }
                        player.setGameMode(GameMode.CREATIVE);
                        planet.getTerritory().showBorders(player);
                        giveBuildPermissions(player);
                        sender.sendMessage(getLocaleMessage("world.build-mode.message.owner"));
                        if (!planet.getTerritory().isAutoSave()) {
                            player.sendMessage(getLocaleMessage("settings.autosave.warning"));
                        }
                    } else {
                        sender.sendMessage(getLocaleMessage("world.build-mode.message.players"));
                    }
                }
            } else {
                if (!planet.isOwner(sender.getName())) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                String nickname = args[0];
                Player onlinePlayer = Bukkit.getPlayer(nickname);
                if (!planet.getWorldPlayers().getAllBuilders().contains(nickname)) {
                    if (onlinePlayer != null) {
                        nickname = onlinePlayer.getName();
                    }
                }
                if (planet.isOwner(nickname)) {
                    sender.sendMessage(getLocaleMessage("same-player"));
                    return true;
                }
                /*
                 * Checks if player's name contains in not trusted
                 * or trusted builders.
                 */
                if (planet.getWorldPlayers().getBuildersNotTrusted().contains(nickname)) {
                    planet.getWorldPlayers().addBuilder(nickname,true);
                    sender.sendMessage(getLocaleMessage("world.players.builders.trusted").replace("%player%", nickname));
                    return true;
                }
                if (planet.getWorldPlayers().getBuildersTrusted().contains(nickname)) {
                    planet.getWorldPlayers().removeBuilder(nickname);
                    sender.sendMessage(getLocaleMessage("world.players.builders.removed").replace("%player%", nickname));
                    return true;
                }
                /*
                 * Adds online player as not trusted builder, if he's not
                 * listed in builders.
                 */
                if (onlinePlayer != null) {
                    Planet playerPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(onlinePlayer);
                    if (planet.equals(playerPlanet)) {
                        sender.sendMessage(getLocaleMessage("world.players.builders.added").replace("%player%", onlinePlayer.getName()));
                        planet.getWorldPlayers().addBuilder(onlinePlayer.getName(),false);
                    } else {
                        sender.sendMessage(getLocaleMessage("no-player-found"));
                    }
                } else {
                    sender.sendMessage(getLocaleMessage("no-player-found"));
                }
            }
        }
        return true;
    }
}
