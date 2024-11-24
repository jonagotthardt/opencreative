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
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.jetbrains.annotations.NotNull;

import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class CommandSpawn implements CommandExecutor {

    final static Plugin plugin = OpenCreative.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            if (!(sender instanceof Player) || sender.hasPermission("opencreative.spawnothers")) {
                if (!(Bukkit.getPlayer(args[0]) == null)) {
                    EventRaiser.raiseQuitEvent(Bukkit.getPlayer(args[0]));
                    teleportToLobby((Bukkit.getPlayer(args[0])));
                } else {
                    sender.sendMessage(getLocaleMessage("not-found-player"));
                }
            } else {
                sender.sendMessage(getLocaleMessage("no-perms"));
            }
        } else {
            if (sender instanceof Player player) {
                if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                    sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND))));
                    return true;
                }
                setCooldown(player,plugin.getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
                EventRaiser.raiseQuitEvent(player);
                teleportToLobby(player);
            }
        }
        return true;
    }
}
