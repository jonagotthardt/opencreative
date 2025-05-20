/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2025, McChicken Studio, mcchickenstudio@gmail.com
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

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;

import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.LimitReachedVariablesEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
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

import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static ua.mcchickenstudio.opencreative.coding.arguments.Argument.parseEntity;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getFileSize;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>WorldVariables</h1>
 * This class represents set of world variables. It includes
 * methods for finding and editing variables value.
 */
public final class WorldVariables {

    private final Planet planet;
    private final Set<WorldVariable> variables = new LinkedHashSet<>();

    public WorldVariables(Planet planet) {
        this.planet = planet;
    }

    public @Nullable WorldVariable getVariable(@NotNull VariableLink link, @NotNull Action action) {
        return getVariable(parseEntity(link.getName(),action.getHandler(),action),link.getVariableType(),action.getHandler().getMainActionHandler());
    }

    public @Nullable WorldVariable getVariable(@NotNull String name, @NotNull VariableLink.VariableType type, @Nullable ActionsHandler handler) {
        return variables.stream()
                .filter(var -> var.getName().equalsIgnoreCase(name))
                .filter(var -> type == var.getVarType())
                .filter(var -> type != VariableLink.VariableType.LOCAL || (handler != null && handler.equals(var.getHandler())))
                .findFirst()
                .orElse(null);
    }

    private boolean handleVariableValue(VariableLink link, ValueType type, Object value, ActionsHandler handler, Action action) {
        WorldVariable variable = (action != null) ? getVariable(link, action) : getVariable(link.getName(), link.getVariableType(), null);
        String valueString = value.toString().substring(0, Math.min(20, value.toString().length()));

        if (variable != null) {
            if (variable.getSize() + getTotalVariablesAmount() > planet.getLimits().getVariablesAmountLimit()) {
                sendCodingDebugLog(getPlanet(), "Reached limit of " + planet.getLimits().getVariablesAmountLimit() + " variables.");
                new LimitReachedVariablesEvent(planet).callEvent();
                return false;
            }
            variable.setType(type);
            variable.setValue(value);
        } else {
            if (getTotalVariablesAmount() > planet.getLimits().getVariablesAmountLimit()) {
                sendCodingDebugLog(getPlanet(), "Reached limit of " + planet.getLimits().getVariablesAmountLimit() + " variables.");
                new LimitReachedVariablesEvent(planet).callEvent();
                return false;
            }

            WorldVariable newVariable = (action != null)
                    ? new WorldVariable(parseEntity(link.getName(), action.getHandler(), action), link.getVariableType(), type, value, handler)
                    : new WorldVariable(link.getName(), link.getVariableType(), type, value, null);

            if (newVariable.getSize() + getTotalVariablesAmount() > planet.getLimits().getVariablesAmountLimit()) {
                sendCodingDebugLog(getPlanet(), "Reached limit of " + planet.getLimits().getVariablesAmountLimit() + " variables.");
                new LimitReachedVariablesEvent(planet).callEvent();
                return false;
            }
            variables.add(newVariable);
        }

        sendCodingDebugLog(getPlanet(), getLocaleMessage("coding-debug.variable." + (variable == null ? "created" : "set"), false)
                .replace("%variable%", action != null ? parseEntity(link.getName(), action.getHandler(), action) : link.getName())
                .replace("%value%", valueString));

        return true;
    }

    /**
     * Sets variable value to new specified one. Used in actions.
     * @param link variable link to set.
     * @param type new type of value.
     * @param value new value.
     * @param handler handler of setting.
     * @param action action of setting.
     */
    public void setVariableValue(VariableLink link, ValueType type, Object value, ActionsHandler handler, Action action) {
        handleVariableValue(link, type, value, handler, action);
    }

    /**
     * Sets variable value to new specified one.
     * @param link variable link to set.
     * @param type new type of value.
     * @param value new value.
     * @return true - if successfully set, false - failed.
     */
    public boolean setVariableValue(VariableLink link, ValueType type, Object value) {
        return handleVariableValue(link, type, value, null, null);
    }

    /**
     * Gets variable value by variable link.
     * @param link link for variable.
     * @param action action.
     * @return variable - if found, null - variable not exists.
     */
    public Object getVariableValue(VariableLink link, Action action) {
        WorldVariable variable = getVariable(link,action);
        return variable != null ? variable.getValue() : null;
    }

    /**
     * Removes variable by variable link.
     * @param link link for variable.
     * @param action action.
     */
    public void removeVariable(VariableLink link, Action action) {
        variables.removeIf(var -> var.equals(getVariable(link,action)));
    }

    public Set<WorldVariable> getSet() {
        return variables;
    }

    /**
     * Clears all current variables in world.
     */
    public void clearVariables() {
        variables.clear();
    }

    /**
     * Loads variables from /planet/variables.json file.
     */
    public void load() {
        long startTime = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Loading variables for planet " + planet.getId());

        clearVariables();
        File variablesJson = FileUtils.getPlanetVariablesJson(planet);
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
                if (variables.size() < planet.getLimits().getVariablesAmountLimit()) {
                    variables.add(new WorldVariable(name,VariableLink.VariableType.SAVED,type,value,null));
                }
            }
        } catch (Exception e) {
            sendCriticalErrorMessage("Failed to parse JSON file " + variablesJson.getPath(),e);
        }

        long endTime = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Loaded " + variables.size() + " variables for planet " + planet.getId() + " in " + (endTime - startTime) + " ms");
    }

    /**
     * Saves variables with type saved into /planet/variables.json file.
     */
    public void save() {
        long startTime = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Saving variables for planet " + planet.getId());

        File variablesJson = FileUtils.getPlanetVariablesJson(planet);
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

        long endTime = System.currentTimeMillis();
        long fileSize = getFileSize(variablesJson);

        OpenCreative.getPlugin().getLogger().info(
                "Saved " + jsonArray.size() + " variables for planet " + planet.getId()
                        + " in " + (endTime - startTime) + " ms"
                        + " (" + byteCountToDisplaySize(fileSize) + ")"
        );
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
            } else if (value instanceof Vector vector) {
                Map<String,Number> vectorMap = new HashMap<>();
                vectorMap.put("x",vector.getX());
                vectorMap.put("y",vector.getY());
                vectorMap.put("z",vector.getZ());
                return vectorMap;
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
                }
                return newMap;
            } else if (value instanceof Color color) {
                Map<String, Integer> colorMap = new HashMap<>();
                colorMap.put("red",color.getRed());
                colorMap.put("green",color.getGreen());
                colorMap.put("blue",color.getBlue());
                return colorMap;
            } else if (value instanceof Particle particle) {
                Map<String, String> particleMap = new HashMap<>();
                particleMap.put("type",particle.name());
                return particleMap;
            } else if (value instanceof EventValueLink link) {
                Map<String, String> valueMap = new HashMap<>();
                valueMap.put("name",link.type().name());
                return valueMap;
            } else if (value instanceof VariableLink link) {
                Map<String, String> variableMap = new HashMap<>();
                variableMap.put("name",link.getName());
                variableMap.put("type",link.getVariableType().name());
                return variableMap;
            }
        } catch (Exception e) {
            return String.valueOf(value);
        }
        return String.valueOf(value);
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
                return new Location(planet.getTerritory().getWorld(),x,y,z,yaw,pitch);
            } else if (type == ValueType.VECTOR) {
                double x, y, z;
                Map<?,?> vectorMap = (Map<?,?>) value;
                x = (Double) vectorMap.get("x");
                y = (Double) vectorMap.get("y");
                z = (Double) vectorMap.get("z");
                return new Vector(x,y,z);
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
            } else if (type == ValueType.COLOR) {
                int red,green, blue;
                Map<?,?> colorMap = (Map<?,?>) value;
                red = (int) colorMap.get("red");
                green = (int) colorMap.get("green");
                blue = (int) colorMap.get("blue");
                return Color.fromRGB(red,green,blue);
            } else if (type == ValueType.PARTICLE) {
                Map<?,?> particleMap = (Map<?,?>) value;
                String particleType = (String) particleMap.get("type");
                return Particle.valueOf(particleType);
            } else if (type == ValueType.EVENT_VALUE) {
                Map<?,?> eventValueMap = (Map<?,?>) value;
                String eventValueType = (String) eventValueMap.get("name");
                return EventValues.Variable.valueOf(eventValueType);
            } else if (type == ValueType.VARIABLE) {
                Map<?,?> varMap = (Map<?,?>) value;
                String varName = (String) varMap.get("name");
                return new VariableLink(varName, VariableLink.VariableType.SAVED);
            }
        } catch (Exception e) {
            return String.valueOf(value);
        }
        return String.valueOf(value);
    }

    /**
     * Returns total size of variables. It includes list and maps elements.
     * @return total size of variables.
     */
    public int getTotalVariablesAmount() {
        int size = 0;
        for (WorldVariable var : variables) {
            size += var.getSize();
        }
        return size;
    }

    public Planet getPlanet() {
        return planet;
    }

    /**
     * Clears local variables with action handler type.
     * @param actionsHandler handler.
     */
    public void garbageCollector(ActionsHandler actionsHandler) {
        variables.removeIf(var -> var.getVarType() == VariableLink.VariableType.LOCAL && var.getHandler() != null && var.getHandler().equals(actionsHandler));
    }
}
