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
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.indev.modules.Module;
import ua.mcchickenstudio.opencreative.indev.modules.ModuleManager;
import ua.mcchickenstudio.opencreative.indev.modules.ModulesBrowserMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class CommandModule implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return true;
        }
        if (!sender.hasPermission("opencreative.module")) {
            sender.sendMessage(getLocaleMessage("no-perms"));
            return true;
        }
        if (!OpenCreative.getSettings().isDebug()) {
            sender.sendMessage(getLocaleMessage("disabled"));
            return true;
        }
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        if (devPlanet == null) {
            sender.sendMessage(getLocaleMessage("only-in-dev-world"));
            return true;
        }
        if (!devPlanet.getPlanet().getWorldPlayers().canDevelop(player)) {
            sender.sendMessage(getLocaleMessage("not-developer"));
            return true;
        }
        if (args.length == 0) {
            new ModulesBrowserMenu(player).open(player);
            return true;
        } else if (args.length == 1) {
            sender.sendMessage(getLocaleMessage("modules.help"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "load" -> {
                Module module = ModuleManager.getInstance().getModuleById(args[1]);
                module.place(devPlanet, player);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of("load","remove","moderate");
    }
}
