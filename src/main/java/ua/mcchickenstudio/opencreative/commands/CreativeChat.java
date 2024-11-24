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
import ua.mcchickenstudio.opencreative.events.player.CreativeChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.parsePAPI;

public class CreativeChat implements CommandExecutor {

    public static final List<Player> creativeChatOff = new ArrayList<>();

    private static boolean chatEnabled = true;

    public static void setChatEnabled(boolean chatEnabled) {
        CreativeChat.chatEnabled = chatEnabled;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (OpenCreative.maintenance && !sender.hasPermission("opencreative.maintenance.bypass")) {
            sender.sendMessage(getLocaleMessage("maintenance"));
            return true;
        }
        if (!chatEnabled && !sender.hasPermission("opencreative.creative-chat.bypass")) {
            sender.sendMessage(getLocaleMessage("creative.creative-chat.off"));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(getLocaleMessage("creative-chat.cc-usage"));
            return true;
        }
        if (sender instanceof Player player) {
            if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            setCooldown(player, OpenCreative.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
        }
        if (args.length == 1 && (args[0].equalsIgnoreCase("off")
                || args[0].equalsIgnoreCase("on")
                || args[0].equalsIgnoreCase("enable")
                || args[0].equalsIgnoreCase("disable"))
                && sender instanceof Player player) {
            if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable")) {
                creativeChatOff.add(player);
                sender.sendMessage(getLocaleMessage("creative-chat.turned-off"));
            } else if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable")) {
                creativeChatOff.remove(player);
                sender.sendMessage(getLocaleMessage("creative-chat.turned-on"));
            }
            return true;
        }
        if (sender instanceof Player player) {
            if (creativeChatOff.contains(player)) {
                sender.sendMessage(getLocaleMessage("creative-chat.on-usage"));
                return true;
            }
        }
        OpenCreative.getPlugin().getLogger().info("[CREATIVE-CHAT] "+sender.getName()+": "+String.join(" ",args));
        for (String executeCommand : OpenCreative.getPlugin().getConfig().getStringList("execute-console-commands.creative-chat")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),parsePAPI(Bukkit.getOfflinePlayer(sender.getName()),executeCommand.replace("%player%",sender.getName()).replace("%message%",String.join(" ",args))));
        }
        String formattedMessage = OpenCreative.getPlugin().getConfig().getString("messages.cc-chat","&6%cc-prefix% &7%player%: %message%");
        formattedMessage = formattedMessage.replace("%player%",sender.getName());
        formattedMessage = formattedMessage.replace("%cc-prefix%", OpenCreative.getPlugin().getConfig().getString("messages.cc-prefix","&6 Chat &8| &7"));
        if (sender instanceof Player) {
            formattedMessage = parsePAPI(Bukkit.getOfflinePlayer(sender.getName()),formattedMessage);
        }
        formattedMessage = formattedMessage.replace("%message%",String.join(" ",args));
        formattedMessage = ChatColor.translateAlternateColorCodes('&',formattedMessage);
        CreativeChatEvent event = new CreativeChatEvent(sender,String.join(" ",args),formattedMessage);
        event.callEvent();
        if (event.isCancelled()) return true;
        formattedMessage = event.getFormattedMessage();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!(creativeChatOff.contains(onlinePlayer))) {
                onlinePlayer.sendMessage(formattedMessage);
            }
        }
        return true;
    }
}
