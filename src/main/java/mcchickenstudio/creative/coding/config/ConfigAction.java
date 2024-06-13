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

package mcchickenstudio.creative.coding.config;

import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.variables.VariableType;
import mcchickenstudio.creative.coding.menus.layouts.ArgumentSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigAction {

    protected final List<ConfigArgument> arguments = new ArrayList<>();
    protected final ActionCategory category;
    protected final ActionType type;

    ConfigAction(ActionCategory category, ActionType type) {
        this.category = category;
        this.type = type;
    }

    public ActionType getType() {
        return type;
    }

    public void addArgument(String path, VariableType type, Object value) {
        System.out.println("Adding argument " + path + " " + type + " " + value);
        arguments.add(new ConfigArgument(path,type,value));
    }

    public void addArgument(ArgumentSlot slot, Object value) {
        System.out.println("Adding argument argslot " + slot.getPath() + " " + slot.getVarType() + " " + value);
        arguments.add(new ConfigArgument(slot.getPath(),slot.getVarType(),value));
    }


}
