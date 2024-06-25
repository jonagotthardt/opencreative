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

package mcchickenstudio.creative.debug;

import mcchickenstudio.creative.plots.Plot;
import org.bukkit.inventory.ItemStack;

public class PlotInfo {

    private final Plot plot;
    private String name;
    private String description;
    private ItemStack icon;
    private String id;
    private PlotAccess sharing;
    private Plot.Category category;

    public PlotInfo(Plot plot) {
        this.plot = plot;
    }

    public void load() {

    }

    public void save() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(Plot.Category category) {
        this.category = category;
    }

    public void setSharing(PlotAccess sharing) {
        this.sharing = sharing;
    }

    public Plot getPlot() {
        return plot;
    }

    public Plot.Category getCategory() {
        return category;
    }

    public enum PlotAccess {
        PUBLIC,
        PRIVATE,
    }
}
