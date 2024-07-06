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

public class Text implements CodingValue {

    private String string;
    private final ValueType type = ValueType.TEXT;

    public Text(String string) {
        this.string = string;
    }

    @Override
    public ValueType getType() {
        return null;
    }

    @Override
    public void setValue(Object value) {
        this.string = value.toString();
    }

    @Override
    public String getValue(boolean deep) {
        return string;
    }

    @Override
    public Object serialize() {
        return string;
    }

    public String getString() {
        return string;
    }
}
