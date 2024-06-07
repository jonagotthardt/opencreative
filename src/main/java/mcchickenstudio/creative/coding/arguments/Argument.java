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

import mcchickenstudio.creative.coding.blocks.variables.VariableType;

import java.util.List;

public class Argument {

    protected final String path;
    protected final VariableType type;
    protected final Object value;

    public Argument(VariableType type, String path, Object value) {
        this.path = path;
        this.value = value;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public VariableType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public boolean isList() {
        return (this.type == VariableType.LIST && getValue() instanceof List);
    }

    @Override
    public String toString() {
        return "Argument. Name: " + path + ", Type: " + type.name() + ", Value: " + value.toString();
    }
}
