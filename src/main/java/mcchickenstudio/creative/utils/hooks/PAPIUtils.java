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

package mcchickenstudio.creative.utils.hooks;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIUtils {

    public static String parsePlaceholdersAPI(OfflinePlayer offlinePlayer, String string) {
        return PlaceholderAPI.setPlaceholders(offlinePlayer,string);
    }

    public static void registerPlaceholder() {
        new Placeholder().register();
    }


}

class Placeholder extends PlaceholderExpansion {

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        switch (identifier) {
            case "plot_id" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) return plot.worldID;
            }
            case "plot_custom_id" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) return plot.getInformation().getCustomID();
            }
            case "plot_online" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) return String.valueOf(plot.getOnline());
            }
            case "plot_uniques" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) return String.valueOf(plot.getUniques());
            }
            case "plot_reputation" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) return String.valueOf(plot.getReputation());
            }
            case "is_in_plot" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                return String.valueOf((plot != null));
            }
            case "plot_name" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) return plot.getInformation().getDisplayName();
            }
            case "plot_description" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) return plot.getInformation().getDescription();
            }
            case "plot_owner" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) return plot.getOwner();
            }
            case "plot_category" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) return String.valueOf(plot.getInformation().getCategory());
            }
            case "plot_sharing" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) return String.valueOf(plot.getPlotSharing());
            }
            case "plot_mode" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) return String.valueOf(plot.getPlotMode());
            }
            case "plot_is_dev_plot_loaded" -> {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot != null) return String.valueOf(plot.devPlot != null && plot.devPlot.isLoaded());
            }
        }
        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "creative";
    }

    @Override
    public @NotNull String getAuthor() {
        return "McChicken Studio";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.5";
    }

    @Override
    public boolean canRegister() {
        return (Main.getPlugin() != null);
    }
}
