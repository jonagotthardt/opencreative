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

package mcchickenstudio.creative.coding.blocks.variables;

import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.utils.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashSet;
import java.util.Set;

public class WorldVariables {

    private final Plot plot;
    private final Set<Variable> variables = new HashSet<>();

    WorldVariables(Plot plot) {
        this.plot = plot;
    }

    public void load() {
        YamlConfiguration config = FileUtils.getPlotVariablesConfig(plot);
        if (config == null) return;
        ConfigurationSection section = FileUtils.getPlotVariablesConfig(plot).getConfigurationSection("variables");
        if (section == null) return;
        String path = section.getCurrentPath() ;
        for (String key : section.getKeys(false)) {
            String name = key.replace(path+".","");
            String valueString = config.getString(key);
            if (valueString == null) return;
            String type = valueString.split(":",1)[0];
            String value = valueString.split(":",1)[1];
            variables.add(new Variable(name,VariableType.parseString(type),value));
        }
    }

    public Object getVarValue(String name) {
        for (Variable var : variables) {
            if (var.getName().equalsIgnoreCase(name)) return var.getValue();
        }
        return null;
    }

    public boolean varExists(String name) {
        for (Variable var : variables) {
            if (var.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public Plot getPlot() {
        return plot;
    }
}
