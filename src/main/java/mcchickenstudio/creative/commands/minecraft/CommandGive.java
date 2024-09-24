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

package mcchickenstudio.creative.commands.minecraft;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.CooldownUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class CommandGive implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getServer().dispatchCommand(sender,"minecraft:give " + String.join(" ",args));
        } else {
            int cooldown = getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (cooldown > 0) {
                sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(cooldown)));
                return true;
            }
            setCooldown(player, Main.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (!player.hasPermission("creative.give.bypass")) {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return true;
                }
                if (!(plot.isOwner(player) || plot.getWorldPlayers().canDevelop(player) || plot.getWorldPlayers().canBuild(player))) {
                    player.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
            }
            if (args.length == 0) {
                sender.sendMessage(getLocaleMessage("commands.give.help"));
                return true;
            }
            // give apple
            if (args.length == 1) {
                try {
                    Material material = Material.valueOf(args[0].replace("minecraft:","").toUpperCase());
                    player.getInventory().addItem(new ItemStack(material));
                } catch (IllegalArgumentException error) {
                    player.sendMessage(getLocaleMessage("commands.give.wrong"));
                }
                // give player apple
            } else if (args.length == 2) {
                Material material;
                Player givePlayer = Bukkit.getPlayer(args[0]);
                if (givePlayer == null) {
                    player.sendMessage(getLocaleMessage("no-player-found"));
                    return true;
                } else {
                    Plot givePlot = PlotManager.getInstance().getPlotByPlayer(givePlayer);
                    if (!player.hasPermission("creative.give.bypass")) {
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (plot == null || !plot.equals(givePlot)) {
                            player.sendMessage(getLocaleMessage("no-player-found"));
                            return true;
                        }
                    }
                }
                try {
                    material = Material.valueOf(args[1].replace("minecraft:", "").toUpperCase());
                    givePlayer.getInventory().addItem(new ItemStack(material));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(getLocaleMessage("commands.give.wrong"));
                }
                // give player item amount
            } else if (args.length == 3) {
                try {
                    Player givePlayer = Bukkit.getPlayer(args[0]);
                    if (givePlayer == null) {
                        player.sendMessage(getLocaleMessage("no-player-found"));
                        return true;
                    }
                    Material material = Material.valueOf(args[1].replace("minecraft:", "").toUpperCase());
                    int amount = Integer.parseInt(args[2]);
                    givePlayer.getInventory().addItem(new ItemStack(material, amount));
                } catch (NumberFormatException error) {
                    player.sendMessage(getLocaleMessage("commands.give.wrong-amount"));
                } catch (IllegalArgumentException error) {
                    player.sendMessage(getLocaleMessage("commands.give.wrong"));
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                tabCompleter.addAll(player.getWorld().getPlayers().stream().map(Player::getName).toList());
            } else if (args.length == 2) {
                tabCompleter.addAll(Arrays.stream(Material.values()).filter(Material::isItem).map(material -> material.name().toLowerCase()).toList());
            } else if (args.length == 3) {
                tabCompleter.add("1");
                tabCompleter.add("16");
                tabCompleter.add("32");
                tabCompleter.add("64");
            }
        }
        return tabCompleter;
    }

}
