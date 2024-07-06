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
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.events.EventValues;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.variables.ValueType;
import mcchickenstudio.creative.debug.values.EventValueLink;
import mcchickenstudio.creative.debug.values.VariableLink;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static mcchickenstudio.creative.coding.arguments.Argument.parseEntity;

public abstract class VariableAction extends Action {

    public VariableAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public ActionCategory getActionCategory() {
        return ActionCategory.VARIABLE_ACTION;
    }

    protected void setVarValue(VariableLink link, Object value) {
        if (link != null) {
            link.setName(parseEntity(link.getName(),this));
            link.setHandler(getHandler().getMainActionHandler());
            ValueType type = ValueType.getByObject(value);
            if (type == null) {
                type = ValueType.TEXT;
            }
            getPlot().getWorldVariables().setVariableValue(link, type, value, getHandler().getMainActionHandler());
        }
    }

    protected Object parseItemValue(ItemStack item) {
        if (!item.hasItemMeta()) return "";
        if (item.getItemMeta().displayName() == null) return "";
        String name = item.getItemMeta().getDisplayName();
        switch(item.getType()) {
            case NAME_TAG: {
                List<String> lore = item.getItemMeta().getLore();
                if (lore != null && !lore.isEmpty()) {
                    if (lore.get(0).startsWith("oc.lang.menus.developer.variables.items.event-value")) {
                        name = ChatColor.stripColor(name);
                        EventValues.Variable varType;
                        try {
                            varType = EventValues.Variable.valueOf(name.toUpperCase());
                        } catch (Exception e) {
                            return null;
                        }
                        if (getHandler().hasTempVariable(varType)) {
                            return getHandler().getVarValue(varType);
                        }
                        return new EventValueLink(varType, getExecutor());
                    }
                }
            }
            case SLIME_BALL:
                return ChatColor.stripColor(name);
            case BOOK:
                return parseEntity(name,this);
            case CLOCK:
                return ChatColor.stripColor(name);
            case MAGMA_CREAM:
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.getLore();
                VariableLink.VariableType type = VariableLink.VariableType.LOCAL;
                if (lore != null && !lore.isEmpty()) {
                    String loreLine = lore.get(0);
                    if (loreLine.equals("oc.lang.items.developer.variable.saved")) {
                        type = VariableLink.VariableType.SAVED;
                    } else if (loreLine.equals("oc.lang.items.developer.variable.global")) {
                        type = VariableLink.VariableType.GLOBAL;
                    }
                }
                VariableLink link = new VariableLink(ChatColor.stripColor(name),type);
                Object variableValue = getPlot().getWorldVariables().getVariableValue(link);
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
                return "";
        }
    }
}
