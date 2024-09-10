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

package mcchickenstudio.creative.commands.world.modes;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.plots.DevPlot;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import mcchickenstudio.creative.coding.CodingBlockParser;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.utils.FileUtils;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;


import static mcchickenstudio.creative.events.player.ChangedWorld.removePlayerWithLocation;
import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.PlayerUtils.clearPlayer;


import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.MessageUtils.*;


public class CommandPlay implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
            if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player,CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            setCooldown(player, Main.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            // Проверка на владельца мира

            DevPlot playerDevPlot = PlotManager.getInstance().getDevPlot(player);
            if (playerDevPlot != null) {
                playerDevPlot.lastLocations.put(player,player.getLocation());
            }

            List<String> developers = new ArrayList<>();
            List<String> trustedDevelopers = FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_TRUSTED);
            List<String> notTrustedDevelopers = FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_NOT_TRUSTED);

            developers.addAll(notTrustedDevelopers);
            developers.addAll(trustedDevelopers);

            removePlayerWithLocation(player);
            if (plot.getPlotMode() != Plot.Mode.PLAYING) {
                if (plot.getOwner().equals(sender.getName()) || developers.contains(sender.getName())) {
                    plot.setPlotMode(Plot.Mode.PLAYING);
                    for (Player p : plot.getPlayers()) {
                        if (PlotManager.getInstance().getDevPlot(p) == null || sender.getName().equals(p.getName())) {
                            p.sendMessage(getLocaleMessage("world.play-mode.message." + (sender == p ? "owner" : "players")));
                            clearPlayer(p);
                            plot.world.getSpawnLocation().getChunk().load(true);
                            p.teleport(plot.world.getSpawnLocation());
                        }
                    }
                    if (plot.isOwner(sender.getName())) {
                        player.getInventory().setItem(8,createItem(Material.COMPASS,1,"items.developer.world-settings"));
                    }
                    if (plot.devPlot.isLoaded()) {
                        new CodingBlockParser().parseCode(plot.devPlot);
                    } else {
                        plot.getScript().loadCode();
                    }
                    for (Player p : plot.getPlayers()) {
                        if (PlotManager.getInstance().getDevPlot(p) == null || sender.getName().equals(p.getName())) {
                            EventRaiser.raiseJoinEvent(p);
                        }
                    }
                } else {
                    sender.sendMessage(getLocaleMessage("not-owner", player));
                }
            } else {
                if (EventRaiser.raisePlayEvent(player) || plot.getWorldPlayers().canDevelop(player)) {
                    plot.world.getSpawnLocation().getChunk().load(true);
                    DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);
                    player.teleport(plot.world.getSpawnLocation());
                    if (plot.getWorldPlayers().canDevelop(player)) {
                        clearPlayer(player);
                        player.sendMessage(getLocaleMessage("world.play-mode.message.owner"));
                        if (plot.isOwner(sender.getName())) {
                            player.getInventory().setItem(8,createItem(Material.COMPASS,1,"items.developer.world-settings"));
                        }
                        if (plot.devPlot.isLoaded()) {
                            new CodingBlockParser().parseCode(plot.devPlot);
                        } else {
                            plot.getScript().loadCode();
                        }
                    } else {
                        player.sendMessage(getLocaleMessage("world.play-mode.message.players"));
                    }
                    if (devPlot == null) {
                        EventRaiser.raiseQuitEvent(player);
                    }
                    EventRaiser.raiseJoinEvent(player);
                }
            }
        }
        return true;
    }
}
