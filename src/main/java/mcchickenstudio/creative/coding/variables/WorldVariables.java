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

package mcchickenstudio.creative.coding.variables;

import mcchickenstudio.creative.coding.blocks.actions.ActionsHandler;
import mcchickenstudio.creative.coding.variables.VariableLink;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.utils.FileUtils;
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

import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingDebugLog;
import static mcchickenstudio.creative.utils.ErrorUtils.sendCriticalErrorMessage;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

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

    public WorldVariable getVariable(VariableLink link) {
        return variables.stream()
                .filter(var -> var.getName().equalsIgnoreCase(link.getName()))
                .filter(var -> link.getVariableType() == var.getVarType())
                .filter(var -> link.getVariableType() != VariableLink.VariableType.LOCAL || link.getHandler().equals(var.getHandler()))
                .findFirst()
                .orElse(null);
    }

    public void setVariableValue(VariableLink link, ValueType type, Object value, ActionsHandler handler) {
        link.setHandler(handler.getMainActionHandler());
        WorldVariable variable = getVariable(link);
        String valueString = value.toString().substring(0, Math.min(20, value.toString().length()));
        if (variable != null) {
            if (variable.getSize() + getTotalVariablesAmount() > plot.getVariablesAmountLimit()) return;
            variable.setType(type);
            variable.setValue(value);
        } else {
            if (getTotalVariablesAmount() > plot.getVariablesAmountLimit()) return;
            variables.add(new WorldVariable(link.getName(), link.getVariableType(), type, value, handler));
        }
        sendCodingDebugLog(getPlot(),getLocaleMessage("plot-code-debug.variable." + (variable == null ? "created" : "set"),false).replace("%variable%",link.getName()).replace("%value%",valueString));

    }

    public Object getVariableValue(VariableLink link) {
        WorldVariable variable = getVariable(link);
        return variable != null ? variable.getValue() : null;
    }

    public void removeVariable(VariableLink link) {
        variables.removeIf(var -> var.equals(getVariable(link)));
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
                value = deserializeObject(value,ValueType.getByObject(value));
                if (variables.size() < plot.getVariablesAmountLimit()) {
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
            if (value instanceof ItemStack || value instanceof Location) {
                final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                final BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(arrayOutputStream);
                objectOutputStream.writeObject(value);
                value = Base64Coder.encodeLines(arrayOutputStream.toByteArray());
            } else if (value instanceof List<?> list) {
                List<Object> newList = new ArrayList<>();
                for (Object element : list) {
                    newList.add(serializeObject(element));
                }
                return newList;
            } else if (value instanceof Map<?,?> map) {
                Map<Object,Object> newMap = new HashMap<>();
                for (Object key : map.keySet()) {
                    key = serializeObject(key);
                    newMap.put(key,serializeObject(map.get(key)));
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
            if (type == ValueType.ITEM || type == ValueType.LOCATION) {
                final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines((String) value));
                final BukkitObjectInputStream objectInputStream = new BukkitObjectInputStream(arrayInputStream);
                value = objectInputStream.readObject();
            } else if (type == ValueType.LIST) {
                List<Object> newList = new ArrayList<>();
                List<?> oldList = (List<?>) value;
                for (Object element : oldList) {
                    newList.add(deserializeObject(element,ValueType.getByObject(element)));
                }
                return newList;
            } else if (type == ValueType.MAP) {
                Map<Object,Object> newMap = new HashMap<>();
                Map<?,?> oldMap = (Map<?,?>) value;
                for (Object key : oldMap.keySet()) {
                    key = deserializeObject(key,ValueType.getByObject(key));
                    newMap.put(key,deserializeObject(oldMap.get(key),ValueType.getByObject(oldMap.get(key))));
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
