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

package mcchickenstudio.creative.coding.arguments;

import mcchickenstudio.creative.coding.variables.ValueType;
import mcchickenstudio.creative.coding.variables.VariableLink;
import mcchickenstudio.creative.plots.Plot;

import java.util.List;

public class Argument {

    protected final Plot plot;
    protected final String path;
    protected final ValueType type;
    protected final Object value;

    public Argument(Plot plot, ValueType type, String path, Object value) {
        this.plot = plot;
        this.path = path;
        this.value = value;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public ValueType getType() {
        return type;
    }

    public Object getValue() {
        if (value instanceof VariableLink) {
            VariableLink link = (VariableLink) value;
            Object variableValue = plot.getWorldVariables().getVariableValue(link);
            if (variableValue != null) {
                return variableValue;
            }
        }
        return value;
    }

    public boolean isList() {
        return (this.type == ValueType.LIST && getValue() instanceof List);
    }

    @Override
    public String toString() {
        return "Argument. Name: " + path + ", Type: " + type.name() + ", Value: " + value.toString();
    }
}
