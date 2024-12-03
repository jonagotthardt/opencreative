/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com
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

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.CreativeChat;

import ua.mcchickenstudio.opencreative.events.plot.PlotDisconnectPlayerEvent;
import ua.mcchickenstudio.opencreative.plots.PlotFlags;
import ua.mcchickenstudio.opencreative.plots.WorldPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import ua.mcchickenstudio.opencreative.plots.Plot;
import ua.mcchickenstudio.opencreative.plots.PlotManager;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;
import static ua.mcchickenstudio.opencreative.utils.WorldUtils.isDevPlot;

public class ChangedWorld implements Listener {

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
        PlayerChat.confirmation.remove(event.getPlayer());
        CreativeChat.creativeChatOff.remove(event.getPlayer());
        event.getPlayer().clearTitle();

        Plot oldPlot = PlotManager.getInstance().getPlotByWorld(oldWorld);
        Plot newPlot = PlotManager.getInstance().getPlotByWorld(newWorld);

        for (Player oldWorldPlayer : oldWorld.getPlayers()) {
            hidePlayerInTab(player,oldWorldPlayer);
            hidePlayerInTab(oldWorldPlayer,player);
        }
        for (Player newWorldPlayer : newWorld.getPlayers()) {
            showPlayerFromTab(player,newWorldPlayer);
            showPlayerFromTab(newWorldPlayer,player);
        }

        if (oldPlot != null && oldPlot == newPlot) {
            if (isDevPlot(oldWorld)) {
                if (!isPlayerWithLocation(player)) {
                    for (Player onlinePlayer : oldWorld.getPlayers()) {
                        onlinePlayer.sendMessage(getLocaleMessage("world.dev-mode.left", player));
                    }
                }
            } else if (isDevPlot(newWorld)) {
                if (!isPlayerWithLocation(player)) {
                    for (Player onlinePlayer : newWorld.getPlayers()) {
                        onlinePlayer.sendMessage(getLocaleMessage("world.dev-mode.joined", player));
                    }
                } else {
                    removePlayerWithLocation(player);
                }
                for (Player onlinePlayer : newPlot.getPlayers()) {
                    showPlayerFromTab(onlinePlayer,player);
                }
            }
        } else {
            player.setLastDeathLocation(null);
            removePlayerWithLocation(player);
            if (oldPlot != null) {
                WorldPlayer worldPlayer = oldPlot.getWorldPlayers().getPlotPlayer(player);
                worldPlayer.save();
                oldPlot.getWorldPlayers().unregisterPlayer(player);
                new PlotDisconnectPlayerEvent(oldPlot,player).callEvent();
                if (oldPlot.getOnline() > 0) {
                    if (oldPlot.getFlagValue(PlotFlags.PlotFlag.JOIN_MESSAGES) == 1) {
                        for (Player onlinePlayer : oldPlot.getPlayers()) {
                            onlinePlayer.sendMessage(getLocaleMessage("world.left", player));
                            hidePlayerInTab(player,onlinePlayer);
                            hidePlayerInTab(onlinePlayer,player);
                        }
                    }
                    if (oldPlot.isOwner(player)) {
                        List<String> notTrustedDevelopers = FileUtils.getPlayersFromPlotList(oldPlot, Plot.PlayersType.DEVELOPERS_NOT_TRUSTED);
                        List<String> notTrustedBuilders = FileUtils.getPlayersFromPlotList(oldPlot, Plot.PlayersType.BUILDERS_NOT_TRUSTED);
                        for (Player p : oldPlot.getPlayers()) {
                            if (oldPlot.getMode() == Plot.Mode.BUILD) {
                                if (notTrustedBuilders.contains(p.getName())) {
                                    p.setGameMode(GameMode.ADVENTURE);
                                    p.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                                    clearWorldModePermissions(p);
                                }
                            }
                            if (PlotManager.getInstance().getDevPlot(p) != null) {
                                if (notTrustedDevelopers.contains(p.getName())) {
                                    p.setGameMode(GameMode.ADVENTURE);
                                    p.sendMessage(getLocaleMessage("world.dev-mode.cant-dev-when-offline"));
                                }
                            }
                        }
                    }
                    for (Player plotPlayer : oldPlot.getPlayers()) {
                        hidePlayerInTab(player,plotPlayer);
                        hidePlayerInTab(plotPlayer,player);
                    }
                } else {
                    if (oldPlot.isLoaded()) {
                        oldPlot.getTerritory().unload();
                    }
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        oldPlot.getInformation().updateIcon();
                    }
                }.runTaskAsynchronously(OpenCreative.getPlugin());
            }
            if (newPlot != null) {
                if (newPlot.getFlagValue(PlotFlags.PlotFlag.JOIN_MESSAGES) == 1) {
                    for (Player onlinePlayer : newPlot.getPlayers()) {
                        onlinePlayer.sendMessage(getLocaleMessage("world.joined", player));
                    }
                }
                for (Player onlinePlayer : newPlot.getPlayers()) {
                    showPlayerFromTab(onlinePlayer,player);
                    showPlayerFromTab(player,onlinePlayer);
                }
                if (newPlot.isOwner(player)) {
                    if (newPlot.getDevPlot().isLoaded()) {
                        for (Player onlinePlayer : newPlot.getDevPlot().getWorld().getPlayers()) {
                            if (newPlot.getWorldPlayers().isNotTrustedDeveloper(onlinePlayer)) {
                                onlinePlayer.setGameMode(GameMode.CREATIVE);
                            }
                        }
                    }
                    if (newPlot.getMode() == Plot.Mode.BUILD) {
                        for (Player onlinePlayer : newPlot.getTerritory().getWorld().getPlayers()) {
                            if (newPlot.getWorldPlayers().isNotTrustedBuilder(onlinePlayer)) {
                                onlinePlayer.setGameMode(GameMode.CREATIVE);
                                giveBuildPermissions(onlinePlayer);
                            }
                        }
                    }

                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        newPlot.getInformation().updateIcon();
                    }
                }.runTaskAsynchronously(OpenCreative.getPlugin());
            }
        }
    }

}
