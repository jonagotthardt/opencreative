/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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

package ua.mcchickenstudio.opencreative.listeners.player;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.modules.ModuleSettingsMenu;
import ua.mcchickenstudio.opencreative.events.planet.PlanetDisconnectPlayerEvent;
import ua.mcchickenstudio.opencreative.menus.world.settings.PlayerControlMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetFlags;
import ua.mcchickenstudio.opencreative.planets.PlanetPlayer;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;

public final class ChangedWorld implements Listener {

    private static final Map<UUID, Location> developerSetLocation = new HashMap<>();

    public static void addPlayerWithLocation(Player player) {
        developerSetLocation.put(player.getUniqueId(), player.getLocation());
    }

    public static void removePlayerWithLocation(Player player) {
        if (!isPlayerWithLocation(player)) return;
        developerSetLocation.remove(player.getUniqueId());
    }

    public static boolean isPlayerWithLocation(Player player) {
        return developerSetLocation.containsKey(player.getUniqueId());
    }

    public static Location getOldLocationPlayerWithLocation(Player player) {
        return developerSetLocation.get(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerWorldChanged(PlayerChangedWorldEvent event) {

        Player player = event.getPlayer();

        clearWorldModePermissions(player);
        World oldWorld = event.getFrom();
        World newWorld = player.getWorld();
        ChatListener.confirmation.remove(player);
        PlayerControlMenu.removeConfirmation(player);
        ModuleSettingsMenu.removeFromCurrentEditing(player);
        player.clearTitle();

        Planet oldPlanet = OpenCreative.getPlanetsManager().getPlanetByWorld(oldWorld);
        Planet newPlanet = OpenCreative.getPlanetsManager().getPlanetByWorld(newWorld);

        for (Player oldWorldPlayer : oldWorld.getPlayers()) {
            hidePlayerInTab(player, oldWorldPlayer);
            hidePlayerInTab(oldWorldPlayer, player);
        }
        for (Player newWorldPlayer : newWorld.getPlayers()) {
            showPlayerFromTab(player, newWorldPlayer);
            showPlayerFromTab(newWorldPlayer, player);
        }

        if (oldPlanet != null && oldPlanet == newPlanet) {
            // Player is in same planet
            if (isDevPlanet(newWorld)) {
                // Player entered developers world
                if (oldPlanet.getWorldPlayers().canDevelop(player)) {
                    giveDevPermissions(player);
                }
                if (isPlayerWithLocation(player)) {
                    removePlayerWithLocation(player);
                }
                for (Player onlinePlayer : newPlanet.getPlayers()) {
                    showPlayerFromTab(onlinePlayer, player);
                    showPlayerFromTab(player, onlinePlayer);
                }
            } else {
                // Player entered build world
                if (!isPlayerWithLocation(player)) {
                    for (Player onlinePlayer : oldWorld.getPlayers()) {
                        onlinePlayer.sendMessage(MessageUtils.getPlayerLocaleMessage("world.dev-mode.left", player));
                    }
                    for (Player onlinePlayer : newWorld.getPlayers()) {
                        newPlanet.getTerritory().showBorders(onlinePlayer);
                    }
                }
            }
        } else {
            // Player is in different planets / not in planets
            if (!player.hasPermission("opencreative.ignore.world-change-clear")) {
                clearPlayer(player);
            }
            player.setLastDeathLocation(null);
            removePlayerWithLocation(player);
            if (oldPlanet != null) {
                // Player was in planet and left it
                PlanetPlayer planetPlayer = oldPlanet.getWorldPlayers().getPlanetPlayer(player);
                if (planetPlayer != null) planetPlayer.save();
                oldPlanet.getWorldPlayers().unregisterPlayer(player);
                new PlanetDisconnectPlayerEvent(oldPlanet, player).callEvent();
                if (oldPlanet.getOnline() > 0) {
                    if (oldPlanet.getFlagValue(PlanetFlags.PlanetFlag.JOIN_MESSAGES) == 1) {
                        for (Player onlinePlayer : oldPlanet.getPlayers()) {
                            onlinePlayer.sendMessage(MessageUtils.getPlayerLocaleMessage("world.left", player));
                        }
                    }
                    if (oldPlanet.isOwner(player)) {
                        List<String> notTrustedDevelopers = FileUtils.getPlayersFromPlanetList(oldPlanet, Planet.PlayersType.DEVELOPERS_NOT_TRUSTED);
                        List<String> notTrustedBuilders = FileUtils.getPlayersFromPlanetList(oldPlanet, Planet.PlayersType.BUILDERS_NOT_TRUSTED);
                        for (Player p : oldPlanet.getPlayers()) {
                            if (oldPlanet.getMode() == Planet.Mode.BUILD) {
                                if (notTrustedBuilders.contains(p.getName())) {
                                    p.setGameMode(GameMode.ADVENTURE);
                                    p.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                                    clearWorldModePermissions(p);
                                }
                            }
                            if (OpenCreative.getPlanetsManager().getDevPlanet(p) != null) {
                                if (notTrustedDevelopers.contains(p.getName())) {
                                    p.setGameMode(GameMode.ADVENTURE);
                                    p.sendMessage(getLocaleMessage("world.dev-mode.cant-dev-when-offline"));
                                }
                            }
                        }
                    }
                    for (Player oldPlanetPlayer : oldPlanet.getPlayers()) {
                        hidePlayerInTab(player, oldPlanetPlayer);
                        hidePlayerInTab(oldPlanetPlayer, player);
                    }
                } else {
                    if (oldPlanet.isLoaded()) {
                        oldPlanet.getTerritory().unload();
                    }
                }
                oldPlanet.getInformation().updateIconAsync();
            }
            if (newPlanet != null) {
                // Player connected to other planet
                newPlanet.getWorldPlayers().registerPlayer(player);
                for (Player onlinePlayer : newPlanet.getPlayers()) {
                    showPlayerFromTab(onlinePlayer, player);
                    showPlayerFromTab(player, onlinePlayer);
                    newPlanet.getTerritory().showBorders(onlinePlayer);
                }
                if (newPlanet.isOwner(player)) {
                    if (newPlanet.getDevPlanet().isLoaded()) {
                        for (Player onlinePlayer : newPlanet.getDevPlanet().getWorld().getPlayers()) {
                            if (newPlanet.getWorldPlayers().isNotTrustedDeveloper(onlinePlayer)) {
                                onlinePlayer.setGameMode(GameMode.CREATIVE);
                            }
                        }
                    }
                    if (newPlanet.getMode() == Planet.Mode.BUILD) {
                        for (Player onlinePlayer : newPlanet.getTerritory().getWorld().getPlayers()) {
                            if (newPlanet.getWorldPlayers().isNotTrustedBuilder(onlinePlayer)) {
                                onlinePlayer.setGameMode(GameMode.CREATIVE);
                                giveBuildPermissions(onlinePlayer);
                            }
                        }
                    }
                }
                newPlanet.getInformation().updateIconAsync();
            }
        }
    }

}
