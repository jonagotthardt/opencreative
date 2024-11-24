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

import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.plots.Plot;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static ua.mcchickenstudio.opencreative.coding.arguments.Argument.parseEntity;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>WorldVariables</h1>
 * This class represents set of world variables. It includes
 * methods for finding and editing variables value.
 */
public class WorldVariables {

    private final Plot plot;
    private final Set<WorldVariable> variables = new HashSet<>();

    public WorldVariables(Plot plot) {
        this.plot = plot;
    }

    public Set<WorldVariable> getSet() {
        return variables;
    }

    public WorldVariable getVariable(VariableLink link, Action action) {
        return getVariable(parseEntity(link.getName(),action.getHandler(),action),link.getVariableType(),link.getHandler());
    }

    public WorldVariable getVariable(String name, VariableLink.VariableType type, ActionsHandler handler) {
        return variables.stream()
                .filter(var -> var.getName().equalsIgnoreCase(name))
                .filter(var -> type == var.getVarType())
                .filter(var -> type != VariableLink.VariableType.LOCAL || handler.equals(var.getHandler()))
                .findFirst()
                .orElse(null);
    }

    public void setVariableValue(VariableLink link, ValueType type, Object value, ActionsHandler handler, Action action) {
        link.setHandler(handler.getMainActionHandler());
        WorldVariable variable = getVariable(link,action);
        String valueString = value.toString().substring(0, Math.min(20, value.toString().length()));
        if (variable != null) {
            if (variable.getSize() + getTotalVariablesAmount() > plot.getLimits().getVariablesAmountLimit()) {
                sendCodingDebugLog(getPlot(), "Reached limit of " + plot.getLimits().getVariablesAmountLimit() + " variables.");
                return;
            }
            variable.setType(type);
            variable.setValue(value);
        } else {
            if (getTotalVariablesAmount() > plot.getLimits().getVariablesAmountLimit()) {
                sendCodingDebugLog(getPlot(), "Reached limit of " + plot.getLimits().getVariablesAmountLimit() + " variables.");
                return;
            }
            WorldVariable newVariable = new WorldVariable(parseEntity(link.getName(),action.getHandler(),action), link.getVariableType(), type, value, handler);
            if (newVariable.getSize() + getTotalVariablesAmount() > plot.getLimits().getVariablesAmountLimit()) {
                sendCodingDebugLog(getPlot(), "Reached limit of " + plot.getLimits().getVariablesAmountLimit() + " variables.");
                return;
            }
            variables.add(newVariable);
        }
        sendCodingDebugLog(getPlot(),getLocaleMessage("plot-code-debug.variable." + (variable == null ? "created" : "set"),false).replace("%variable%", parseEntity(link.getName(),action.getHandler(),action)).replace("%value%",valueString));

    }

    public Object getVariableValue(VariableLink link, Action action) {
        WorldVariable variable = getVariable(link,action);
        return variable != null ? variable.getValue() : null;
    }

    public void removeVariable(VariableLink link, Action action) {
        variables.removeIf(var -> var.equals(getVariable(link,action)));
    }

    public void clearVariables() {
        variables.clear();
    }

    public void load() {
        clearVariables();
        File variablesJson = FileUtils.getPlotVariablesJson(plot);
        if (variablesJson == null || variablesJson.length() <= 2) {
            return;
        }
        try {
            JSONParser jsonParser = new JSONParser();
            JSONArray a = (JSONArray) jsonParser.parse(new FileReader(variablesJson));
            for (Object object : a) {
                JSONObject jsonObject = (JSONObject) object;
                String name = (String) jsonObject.get("name");
                ValueType type = ValueType.valueOf((String) jsonObject.get("type"));
                Object value = jsonObject.get("value");
                value = deserializeObject(value,type);
                if (variables.size() < plot.getLimits().getVariablesAmountLimit()) {
                    variables.add(new WorldVariable(name,VariableLink.VariableType.SAVED,type,value,null));
                }
            }
        } catch (Exception e) {
            sendCriticalErrorMessage("Failed to parse JSON file " + variablesJson.getPath(),e);
        }
    }

    public void save() {
        File variablesJson = FileUtils.getPlotVariablesJson(plot);
        if (variablesJson == null) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        try (FileWriter file = new FileWriter(variablesJson.getPath())) {
            Files.newBufferedWriter(variablesJson.toPath() , StandardOpenOption.TRUNCATE_EXISTING);
            for (WorldVariable worldVariable : variables) {
                if (worldVariable.getVarType() != VariableLink.VariableType.SAVED) {
                    continue;
                }
                JSONObject objItem = new JSONObject();
                objItem.put("name", worldVariable.getName());
                objItem.put("type", worldVariable.getType().name());
                Object value = worldVariable.getValue();
                value = serializeObject(value);
                objItem.put("value", value);
                jsonArray.add(objItem);
            }
            file.write(jsonArray.toString());
        } catch (Exception e){
            sendCriticalErrorMessage("Failed to save variables",e);
        }
    }

    private Object serializeObject(Object value) {
        try {
            if (value instanceof ItemStack) {
                final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                final BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(arrayOutputStream);
                objectOutputStream.writeObject(value);
                value = Base64Coder.encodeLines(arrayOutputStream.toByteArray());
            } else if (value instanceof Location location) {
                Map<String,Number> locationMap = new HashMap<>();
                locationMap.put("x",location.getX());
                locationMap.put("y",location.getY());
                locationMap.put("z",location.getZ());
                locationMap.put("yaw",location.getYaw());
                locationMap.put("pitch",location.getPitch());
                return locationMap;
            } else if (value instanceof List<?> list) {
                List<Object> newList = new ArrayList<>();
                for (Object element : list) {
                    Map<String,Object> parsedElement = new HashMap<>();
                    parsedElement.put("type",ValueType.getByObject(element).name());
                    parsedElement.put("value",serializeObject(element));
                    newList.add(parsedElement);
                }
                return newList;
            } else if (value instanceof Map<?,?> map) {
                Map<Object,Object> newMap = new HashMap<>();
                for (Object key : map.keySet()) {
                    Map<String,Object> newKey = new HashMap<>();
                    newKey.put("type",ValueType.getByObject(key).name());
                    newKey.put("value",serializeObject(key));
                    Map<String,Object> newValue = new HashMap<>();
                    newValue.put("type",ValueType.getByObject(map.get(key)).name());
                    newValue.put("value",serializeObject(map.get(key)));
                    String serializedKey = new JSONObject(newKey).toString();
                    newMap.put(serializedKey, newValue);
                    //newMap.put(newKey,newValue);
                }
                return newMap;
            }
        } catch (Exception e) {
            return value;
        }
        return value;
    }

    private Object deserializeObject(Object value, ValueType type) {
        try {
            if (type == ValueType.ITEM) {
                final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines((String) value));
                final BukkitObjectInputStream objectInputStream = new BukkitObjectInputStream(arrayInputStream);
                value = objectInputStream.readObject();
            } else if (type == ValueType.LOCATION) {
                double x, y, z;
                float yaw, pitch;
                Map<?,?> locationMap = (Map<?,?>) value;
                x = (Double) locationMap.get("x");
                y = (Double) locationMap.get("y");
                z = (Double) locationMap.get("z");
                yaw = ((Double) locationMap.get("yaw")).floatValue();
                pitch = ((Double) locationMap.get("pitch")).floatValue();
                return new Location(plot.getTerritory().getWorld(),x,y,z,yaw,pitch);
            } else if (type == ValueType.LIST) {
                List<Object> newList = new ArrayList<>();
                List<?> oldList = (List<?>) value;
                for (Object element : oldList) {
                    Object newElement = element;
                    if (newElement instanceof Map<?,?> insideMap && insideMap.containsKey("type") && insideMap.containsKey("value")) {
                        ValueType keyType = ValueType.parseString(insideMap.get("type").toString());
                        newElement = deserializeObject(insideMap.get("value"),keyType);
                    } else {
                        newElement = deserializeObject(newElement,ValueType.getByObject(newElement));
                    }
                    newList.add(newElement);
                }
                return newList;
            } else if (type == ValueType.MAP) {
                Map<Object,Object> newMap = new HashMap<>();
                Map<?,?> oldMap = (Map<?,?>) value;
                for (Object key : oldMap.keySet()) {
                    Object newKey = key;
                    Object newValue = oldMap.get(key);
                    Map<String, Object> deserializedKey = (Map<String, Object>) new JSONParser().parse((String) newKey);
                    newKey = deserializeObject(deserializedKey.get("value"), ValueType.parseString(deserializedKey.get("type").toString()));
                    if (newValue instanceof Map<?,?> insideMap && insideMap.containsKey("type") && insideMap.containsKey("value")) {
                        ValueType keyValueType = ValueType.parseString(insideMap.get("type").toString());
                        newValue = deserializeObject(insideMap.get("value"),keyValueType);
                    } else {
                        newValue = deserializeObject(key,ValueType.getByObject(key));
                    }
                    newMap.put(newKey,newValue);
                }
                return newMap;
            }
        } catch (Exception e) {
            return value;
        }
        return value;
    }

    public int getTotalVariablesAmount() {
        int size = 0;
        for (WorldVariable var : variables) {
            size += var.getValue() instanceof List ? ((List<?>) var.getValue()).size() : 1;
        }
        return size;
    }

    public Plot getPlot() {
        return plot;
    }

    public void garbageCollector(ActionsHandler actionsHandler) {
        variables.removeIf(var -> var.getVarType() == VariableLink.VariableType.LOCAL && var.getHandler() != null && var.getHandler().equals(actionsHandler));
    }
}
