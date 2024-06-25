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

package mcchickenstudio.creative.coding.blocks.actions.variableactions;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.variables.ValueType;
import mcchickenstudio.creative.coding.variables.VariableLink;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public abstract class VariableAction extends Action {

    public VariableAction(Executor executor, int x, Arguments args) {
        super(executor, x, args);
    }

    @Override
    public ActionCategory getActionCategory() {
        return ActionCategory.VARIABLE_ACTION;
    }

    protected void setVarValue(VariableLink link, Object value) {
        if (link != null) {
            ValueType type = ValueType.getByObject(value);
            if (type == null) {
                type = ValueType.TEXT;
            }
            getPlot().getWorldVariables().setVarValue(link.getName(), type, value);
        }
    }

    protected Object parseItemValue(ItemStack item, boolean isItemStack) {
        if (isItemStack) {
            return item.serialize();
        }
        if (!item.hasItemMeta()) return "";
        if (item.getItemMeta().displayName() == null) return "";
        String name = item.getItemMeta().getDisplayName();
        switch(item.getType()) {
            case SLIME_BALL:
                return ChatColor.stripColor(name);
            case BOOK:
                return name;
            case CLOCK:
                return ChatColor.stripColor(name);
            case MAGMA_CREAM:
                VariableLink link = new VariableLink(ChatColor.stripColor(name));
                Object variableValue = getPlot().getWorldVariables().getVarValue(link);
                if (variableValue != null) {
                    return variableValue;
                }
                return link;
            case PAPER:
                String locationString = ChatColor.stripColor(name);
                String[] locCoords = locationString.split(" ");
                double x,y,z;
                float yaw,pitch;
                if (locCoords.length == 5) {
                    try {
                        x = Double.parseDouble(locCoords[0]);
                        y = Double.parseDouble(locCoords[1]);
                        z = Double.parseDouble(locCoords[2]);
                        yaw = Float.parseFloat(locCoords[3]);
                        pitch = Float.parseFloat(locCoords[4]);
                        return new Location(getExecutor().getPlot().world,x,y,z,yaw,pitch);
                    } catch (Exception error) {
                        return getExecutor().getPlot().world.getSpawnLocation();
                    }
                }
            default:
                return name;
        }
    }
}
