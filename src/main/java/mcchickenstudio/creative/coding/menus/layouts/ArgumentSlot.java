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

package mcchickenstudio.creative.coding.menus.layouts;

import mcchickenstudio.creative.coding.blocks.variables.VariableType;

public class ArgumentSlot {

    private final String path;
    private final VariableType varType;
    private final byte minParameter;
    private final byte maxParameter;
    private final byte listSize;
    private final boolean acceptEmptyItems;

    public ArgumentSlot(String path, VariableType varType) {
        this.varType = varType;
        this.minParameter = 0;
        this.maxParameter = 0;
        this.listSize = 1;
        this.path = path;
        this.acceptEmptyItems = false;
    }

    public ArgumentSlot(String path, VariableType varType, byte listSize) {
        this.varType = varType;
        this.minParameter = 0;
        this.maxParameter = 0;
        this.listSize = listSize;
        this.path = path;
        this.acceptEmptyItems = false;
    }

    public ArgumentSlot(String path, VariableType varType, byte listSize, boolean acceptEmptyItems) {
        this.varType = varType;
        this.minParameter = 0;
        this.maxParameter = 0;
        this.listSize = listSize;
        this.path = path;
        this.acceptEmptyItems = acceptEmptyItems;
    }

    public ArgumentSlot(String path, byte minParam, byte maxParam) {
        this.varType = VariableType.PARAMETER;
        this.minParameter = minParam;
        this.maxParameter = maxParam;
        this.listSize = 1;
        this.path = path;
        this.acceptEmptyItems = false;
    }

    public byte getMinParameter() {
        return minParameter;
    }

    public byte getMaxParameter() {
        return maxParameter;
    }

    public VariableType getVarType() {
        return varType;
    }

    public boolean isList() {
        return getListSize() > 1;
    }

    public boolean isParameter() {
        return (getMinParameter() != 0 && getMaxParameter() != 0);
    }

    public boolean acceptEmptyItems() {
        return acceptEmptyItems;
    }

    public byte getListSize() {
        return listSize;
    }

    public String getPath() {
        return path;
    }

    public final boolean isItemStack() {
        return varType == VariableType.ITEM;
    }
}
