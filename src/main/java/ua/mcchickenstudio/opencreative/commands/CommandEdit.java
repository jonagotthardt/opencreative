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

package ua.mcchickenstudio.opencreative.commands;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;

/**
 * <h1>CommandEdit</h1>
 * This command is responsible for editing items data,
 * like display name and lore.
 * <p>
 * Available: For world builders or developers.
 */
public class CommandEdit extends CommandHandler {

    private static final int TEXT_LIMIT = 100;
    private static final int LINES_LIMIT = 20;

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            OpenCreative.getPlugin().getLogger().info(getLocaleMessage("only-in-world"));
            return;
        }

        if (!checkPermissions(player)) return;

        if (args.length == 0) {
            sender.sendMessage(getLocaleMessage("commands.edit.help"));
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        ItemMeta meta = item.getItemMeta();
        if (item.getType().isAir() || meta == null) {
            sender.sendMessage(getLocaleMessage("commands.edit.item"));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "name":
                handleSetName(player, item, args);
                break;
            case "lore", "setlore":
                handleSetLore(player, item, args);
                break;
            case "addlore":
                handleAddLore(player, item, args);
                break;
            case "removelore", "deletelore", "dellore", "remlore":
                handleRemoveLore(player, item, args);
                break;
            case "clear":
                handleClear(player, item);
                break;
        }

        return;
    }

    private boolean checkPermissions(Player player) {
        if (!checkAndSetCooldownWithMessage(player, OpenCreative.getSettings().getGroups().getGroup(player), CooldownUtils.CooldownType.GENERIC_COMMAND)) {
            return false;
        }

        if (player.hasPermission("opencreative.edit.bypass")) return true;

        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) {
            player.sendMessage(getLocaleMessage("only-in-world"));
            return false;
        }
        if (!(planet.isOwner(player) || planet.getWorldPlayers().canDevelop(player) || planet.getWorldPlayers().canBuild(player))) {
            player.sendMessage(getLocaleMessage("not-owner"));
            return false;
        }

        return true;
    }

    private String joinArgs(String[] args, int fromIndex) {
        return String.join(" ", Arrays.copyOfRange(args, fromIndex, args.length));
    }

    private void handleSetName(Player player, ItemStack item, String[] args) {
        ItemMeta meta = item.getItemMeta();
        if (args.length == 1) {
            if (!meta.hasDisplayName()) {
                player.sendMessage(getLocaleMessage("commands.edit.item"));
                return;
            }
            player.sendMessage(meta.displayName().clickEvent(ClickEvent.suggestCommand(meta.getDisplayName())));
        } else {
            String newName = ChatColor.translateAlternateColorCodes('&', joinArgs(args, 1));
            if (ChatColor.stripColor(newName).length() >= TEXT_LIMIT) {
                player.sendMessage(toComponent(getLocaleMessage("commands.edit.text-limit").replace("%limit%",String.valueOf(TEXT_LIMIT))));
                return;
            }
            meta.setDisplayName(newName);
            item.setItemMeta(meta);
            player.sendMessage(toComponent(getLocaleMessage("commands.edit.renamed").replace("%name%", newName)).clickEvent(ClickEvent.suggestCommand(newName)));
        }
    }

    private void handleSetLore(Player player, ItemStack item, String[] args) {
        ItemMeta meta = item.getItemMeta();
        if (args.length == 1) {
            player.sendMessage(getLocaleMessage("commands.edit.item"));
            return;
        }
        int lineNumber = 1;
        try {
            lineNumber = Integer.parseInt(args[1]);
            if (lineNumber < 1) {
                lineNumber = 1;
            } else if (lineNumber >= LINES_LIMIT) {
                player.sendMessage(toComponent(getLocaleMessage("commands.edit.lines-limit").replace("%limit%",String.valueOf(LINES_LIMIT))));
                return;
            }
        } catch (NumberFormatException ignored) {}
        String newLoreLine = ChatColor.translateAlternateColorCodes('&', joinArgs(args, 1));
        if (ChatColor.stripColor(newLoreLine).length() >= TEXT_LIMIT) {
            player.sendMessage(toComponent(getLocaleMessage("commands.edit.text-limit").replace("%limit%",String.valueOf(TEXT_LIMIT))));
            return;
        }
        List<String> newLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        if (newLore.size() < lineNumber) {
            while (newLore.size() < lineNumber) {
                newLore.add(" ");
            }
        }
        if (args.length == 2) {
            player.sendMessage(Component.text(newLore.get(lineNumber-1)).clickEvent(ClickEvent.suggestCommand(newLore.get(lineNumber-1))));
            return;
        }
        newLore.set(lineNumber-1,newLoreLine);
        meta.setLore(newLore);
        item.setItemMeta(meta);
        player.sendMessage(toComponent(getLocaleMessage("commands.edit.set-lore").replace("%number%",String.valueOf(lineNumber)).replace("%lore%", newLoreLine)).clickEvent(ClickEvent.suggestCommand(newLoreLine)));
    }

    private void handleAddLore(Player player, ItemStack item, String[] args) {
        ItemMeta meta = item.getItemMeta();
        String newLoreLine = ChatColor.translateAlternateColorCodes('&', joinArgs(args, 1));
        if (ChatColor.stripColor(newLoreLine).length() >= TEXT_LIMIT) {
            player.sendMessage(toComponent(getLocaleMessage("commands.edit.text-limit").replace("%limit%",String.valueOf(TEXT_LIMIT))));
            return;
        }
        List<String> newLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        if (newLore.size() >= LINES_LIMIT) {
            player.sendMessage(toComponent(getLocaleMessage("commands.edit.lines-limit").replace("%limit%",String.valueOf(LINES_LIMIT))));
            return;
        }
        newLore.add(newLoreLine);
        meta.setLore(newLore);
        item.setItemMeta(meta);
        player.sendMessage(toComponent(getLocaleMessage("commands.edit.set-lore").replace("%number%",String.valueOf(newLore.size())).replace("%lore%", newLoreLine)).clickEvent(ClickEvent.suggestCommand(newLoreLine)));
    }

    private void handleRemoveLore(Player player, ItemStack item, String[] args) {
        ItemMeta meta = item.getItemMeta();
        if (args.length == 1) {
            player.sendMessage(getLocaleMessage("commands.edit.item"));
            return;
        }
        int lineNumber = 1;
        try {
            lineNumber = Integer.parseInt(args[1]);
            if (lineNumber < 1) {
                lineNumber = 1;
            } else if (lineNumber >= LINES_LIMIT) {
                player.sendMessage(toComponent(getLocaleMessage("commands.edit.lines-limit").replace("%limit%",String.valueOf(LINES_LIMIT))));
                return;
            }
        } catch (NumberFormatException ignored) {}
        List<String> newLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        if (newLore.size() >= lineNumber) {
            newLore.remove(lineNumber-1);
        }
        meta.setLore(newLore);
        item.setItemMeta(meta);
        player.sendMessage(toComponent(getLocaleMessage("commands.edit.removed-lore").replace("%number%",String.valueOf(lineNumber))));

    }

    private void handleClear(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.displayName(null);
        meta.lore(null);
        item.setItemMeta(meta);
        player.sendMessage(toComponent(getLocaleMessage("commands.edit.cleared")));
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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
