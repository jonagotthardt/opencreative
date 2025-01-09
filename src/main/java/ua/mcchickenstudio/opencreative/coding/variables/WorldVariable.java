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

package ua.mcchickenstudio.opencreative.coding.variables;

import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.List;
import java.util.Map;

/**
 * <h1>WorldVariable</h1>
 * This class represents a variable in world, that
 * has type and stores value.
 *
 * <p>Variables can be <b>local, global, saved</b>.
 * <p>Local - stores only in ActionsHandler
 * <p>Global - stores in world while it's loaded
 * <p>Saved - stores in world forever
 */
public class WorldVariable {

    private final String name;
    private Object value;
    private ValueType valueType;
    private final VariableLink.VariableType varType;
    private final ActionsHandler handler;

    public WorldVariable(String name, VariableLink.VariableType varType, ValueType type, Object value, ActionsHandler handler) {
        this.name = name;
        this.valueType = type;
        this.varType = varType;
        this.value = value;
        this.handler = handler;
    }

    public ActionsHandler getHandler() {
        return handler;
    }

    public final Object getValue() {
        return value;
    }

    public final ValueType getType() {
        return valueType;
    }

    public VariableLink.VariableType getVarType() {
        return varType;
    }

    public String getName() {
        return name;
    }

    public void setType(ValueType type) {
        this.valueType = type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getSize() {
        int size = 1;
        if (value instanceof List<?> list) {
            size += list.size();
        } else if (value instanceof Map<?,?> map) {
            size += 2*map.size();
        }
        return size;
    }
}
