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
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
import static mcchickenstudio.creative.utils.PlayerUtils.giveBuildPermissions;

public class CommandBuild implements CommandExecutor {
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
            List<String> builders = new ArrayList<>();
            List<String> trustedBuilders = FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.BUILDERS_TRUSTED);
            List<String> notTrustedBuilders = FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.BUILDERS_NOT_TRUSTED);
            builders.addAll(notTrustedBuilders);
            builders.addAll(trustedBuilders);
            if (args.length == 0) {
                removePlayerWithLocation(player);
                if (plot.getPlotMode() != Plot.Mode.BUILD) {
                    if (plot.getOwner().equalsIgnoreCase(sender.getName()) || builders.contains(sender.getName())) {
                        Player plotOwner = Bukkit.getPlayer(plot.getOwner());
                        if (notTrustedBuilders.contains(sender.getName())) {
                            if (plotOwner == null) {
                                sender.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                                return true;
                            }
                            Plot ownerPlot = PlotManager.getInstance().getPlotByPlayer(plotOwner);
                            if (!(ownerPlot == plot)) {
                                sender.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                                return true;
                            }
                        }
                        plot.stopBukkitRunnables();
                        plot.setPlotMode(Plot.Mode.BUILD);
                        for (Player p : plot.getPlayers()){
                            p.sendMessage(getLocaleMessage("world.build-mode.message." + (sender == p ? "owner" : "players")));
                            if (PlotManager.getInstance().getDevPlot(p) == null || sender.getName().equals(p.getName())) {
                                clearPlayer(p);
                                p.sendTitle(getLocaleMessage("world.build-mode.title"),getLocaleMessage("world.build-mode.subtitle"));
                                p.teleport(plot.world.getSpawnLocation());
                                p.playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT,100,1.7f);
                                if (builders.contains(p.getName())) {
                                    if (notTrustedBuilders.contains(p.getName())) {
                                        if (plotOwner != null) {
                                            Plot ownerPlot = PlotManager.getInstance().getPlotByPlayer(plotOwner);
                                            if (ownerPlot == plot) {
                                                p.setGameMode(GameMode.CREATIVE);
                                                giveBuildPermissions(p);
                                            }
                                        }
                                    } else {
                                        p.setGameMode(GameMode.CREATIVE);
                                        giveBuildPermissions(p);
                                    }
                                }
                            }
                        }
                        player.setGameMode(GameMode.CREATIVE);
                        if (plot.isOwner(sender.getName())) {
                            giveBuildPermissions(player);
                            ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
                            player.getInventory().setItem(8,worldSettingsItem);
                        }
                    }
                } else {
                    clearPlayer(player);
                    player.sendTitle(getLocaleMessage("world.build-mode.title"),getLocaleMessage("world.build-mode.subtitle"));
                    player.teleport(plot.world.getSpawnLocation());
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT,100,1.7f);
                    if (plot.getOwner().equalsIgnoreCase(sender.getName()) || builders.contains(sender.getName())) {
                        Player plotOwner = Bukkit.getPlayer(plot.getOwner());
                        if (notTrustedBuilders.contains(sender.getName())) {
                            if (plotOwner == null) {
                                sender.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                                return true;
                            }
                            Plot ownerPlot = PlotManager.getInstance().getPlotByPlayer(plotOwner);
                            if (!(ownerPlot == plot)) {
                                sender.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                                return true;
                            }
                        }
                        if (plot.isOwner(sender.getName())) {
                            ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
                            player.getInventory().setItem(8,worldSettingsItem);
                        }
                        player.setGameMode(GameMode.CREATIVE);
                        giveBuildPermissions(player);
                        sender.sendMessage(getLocaleMessage("world.build-mode.message.owner"));
                    } else {
                        sender.sendMessage(getLocaleMessage("world.build-mode.message.players"));
                    }
                }
            } else {
                if (!plot.getOwner().equalsIgnoreCase(sender.getName())) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                if (plot.getOwner().equalsIgnoreCase(args[0])) {
                    sender.sendMessage(getLocaleMessage("same-player"));
                    return true;
                }
                if (notTrustedBuilders.contains(args[0])) {
                    plot.getWorldPlayers().addBuilder(args[0],true);
                    sender.sendMessage(getLocaleMessage("world.players.builders.trusted").replace("%player%", args[0]));
                } else if (trustedBuilders.contains(args[0])) {
                    plot.getWorldPlayers().removeBuilder(args[0]);
                    sender.sendMessage(getLocaleMessage("world.players.builders.removed").replace("%player%", args[0]));
                } else {
                    Player addedPlayer = Bukkit.getPlayer(args[0]);
                    if (addedPlayer != null && addedPlayer != player) {
                        Plot plot1 = PlotManager.getInstance().getPlotByPlayer(addedPlayer);
                        if (plot == plot1) {
                            sender.sendMessage(getLocaleMessage("world.players.builders.added").replace("%player%", addedPlayer.getName()));
                            plot.getWorldPlayers().addBuilder(addedPlayer.getName(),true);
                            addedPlayer.setGameMode(GameMode.CREATIVE);
                        } else {
                            sender.sendMessage(getLocaleMessage("no-player-found"));
                        }
                    } else {
                        sender.sendMessage(getLocaleMessage("no-player-found"));
                    }
                }
            }
        }
        return true;
    }
}
