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

import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.events.planet.PlanetModeChangeEvent;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.coding.CodingBlockParser;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.jetbrains.annotations.NotNull;


import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld.removePlayerWithLocation;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;


import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;

public class CommandPlay implements CommandExecutor, TabCompleter {

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
            // Проверка на владельца мира

            DevPlanet playerDevPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
            if (playerDevPlanet != null) {
                playerDevPlanet.getLastLocations().put(player,player.getLocation());
            }

            removePlayerWithLocation(player);
            if (planet.getMode() != Planet.Mode.PLAYING) {
                if (planet.getWorldPlayers().canDevelop(player)) {
                    PlanetModeChangeEvent event = new PlanetModeChangeEvent(planet, planet.getMode(), Planet.Mode.PLAYING,player);
                    event.callEvent();
                    if (event.isCancelled()) {
                        return true;
                    }
                    planet.setMode(Planet.Mode.PLAYING);
                    if (isEntityInDevPlanet(player)) {
                        clearPlayer(player);
                        player.teleport(planet.getTerritory().getWorld().getSpawnLocation());
                        planet.getTerritory().showBorders(player);
                        if (planet.isOwner(sender.getName())) {
                            player.getInventory().setItem(8,createItem(Material.COMPASS,1,"items.developer.world-settings"));
                        }
                        givePlayPermissions(player);
                        EventRaiser.raiseJoinEvent(player);
                    }
                } else {
                    sender.sendMessage(getLocaleMessage("not-owner", player));
                }
            } else {
                if (EventRaiser.raisePlayEvent(player) || planet.getWorldPlayers().canDevelop(player)) {
                    if (planet.getWorldPlayers().canDevelop(player)) {
                        player.sendMessage(getLocaleMessage("world.play-mode.message.owner"));
                        if (!Arrays.asList(args).contains("--no-compile")) {
                            if (planet.getDevPlanet().isLoaded()) {
                                new CodingBlockParser().parseCode(planet.getDevPlanet());
                            } else {
                                planet.getTerritory().getScript().loadCode();
                            }
                        }
                    } else {
                        player.sendMessage(getLocaleMessage("world.play-mode.message.players"));
                    }
                    planet.getTerritory().getWorld().getSpawnLocation().getChunk().load(true);
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet != null) {
                        clearPlayer(player);
                    } else {
                        EventRaiser.raiseQuitEvent(player);
                    }
                    clearPlayer(player);
                    player.teleport(planet.getTerritory().getWorld().getSpawnLocation());
                    planet.getTerritory().showBorders(player);
                    if (planet.isOwner(sender.getName())) {
                        player.getInventory().setItem(8,createItem(Material.COMPASS,1,"items.developer.world-settings"));
                    }
                    if (planet.getWorldPlayers().canDevelop(player)) {
                        givePlayPermissions(player);
                    }
                    EventRaiser.raiseJoinEvent(player);
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return null;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) return null;
        if (planet.getMode() == Planet.Mode.PLAYING && planet.getWorldPlayers().canDevelop(player) && args.length <= 1) {
            return List.of("--no-compile");
        }
        return null;
    }
}
