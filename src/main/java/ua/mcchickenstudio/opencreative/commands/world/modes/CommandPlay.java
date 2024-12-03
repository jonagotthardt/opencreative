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

package ua.mcchickenstudio.opencreative.commands.world.modes;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.events.plot.PlotModeChangeEvent;
import ua.mcchickenstudio.opencreative.plots.DevPlot;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.coding.CodingBlockParser;
import ua.mcchickenstudio.opencreative.plots.PlotManager;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.plots.Plot;
import org.jetbrains.annotations.NotNull;


import static ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld.removePlayerWithLocation;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;


import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;


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
            setCooldown(player, OpenCreative.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            // Проверка на владельца мира

            DevPlot playerDevPlot = PlotManager.getInstance().getDevPlot(player);
            if (playerDevPlot != null) {
                playerDevPlot.getLastLocations().put(player,player.getLocation());
            }

            removePlayerWithLocation(player);
            if (plot.getMode() != Plot.Mode.PLAYING) {
                if (plot.getWorldPlayers().canDevelop(player)) {
                    PlotModeChangeEvent event = new PlotModeChangeEvent(plot,plot.getMode(), Plot.Mode.PLAYING,player);
                    event.callEvent();
                    if (event.isCancelled()) {
                        return true;
                    }
                    plot.setMode(Plot.Mode.PLAYING);
                    if (isEntityInDevPlot(player)) {
                        clearPlayer(player);
                        player.teleport(plot.getTerritory().getWorld().getSpawnLocation());
                        if (plot.isOwner(sender.getName())) {
                            player.getInventory().setItem(8,createItem(Material.COMPASS,1,"items.developer.world-settings"));
                        }
                        givePlayPermissions(player);
                        EventRaiser.raiseJoinEvent(player);
                    }
                } else {
                    sender.sendMessage(getLocaleMessage("not-owner", player));
                }
            } else {
                if (EventRaiser.raisePlayEvent(player) || plot.getWorldPlayers().canDevelop(player)) {
                    if (plot.getWorldPlayers().canDevelop(player)) {
                        player.sendMessage(getLocaleMessage("world.play-mode.message.owner"));
                        if (plot.getDevPlot().isLoaded()) {
                            new CodingBlockParser().parseCode(plot.getDevPlot());
                        } else {
                            plot.getTerritory().getScript().loadCode();
                        }
                    } else {
                        player.sendMessage(getLocaleMessage("world.play-mode.message.players"));
                    }
                    plot.getTerritory().getWorld().getSpawnLocation().getChunk().load(true);
                    DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);
                    if (devPlot != null) {
                        clearPlayer(player);
                    } else {
                        EventRaiser.raiseQuitEvent(player);
                    }
                    clearPlayer(player);
                    player.teleport(plot.getTerritory().getWorld().getSpawnLocation());
                    if (plot.isOwner(sender.getName())) {
                        player.getInventory().setItem(8,createItem(Material.COMPASS,1,"items.developer.world-settings"));
                    }
                    if (plot.getWorldPlayers().canDevelop(player)) {
                        givePlayPermissions(player);
                    }
                    EventRaiser.raiseJoinEvent(player);
                }
            }
        }
        return true;
    }
}
