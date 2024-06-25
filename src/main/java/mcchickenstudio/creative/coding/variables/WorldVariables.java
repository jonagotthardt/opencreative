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

package mcchickenstudio.creative.coding.variables;

import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.utils.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCriticalErrorMessage;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.MessageUtils.sendMessageOnce;

public class WorldVariables {

    private final Plot plot;
    private final Set<WorldVariable> worldVariables = new HashSet<>();

    public WorldVariables(Plot plot) {
        this.plot = plot;
    }

    public void clear() {
        worldVariables.clear();
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
            worldVariables.add(new WorldVariable(name, ValueType.parseString(type),value));
        }
    }

    public void save() {
        if (worldVariables.isEmpty()) {
            return;
        }
        YamlConfiguration config = FileUtils.getPlotVariablesConfig(plot);
        if (config == null) {
            return;
        }
        ConfigurationSection section = config.getConfigurationSection("variables");
        if (section == null) {
            config.createSection("variables");
            section = config.getConfigurationSection("variables");
        }
        for (WorldVariable worldVariable : worldVariables) {
            if (section == null) return;
            Map<String, Object> data = new HashMap<>();
            data.put("type",worldVariable.getType().name());
            data.put("value",(worldVariable.getValue() instanceof ItemStack ? ((ItemStack) worldVariable.getValue()).serialize() : worldVariable.getValue()));
            section.set(worldVariable.getName().toLowerCase(),data);
            System.out.println("saved " + worldVariable.getName() + " " + worldVariable.getType() + " " + worldVariable.getValue()) ;
        }
        try {
            config.save(FileUtils.getPlotVariablesFile(plot));
        } catch (IOException e) {
            sendCriticalErrorMessage("Couldn't not save variables in plot " + plot.worldName);
        }
    }

    public void setVarValue(String name, ValueType type, Object value) {
        long size = (value instanceof List) ? ((List<?>) value).size() : 1;
        if (size > plot.getVariablesAmountLimit()) return;
        for (WorldVariable var : worldVariables) {
            long varSize = (var.getValue() instanceof List) ? ((List<?>) var.getValue()).size() : 1;
            size += varSize;
            if (size > plot.getVariablesAmountLimit()) return;
            if (var.getName().equalsIgnoreCase(name)) {
                var.setType(type);
                var.setValue(value);
                return;
            }
        }
        worldVariables.add(new WorldVariable(name, type, value));
    }

    public Object getVarValue(VariableLink link) {
        for (WorldVariable var : worldVariables) {
            if (var.getName().equalsIgnoreCase(link.getName())) {
                return var.getValue();
            }
        }
        return null;
    }

    public Object getVarValue(String name) {
        for (WorldVariable var : worldVariables) {
            if (var.getName().equalsIgnoreCase(name)) {
                return var.getValue();
            }
        }
        return null;
    }

    public boolean varExists(String name) {
        for (WorldVariable var : worldVariables) {
            if (var.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public Plot getPlot() {
        return plot;
    }
}
