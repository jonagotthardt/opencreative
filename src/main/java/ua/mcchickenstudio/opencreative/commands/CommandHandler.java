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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;

/**
 * <h1>CommandHandler</h1>
 * This class represents executor and tab completer
 * for OpenCreative+ commands.
 */
public abstract class CommandHandler implements CommandExecutor, TabCompleter {

    public abstract void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

    public abstract @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args);

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            onExecute(sender, command, label, args);
        } catch (Exception error) {
            if (sender instanceof Player player) {
                sendPlayerErrorMessage(player, "Error while executing /" + label + " " + String.join(" ",args), error);
            } else {
                sendCriticalErrorMessage("Error while executing /" + label + " " + String.join(" ",args), error);
            }
        }
        return true;
    }

    @Override
    public final @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabCompleter = onTab(sender, command, alias, args);
        if (tabCompleter == null) return null;
        return tabCompleter.stream().filter(s -> s.startsWith(args[args.length-1])).toList();
    }

}
