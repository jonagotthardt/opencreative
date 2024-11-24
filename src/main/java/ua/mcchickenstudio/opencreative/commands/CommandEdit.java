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

package ua.mcchickenstudio.opencreative.commands;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.plots.Plot;
import ua.mcchickenstudio.opencreative.plots.PlotManager;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;

public class CommandEdit implements CommandExecutor, TabCompleter {

    private final int TEXT_LIMIT = 100;
    private final int LINES_LIMIT = 20;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            OpenCreative.getPlugin().getLogger().info(getLocaleMessage("only-in-world"));
        } else {
            int cooldown = getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (cooldown > 0) {
                sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(cooldown)));
                return true;
            }
            setCooldown(player, OpenCreative.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (!player.hasPermission("opencreative.edit.bypass")) {
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
                sender.sendMessage(getLocaleMessage("commands.edit.help"));
                return true;
            }
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (item.getType().isAir() || meta == null) {
                sender.sendMessage(getLocaleMessage("commands.edit.item"));
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "name": {
                    if (args.length == 1) {
                        if (!meta.hasDisplayName()) {
                            sender.sendMessage(getLocaleMessage("commands.edit.item"));
                            return true;
                        }
                        sender.sendMessage(meta.displayName().clickEvent(ClickEvent.suggestCommand(meta.getDisplayName())));
                    } else {
                        String newName = ChatColor.translateAlternateColorCodes('&',String.join(" ", Arrays.copyOfRange(args,1,args.length)));
                        if (ChatColor.stripColor(newName).length() >= TEXT_LIMIT) {
                            sender.sendMessage(toComponent(getLocaleMessage("commands.edit.text-limit").replace("%limit%",String.valueOf(TEXT_LIMIT))));
                            return true;
                        }
                        meta.setDisplayName(newName);
                        item.setItemMeta(meta);
                        sender.sendMessage(toComponent(getLocaleMessage("commands.edit.renamed").replace("%name%", newName)).clickEvent(ClickEvent.suggestCommand(newName)));
                    }
                    break;
                }
                case "lore", "setlore": {
                    if (args.length == 1) {
                        sender.sendMessage(getLocaleMessage("commands.edit.item"));
                        return true;
                    }
                    int lineNumber = 1;
                    try {
                        lineNumber = Integer.parseInt(args[1]);
                        if (lineNumber < 1) {
                            lineNumber = 1;
                        } else if (lineNumber >= LINES_LIMIT) {
                            sender.sendMessage(toComponent(getLocaleMessage("commands.edit.lines-limit").replace("%limit%",String.valueOf(LINES_LIMIT))));
                            return true;
                        }
                    } catch (NumberFormatException ignored) {}
                    String newLoreLine = ChatColor.translateAlternateColorCodes('&',String.join(" ", Arrays.copyOfRange(args,2,args.length)));
                    if (ChatColor.stripColor(newLoreLine).length() >= TEXT_LIMIT) {
                        sender.sendMessage(toComponent(getLocaleMessage("commands.edit.text-limit").replace("%limit%",String.valueOf(TEXT_LIMIT))));
                        return true;
                    }
                    List<String> newLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
                    if (newLore.size() < lineNumber) {
                        while (newLore.size() < lineNumber) {
                            newLore.add(" ");
                        }
                    }
                    if (args.length == 2) {
                        sender.sendMessage(Component.text(newLore.get(lineNumber-1)).clickEvent(ClickEvent.suggestCommand(newLore.get(lineNumber-1))));
                        return true;
                    }
                    newLore.set(lineNumber-1,newLoreLine);
                    meta.setLore(newLore);
                    item.setItemMeta(meta);
                    sender.sendMessage(toComponent(getLocaleMessage("commands.edit.set-lore").replace("%number%",String.valueOf(lineNumber)).replace("%lore%", newLoreLine)).clickEvent(ClickEvent.suggestCommand(newLoreLine)));
                    break;
                }
                case "addlore": {
                    String newLoreLine = ChatColor.translateAlternateColorCodes('&',String.join(" ", Arrays.copyOfRange(args,1,args.length)));
                    if (ChatColor.stripColor(newLoreLine).length() >= TEXT_LIMIT) {
                        sender.sendMessage(toComponent(getLocaleMessage("commands.edit.text-limit").replace("%limit%",String.valueOf(TEXT_LIMIT))));
                        return true;
                    }
                    List<String> newLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
                    if (newLore.size() >= LINES_LIMIT) {
                        sender.sendMessage(toComponent(getLocaleMessage("commands.edit.lines-limit").replace("%limit%",String.valueOf(LINES_LIMIT))));
                        return true;
                    }
                    newLore.add(newLoreLine);
                    meta.setLore(newLore);
                    item.setItemMeta(meta);
                    sender.sendMessage(toComponent(getLocaleMessage("commands.edit.set-lore").replace("%number%",String.valueOf(newLore.size())).replace("%lore%", newLoreLine)).clickEvent(ClickEvent.suggestCommand(newLoreLine)));
                    break;
                }
                case "removelore", "deletelore", "dellore", "remlore": {
                    if (args.length == 1) {
                        sender.sendMessage(getLocaleMessage("commands.edit.item"));
                        return true;
                    }
                    int lineNumber = 1;
                    try {
                        lineNumber = Integer.parseInt(args[1]);
                        if (lineNumber < 1) {
                            lineNumber = 1;
                        } else if (lineNumber >= LINES_LIMIT) {
                            sender.sendMessage(toComponent(getLocaleMessage("commands.edit.lines-limit").replace("%limit%",String.valueOf(LINES_LIMIT))));
                            return true;
                        }
                    } catch (NumberFormatException ignored) {}
                    List<String> newLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
                    if (newLore.size() >= lineNumber) {
                        newLore.remove(lineNumber-1);
                    }
                    meta.setLore(newLore);
                    item.setItemMeta(meta);
                    sender.sendMessage(toComponent(getLocaleMessage("commands.edit.removed-lore").replace("%number%",String.valueOf(lineNumber))));
                    break;
                }
                case "clear": {
                    meta.displayName(null);
                    meta.lore(null);
                    item.setItemMeta(meta);
                    sender.sendMessage(toComponent(getLocaleMessage("commands.edit.cleared")));
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (args.length <= 1) {
            tabCompleter.add("name");
            tabCompleter.add("lore");
            tabCompleter.add("addlore");
            tabCompleter.add("removelore");
            tabCompleter.add("clear");
        }
        return tabCompleter;
    }
}
