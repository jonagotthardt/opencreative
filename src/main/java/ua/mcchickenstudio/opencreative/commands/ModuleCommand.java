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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.indev.modules.Module;
import ua.mcchickenstudio.opencreative.indev.modules.ModuleManager;
import ua.mcchickenstudio.opencreative.indev.modules.ModulesBrowserMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class ModuleCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return;
        }
        if (!sender.hasPermission("opencreative.module")) {
            sender.sendMessage(getLocaleMessage("no-perms"));
            return;
        }
        if (!OpenCreative.getSettings().isDebug()) {
            sender.sendMessage(getLocaleMessage("disabled"));
            return;
        }
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        if (devPlanet == null) {
            sender.sendMessage(getLocaleMessage("only-in-dev-world"));
            return;
        }
        if (!devPlanet.getPlanet().getWorldPlayers().canDevelop(player)) {
            sender.sendMessage(getLocaleMessage("not-developer"));
            return;
        }
        if (args.length == 0) {
            new ModulesBrowserMenu(player).open(player);
            return;
        } else if (args.length == 1) {
            sender.sendMessage(getLocaleMessage("modules.help"));
            return;
        }
        switch (args[0].toLowerCase()) {
            case "load" -> {
                Module module = ModuleManager.getInstance().getModuleById(args[1]);
                module.place(devPlanet, player);
            }
        }
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of("load","remove","moderate");
    }
}
