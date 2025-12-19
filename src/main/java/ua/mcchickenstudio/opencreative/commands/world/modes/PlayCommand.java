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

import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.JoinEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.PlayEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.QuitEvent;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.events.planet.PlanetModeChangeEvent;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.coding.CodingBlockParser;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.jetbrains.annotations.NotNull;


import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld.removePlayerWithLocation;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.*;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;


import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;

/**
 * <h1>PlayCommand</h1>
 * This command is responsible for changing current world's mode
 * to play mode. If it's already set, it can teleport player to
 * spawn location and load code.
 * <p>
 * Available: For world developers.
 */
public class PlayCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return;
        }

        if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;

        if (player.isDead()) {
            sender.sendMessage(getLocaleMessage("only-alive"));
            return;
        }
        
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) {
            player.sendMessage(getLocaleMessage("only-in-world"));
            return;
        }

        if (!OpenCreative.getSettings().getCodingSettings().isEnabled()) {
            player.sendMessage(getLocaleMessage("world.dev-mode.disabled"));
            return;
        }

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
                    return;
                }
                if (!OpenCreative.getStability().isFine()) {
                    player.sendMessage(getLocaleMessage("creative.stability.cannot"));
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                planet.setMode(Planet.Mode.PLAYING);
                if (isEntityInDevPlanet(player)) {
                    clearPlayer(player);
                    player.teleport(planet.getTerritory().getSpawnLocation());
                    planet.getTerritory().showBorders(player);
                    if (planet.isOwner(sender.getName())) {
                        player.getInventory().setItem(8,createItem(Material.COMPASS,1,"items.developer.world-settings"));
                    }
                    givePlayPermissions(player);
                    new JoinEvent(player).callEvent();
                }
            } else {
                sender.sendMessage(getPlayerLocaleMessage("not-owner", player));
            }
        } else {
            if (new PlayEvent(player).callEvent() || planet.getWorldPlayers().canDevelop(player)) {
                if (planet.getWorldPlayers().canDevelop(player)) {
                    if (!OpenCreative.getStability().isFine()) {
                        player.sendMessage(getLocaleMessage("creative.stability.cannot"));
                        Sounds.PLAYER_FAIL.play(player);
                    } else {
                        player.sendMessage(getLocaleMessage("world.play-mode.message.owner"));
                        if (!Arrays.asList(args).contains("--no-compile")) {
                            if (planet.getDevPlanet().isLoaded()) {
                                new CodingBlockParser(planet.getDevPlanet()).parseCode(planet.getDevPlanet());
                            } else {
                                planet.getTerritory().getScript().loadCode();
                            }
                        }
                    }
                } else {
                    player.sendMessage(getLocaleMessage("world.play-mode.message.players"));
                }
                planet.getTerritory().getSpawnLocation().getChunk().load(true);
                DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                if (devPlanet != null) {
                    clearPlayer(player);
                } else {
                    new QuitEvent(player).callEvent();
                }
                clearPlayer(player);
                player.teleport(planet.getTerritory().getSpawnLocation());
                planet.getTerritory().showBorders(player);
                if (planet.isOwner(sender.getName())) {
                    player.getInventory().setItem(8,createItem(Material.COMPASS,1,"items.developer.world-settings"));
                }
                if (planet.getWorldPlayers().canDevelop(player)) {
                    givePlayPermissions(player);
                }
                new JoinEvent(player).callEvent();
            }
        }
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return null;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) return null;
        if (planet.getMode() == Planet.Mode.PLAYING && planet.getWorldPlayers().canDevelop(player) && args.length <= 1) {
            return List.of("--no-compile");
        }
        return null;
    }
}
