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

package mcchickenstudio.creative.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.ItemUtils.itemEquals;
import static mcchickenstudio.creative.utils.PlayerUtils.clearPlayer;
import static mcchickenstudio.creative.commands.CommandAd.plugin;

import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.MessageUtils.*;

public class CommandDev implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
            if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            setCooldown(player, plugin.getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);

            List<String> developers = new ArrayList<>();
            List<String> trustedDevelopers = FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_TRUSTED);
            List<String> notTrustedDevelopers = FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_NOT_TRUSTED);
            List<String> guestsDevelopers = FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_GUESTS);

            developers.addAll(notTrustedDevelopers);
            developers.addAll(trustedDevelopers);
            developers.addAll(guestsDevelopers);

            if (args.length == 0 || args.length == 3) {
                // Проверка на владельца мира
                if (plot.getOwner().equalsIgnoreCase(sender.getName()) || developers.contains(sender.getName())) {
                    Player plotOwner = Bukkit.getPlayer(plot.getOwner());
                    if (notTrustedDevelopers.contains(sender.getName())) {
                        if (plotOwner == null) {
                            sender.sendMessage(getLocaleMessage("world.dev-mode.cant-dev-when-offline"));
                            return true;
                        }
                        Plot ownerPlot = PlotManager.getInstance().getPlotByPlayer(plotOwner);
                        if (!(ownerPlot == plot)) {
                            sender.sendMessage(getLocaleMessage("world.dev-mode.cant-dev-when-offline"));
                            return true;
                        }
                    }

                    PlayerInventory playerInventory = player.getInventory();
                    ItemStack[] playerInventoryItems = (PlotManager.getInstance().getDevPlot(player) == null ?  playerInventory.getContents() : new ItemStack[]{});
                    clearPlayer(player);
                    sender.sendMessage(getLocaleMessage("world.dev-mode.help", player));
                    if (args.length == 3) {
                        try {
                            double x = Double.parseDouble(args[0]);
                            double y = Double.parseDouble(args[1]);
                            double z = Double.parseDouble(args[2]);
                            plot.teleportToDevPlot(player,x,y,z);
                        } catch (Exception ignored) {
                            plot.teleportToDevPlot(player);
                        }
                    } else {
                        plot.teleportToDevPlot(player);
                    }

                    if (guestsDevelopers.contains(sender.getName())) {
                        player.setGameMode(GameMode.ADVENTURE);
                    } else {
                        player.setGameMode(GameMode.CREATIVE);
                        player.setFlying(true);
                    }
                    giveItems(player);
                    ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
                    for (ItemStack playerItem : playerInventoryItems) {
                        if (playerItem != null && !itemEquals(playerItem, worldSettingsItem)) {
                            player.getInventory().addItem(playerItem);
                        }
                    }
                    player.sendTitle(getLocaleMessage("world.dev-mode.title"), getLocaleMessage("world.dev-mode.subtitle"));
                    player.playSound(player.getLocation(), Sound.valueOf("BLOCK_BEACON_POWER_SELECT"), 100, 1.3f);

                } else {
                    sender.sendMessage(getLocaleMessage("not-owner", player));
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
                if (notTrustedDevelopers.contains(args[0])) {
                    plot.setBuilderTrusted(args[0], true);
                    sender.sendMessage(getLocaleMessage("world.players.developers.trusted").replace("%player%", args[0]));
                } else if (trustedDevelopers.contains(args[0])) {
                    plot.removeBuilder(args[0]);
                    sender.sendMessage(getLocaleMessage("world.players.developers.removed").replace("%player%", args[0]));
                } else {
                    Player addedPlayer = Bukkit.getPlayer(args[0]);
                    if (addedPlayer != null && addedPlayer != player) {
                        Plot plot1 = PlotManager.getInstance().getPlotByPlayer(addedPlayer);
                        if (plot == plot1) {
                            sender.sendMessage(getLocaleMessage("world.players.developers.added").replace("%player%", addedPlayer.getName()));
                            plot.setDeveloperTrusted(addedPlayer.getName(), false);
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

    private void giveItems(Player player) {
        ItemStack eventPlayerItem = createItem(Material.DIAMOND_BLOCK,1,"items.developer.event-player");
        player.getInventory().setItem(0, eventPlayerItem);

        ItemStack actionPlayerItem = createItem(Material.COBBLESTONE,1,"items.developer.action-player");
        player.getInventory().setItem(1, actionPlayerItem);

        ItemStack conditionPlayerItem = createItem(Material.OAK_PLANKS,1,"items.developer.condition-player");
        player.getInventory().setItem(2, conditionPlayerItem);

        ItemStack actionControl = createItem(Material.COAL_BLOCK,1,"items.developer.action-control");
        player.getInventory().setItem(3, actionControl);

        /*ItemStack functionItem = createItem(Material.LAPIS_BLOCK,1,"items.developer.function");
        player.getInventory().setItem(3, functionItem);

        ItemStack cycleItem = createItem(Material.OXIDIZED_COPPER,1,"items.developer.cycle");
        player.getInventory().setItem(4, cycleItem);*/

        //ItemStack elseItem = createItem(Material.END_STONE,1,"items.developer.else");
        //player.getInventory().setItem(3, elseItem);

        ItemStack flySpeedChangerItem = createItem(Material.FEATHER,1,"items.developer.fly-speed-changer");
        player.getInventory().setItem(6, flySpeedChangerItem);

        player.getInventory().setItem(7, createItem(Material.IRON_INGOT,1,"items.developer.variables"));

        ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
        player.getInventory().setItem(8, worldSettingsItem);
    }
}
