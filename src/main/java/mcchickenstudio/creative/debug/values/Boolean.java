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

package mcchickenstudio.creative.debug.values;

import mcchickenstudio.creative.coding.variables.ValueType;

public class Boolean implements CodingValue {

    private boolean value;
    private final ValueType type = ValueType.TEXT;

    public Boolean(boolean value) {
        this.value = value;
    }

    @Override
    public ValueType getType() {
        return ValueType.BOOLEAN;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof java.lang.Boolean b) {
            this.value = b;
        }
    }

    @Override
    public java.lang.Boolean getValue(boolean deep) {
        return value;
    }

    @Override
    public Object serialize() {
        return value;
    }
}
