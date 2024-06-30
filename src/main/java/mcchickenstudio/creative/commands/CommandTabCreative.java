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

package mcchickenstudio.creative.commands;

import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandTabCreative implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (args.length == 1) {
            tabCompleter.add("reload");
            tabCompleter.add("maintenance");
            tabCompleter.add("load");
            tabCompleter.add("unload");
            tabCompleter.add("resetlocale");
        } else if (args.length == 2) {
            if ("maintenance".equalsIgnoreCase(args[0])) {
                tabCompleter.add("start");
                tabCompleter.add("end");
            } else if ("load".equalsIgnoreCase(args[0]) || "unload".equalsIgnoreCase(args[0])) {
                tabCompleter.addAll(PlotManager.getInstance().getPlots().stream().map(plot -> plot.worldID).toList());
            }
        } else if (args.length == 3) {
            if ("start".equalsIgnoreCase(args[1])) {
                tabCompleter.add("120");
                tabCompleter.add("60");
                tabCompleter.add("30");
                tabCompleter.add("15");
            }
        }
        return tabCompleter;
    }

}
