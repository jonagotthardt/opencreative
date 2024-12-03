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

package ua.mcchickenstudio.opencreative.commands.minecraft;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.plots.Plot;
import ua.mcchickenstudio.opencreative.plots.PlotManager;
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

public class CommandWeather implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            /*
             * If sender is console, then replace with default /minecraft:weather command
             */
            Bukkit.getServer().dispatchCommand(sender, "minecraft:weather " + String.join(" ", args));
            return true;
        }
        int cooldown = getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND);
        if (cooldown > 0) {
            sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(cooldown)));
            return true;
        }
        setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
        if (!player.hasPermission("opencreative.weather.bypass")) {
            /*
             * Checking is player owner, builder or developer of world.
             * If not, he can't change world's time.
             */
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
        if (args.length != 1 || !(args[0].equalsIgnoreCase("sun") || args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("storm") || args[0].equalsIgnoreCase("rain") || args[0].equalsIgnoreCase("rainy") || args[0].equalsIgnoreCase("thunder"))) {
            sender.sendMessage(getLocaleMessage("commands.weather.help"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "sun", "clear" -> {
                player.getWorld().setStorm(false);
                player.getWorld().setThundering(false);
            }
            case "storm", "rain", "rainy" -> {
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(false);
            }
            case "thunder" -> {
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(true);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (args.length == 1) {
            tabCompleter.add("sun");
            tabCompleter.add("rain");
            tabCompleter.add("thunder");
            return tabCompleter;
        }
        return null;
    }

}
