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

/**
 * <h1>Number</h1>
 * This class represents Number value, that stores double.
 * It has methods to get integer, float and long values also.
 */
public class Number implements CodingValue {

    private double number;
    private final ValueType type = ValueType.NUMBER;

    public Number(double number) {
        this.number = number;
    }

    public Number(float number) {
        this.number = number;
    }

    public Number(int number) {
        this.number = number;
    }

    public Number(long number) {
        this.number = number;
    }

    @Override
    public ValueType getType() {
        return ValueType.NUMBER;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Double d) {
            this.number = d;
        } else if (value instanceof Integer i) {
            this.number = i.doubleValue();
        } else if (value instanceof Float f) {
            this.number = f.doubleValue();
        } else if (value instanceof Long l) {
            this.number = l.doubleValue();
        } else if (value instanceof String s) {
            try {
                this.number = Double.parseDouble(s);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public Double getValue(boolean deep) {
        return number;
    }

    @Override
    public Object serialize() {
        return number;
    }

    public double getDouble() {
        return number;
    }

    public int getInt() {
        return ((Double) number).intValue();
    }

    public long getLong() {
        return ((Double) number).longValue();
    }

    public float getFloat() {
        return ((Double) number).floatValue();
    }
}
