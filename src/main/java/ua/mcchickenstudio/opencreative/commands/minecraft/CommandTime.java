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

package ua.mcchickenstudio.opencreative.commands.minecraft;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class CommandTime implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            /*
             * If sender is console, then replace with default /minecraft:time command
             */
            Bukkit.getServer().dispatchCommand(sender, "minecraft:time " + String.join(" ", args));
            return true;
        }
        int cooldown = getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND);
        if (cooldown > 0) {
            sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(cooldown)));
            return true;
        }
        setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
        if (!player.hasPermission("opencreative.time.bypass")) {
            /*
             * Checking is player owner, builder or developer of world.
             * If not, he can't change world's time.
             */
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            if (planet == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
            if (!(planet.isOwner(player) || planet.getWorldPlayers().canDevelop(player) || planet.getWorldPlayers().canBuild(player))) {
                player.sendMessage(getLocaleMessage("not-owner"));
                return true;
            }
        }
        if (args.length != 2 || !(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add"))) {
            sender.sendMessage(getLocaleMessage("commands.time.help"));
            return true;
        }
        int time = 6000;
        boolean add = args[0].equalsIgnoreCase("add");
        try {
            time = Integer.parseInt(args[1]);
        } catch (Exception ignored) {}
        player.getWorld().setTime(add ? time+player.getWorld().getTime() : time);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (args.length == 1) {
            tabCompleter.add("set");
            tabCompleter.add("add");
        } else if (args.length == 2) {
            tabCompleter.add("6000");
            tabCompleter.add("12500");
            tabCompleter.add("15000");
            tabCompleter.add("0");
            tabCompleter.add("1000");
        }
        return tabCompleter;
    }

}
