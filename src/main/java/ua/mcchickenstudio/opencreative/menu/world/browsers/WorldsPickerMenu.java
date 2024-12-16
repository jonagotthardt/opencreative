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

package ua.mcchickenstudio.opencreative.menu.world.browsers;

import ua.mcchickenstudio.opencreative.plots.Plot;
import ua.mcchickenstudio.opencreative.plots.PlotManager;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;

public class WorldsPickerMenu extends WorldsBrowserMenu{

    public WorldsPickerMenu(Player player, Set<Plot> plots) {
        super(player, plots, false);
    }

    @Override
    protected void onPlotClick(Player player, Plot downloadablePlot) {
        if (downloadablePlot.getInformation().isDownloadable()) {
            int id = WorldUtils.generateWorldID();
            FileUtils.copyFilesToDirectory(FileUtils.getPlotFolder(downloadablePlot),new File(Bukkit.getWorldContainer().getPath() + File.separator + "unloadedWorlds" + File.separator + "plot" + id));
            if (downloadablePlot.getDevPlot().exists()) {
                FileUtils.copyFilesToDirectory(FileUtils.getDevPlotFolder(downloadablePlot.getDevPlot()),new File(Bukkit.getWorldContainer().getPath() + File.separator + "unloadedWorlds" + File.separator + "plot" + id + "dev"));
            }
            Plot newPlot = new Plot(id);
            FileUtils.setPlotConfigParameter(newPlot,"creation-time",System.currentTimeMillis());
            newPlot.setOwner(player.getName());
            newPlot.getInformation().setCustomID(String.valueOf(id));
            newPlot.getInformation().setDownloadable(false);
            newPlot.getWorldPlayers().purgeData();
            PlotManager.getInstance().registerPlot(newPlot);
            FileUtils.deleteFolder(new File(FileUtils.getPlotFolder(newPlot).getPath() + File.separator + "playersData"));
            FileUtils.deleteUnnecessaryWorldFiles(FileUtils.getPlotFolder(newPlot));
            newPlot.connectPlayer(player);
        }
    }
}
