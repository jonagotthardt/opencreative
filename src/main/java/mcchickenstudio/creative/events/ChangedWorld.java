/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package mcchickenstudio.creative.events;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.commands.CreativeChat;

import mcchickenstudio.creative.plots.PlotFlags;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static mcchickenstudio.creative.events.PlayerMove.previousLocation;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.PlayerUtils.clearBuildPermissions;
import static mcchickenstudio.creative.utils.PlayerUtils.translateBlockSign;

public class ChangedWorld implements Listener {

    @EventHandler
    public void onPlayerWorldChanged(PlayerChangedWorldEvent event) {

        Player player = event.getPlayer();

        clearBuildPermissions(player);
        World oldWorld = event.getFrom();
        World newWorld = player.getWorld();

        previousLocation.remove(player.getUniqueId());

        PlayerChat.confirmation.remove(event.getPlayer());
        CreativeChat.creativeChatOff.remove(event.getPlayer());
        event.getPlayer().clearTitle();

        Plot oldPlot = PlotManager.getInstance().getPlotByWorld(oldWorld);
        Plot newPlot = PlotManager.getInstance().getPlotByWorld(newWorld);

        if (oldPlot != null && oldPlot == newPlot) {
            if (oldWorld.getName().endsWith("dev")) {
                for (Player onlinePlayer : oldWorld.getPlayers()) {
                    onlinePlayer.sendMessage(getLocaleMessage("world.dev-mode.left", player));
                }
            } else if (newWorld.getName().endsWith("dev")) {
                for (Player onlinePlayer : newWorld.getPlayers()) {
                   onlinePlayer.sendMessage(getLocaleMessage("world.dev-mode.joined", player));
                }
                EventRaiser.raiseQuitEvent(event.getPlayer());
            }
        } else {
            if (oldPlot != null) {
                if (oldPlot.getOnline() > 0) {
                    if (oldPlot.getFlagValue(PlotFlags.PlotFlag.JOIN_MESSAGES) == 1) {
                        for (Player onlinePlayer : oldPlot.getPlayers()) {
                            onlinePlayer.sendMessage(getLocaleMessage("world.left", player));
                            onlinePlayer.hidePlayer(player);
                            player.hidePlayer(onlinePlayer);
                        }
                    }
                    if (oldPlot.isOwner(player)) {
                        List<String> notTrustedDevelopers = FileUtils.getPlayersFromPlotConfig(oldPlot, Plot.PlayersType.DEVELOPERS_NOT_TRUSTED);
                        List<String> notTrustedBuilders = FileUtils.getPlayersFromPlotConfig(oldPlot, Plot.PlayersType.BUILDERS_NOT_TRUSTED);
                        for (Player p : oldPlot.getPlayers()) {
                            if (oldPlot.plotMode == Plot.Mode.BUILD) {
                                if (notTrustedBuilders.contains(p.getName())) {
                                    p.setGameMode(GameMode.ADVENTURE);
                                    p.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
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
                    EventRaiser.raiseQuitEvent(event.getPlayer());
                } else {
                    PlotManager.getInstance().unloadPlot(oldPlot);
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        oldPlot.updatePlotIcon();
                    }
                }.runTaskAsynchronously(Main.getPlugin());
            } else {
                for (Player onlinePlayer : oldWorld.getPlayers()) {
                    onlinePlayer.hidePlayer(player);
                    player.hidePlayer(onlinePlayer);
                }
            }
            if (newPlot != null) {
                if (newPlot.getFlagValue(PlotFlags.PlotFlag.JOIN_MESSAGES) == 1) {
                    for (Player onlinePlayer : newPlot.getPlayers()) {
                        onlinePlayer.sendMessage(getLocaleMessage("world.joined", player));
                        onlinePlayer.showPlayer(player);
                        player.showPlayer(onlinePlayer);
                    }
                }
                EventRaiser.raiseJoinEvent(event.getPlayer());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        newPlot.updatePlotIcon();
                    }
                }.runTaskAsynchronously(Main.getPlugin());
            } else {
                for (Player onlinePlayer : newWorld.getPlayers()) {
                    onlinePlayer.showPlayer(player);
                    player.showPlayer(onlinePlayer);
                }
            }
        }
    }

}
