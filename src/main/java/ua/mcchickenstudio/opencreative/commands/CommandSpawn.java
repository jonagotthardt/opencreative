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

import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class CommandSpawn implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            setCooldown(player,OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
        }
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                // Console cannot be teleported to lobby
                sender.sendMessage("Only player can be teleported to lobby");
                return true;
            }
            EventRaiser.raiseQuitEvent(player);
            teleportToLobby(player);
            return true;
        }
        if (!sender.hasPermission("opencreative.spawn.others")) {
            sender.sendMessage(getLocaleMessage("no-perms"));
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(getLocaleMessage("not-found-player"));
            return true;
        }
        EventRaiser.raiseQuitEvent(player);
        teleportToLobby(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("opencreative.spawn.others") || args.length > 1) {
            return null;
        }
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
