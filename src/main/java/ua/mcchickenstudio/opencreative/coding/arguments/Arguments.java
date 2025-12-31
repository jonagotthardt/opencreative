/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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

package ua.mcchickenstudio.opencreative.coding.arguments;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.EventValueLink;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.BlockUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Pattern;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;

/**
 * <h1>Arguments</h1>
 * This class represents an arguments holder, that
 * stores arguments for actions and conditions.
 * @see Argument
 */
public class Arguments {

    private final @NotNull Planet planet;
    private final @NotNull Executor executor;
    private final @NotNull List<Argument> argumentList = new ArrayList<>();

    private final static Pattern INT_PATTERN = Pattern.compile("^-?[0-9]*$");
    private final static Pattern FLOAT_PATTERN = Pattern.compile("^-?[0-9]*\\.?[0-9]+$");

    public Arguments(@NotNull Planet planet, @NotNull Executor executor) {
        this.planet = planet;
        this.executor = executor;
    }

    public final void load(ConfigurationSection section) {
        for (String path : section.getKeys(false)) {
            Argument arg = loadArgument(section,path);
            if (arg != null) argumentList.add(arg);
        }
    }

    private Argument loadArgument(ConfigurationSection section, String name) {
        String configType = section.getString(name+".type");
        Object configValue = section.get(name+".value");
        if (configType == null || configType.isEmpty() || configValue == null) {
            return null;
        }
        ValueType type = ValueType.parseString(configType.toUpperCase());
        Object value = parseValue(section,name,type,configValue);
        return new Argument(planet,type,name,value);
    }

    private @NotNull Object parseValue(ConfigurationSection section, String name, ValueType type, Object configValue) {
        String stringValue = configValue.toString();
        ConfigurationSection listSection = section.getConfigurationSection(name + ".value");
        switch (type) {
            case LIST:
                List<Argument> arguments = new ArrayList<>();
                if (listSection == null) {
                    return arguments;
                }
                for (String key : listSection.getKeys(false)) {
                    Argument argument = loadArgument(listSection, key);
                    if (argument != null) arguments.add(argument);
                }
                return arguments;
            case LOCATION:
                double x,y,z;
                float yaw,pitch;
                if (listSection == null) {
                    return planet.getTerritory().getSpawnLocation();
                }
                x = listSection.getDouble("x");
                y = listSection.getDouble("y");
                z = listSection.getDouble("z");
                yaw = (float) listSection.getDouble("yaw");
                pitch = (float) listSection.getDouble("pitch");
                return new Location(planet.getTerritory().getWorld(),x,y,z,yaw,pitch);
            case VECTOR:
                if (listSection == null) {
                    return new Vector(0,0,0);
                }
                x = listSection.getDouble("x");
                y = listSection.getDouble("y");
                z = listSection.getDouble("z");
                return new Vector(x,y,z);
            case COLOR:
                int r,g,b;
                if (listSection == null) {
                    return Color.WHITE;
                }
                r = listSection.getInt("red");
                g = listSection.getInt("blue");
                b = listSection.getInt("green");
                if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
                    return Color.fromRGB(r,g,b);
                } else {
                    return Color.WHITE;
                }
            case VARIABLE: {
                if (listSection == null) {
                    return stringValue;
                }
                String varName = listSection.getString("name","");
                String typeString = listSection.getString("type");
                VariableLink.VariableType varType = VariableLink.VariableType.getEnum(typeString);
                if (varType == null) {
                    varType = VariableLink.VariableType.GLOBAL;
                }
                return new VariableLink(varName,varType);
            }
            case EVENT_VALUE: {
                if (listSection == null) {
                    return stringValue;
                }
                String valueType = listSection.getString("name");
                String targetType = listSection.getString("target","selected");
                if (valueType == null) return stringValue;
                if (valueType.isEmpty()) return stringValue;
                Target target = Target.getByText(targetType);
                try {
                    if (valueType.startsWith("PLOT")) {
                        valueType = valueType.replace("PLOT","PLANET");
                    }
                    return new EventValueLink(valueType, target);
                } catch (Exception e) {
                    return stringValue;
                }
            }
            case NUMBER:
                if (INT_PATTERN.matcher(stringValue).matches()) {
                    return Integer.parseInt(stringValue);
                } else if (FLOAT_PATTERN.matcher(stringValue).matches()) {
                    return Float.parseFloat(stringValue);
                }
                return 0;
            case BOOLEAN:
                return Boolean.parseBoolean(stringValue);
            case PARAMETER:
                return Float.parseFloat(stringValue);
            case ITEM:
                if (listSection == null) {
                    return new ItemStack(Material.AIR);
                }
                return ItemStack.deserialize(listSection.getValues(false));
            case PARTICLE:
                if (listSection == null) {
                    return stringValue;
                }
                String typeString = listSection.getString("type");
                if (typeString == null || typeString.isEmpty()) return stringValue;
                try {
                    return Particle.valueOf(typeString);
                } catch (Exception e) {
                    return typeString;
                }
            default:
                return stringValue;
        }
    }

    public final boolean pathExists(@NotNull String path) {
        for (Argument argument : argumentList) {
            if (argument.getPath().equals(path)) return true;
        }
        return false;
    }

    private @Nullable Argument getArg(@NotNull String path) {
        for (Argument argument : argumentList) {
            if (argument.getPath().equals(path)) {
                return argument;
            }
        }
        return null;
    }

    public final @NotNull Map<Object, Object> getMap(@NotNull String path, @NotNull Action action) {
        Map<Object, Object> map = new LinkedHashMap<>();
        Argument arg = getArg(path);
        if (arg == null) {
            return new LinkedHashMap<>();
        }
        try {
            Object value = arg.getValue(action);
            if (value instanceof Map<?, ?> rawMap) {
                map.putAll(rawMap);
            }
        } catch(ClassCastException e) {
            map = new LinkedHashMap<>();
        }
        sendCodingDebugVariable(planet, path, map);
        return map;
    }

    @SuppressWarnings("unchecked")
    public final <T> List<T> getList(@NotNull String path, @NotNull Action action) {
        List<T> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null) {
            try {
                if (arg.isList()) {
                    List<Argument> args = (List<Argument>) arg.getValue(action);
                    for (Argument argument : args) {
                        list.add((T) (argument.getValue(action)));
                    }
                } else if (arg.getValue(action) instanceof List<?>) {
                    List<Object> argList = (List<Object>) arg.getValue(action);
                    for (Object object : argList) {
                        list.add((T) object);
                    }
                }
            } catch(ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(planet,path,list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull List<VariableLink> getVarLinksList(@NotNull String path, @NotNull Action action) {
        List<VariableLink> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null && arg.isList()) {
            try {
                List<Argument> args = (List<Argument>) arg.getValue(action);
                for (Argument argument : args) {
                    if (argument.value instanceof VariableLink link) {
                        list.add(link);
                    }
                }
            } catch (ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(planet,path,list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull List<Boolean> getBooleanList(@NotNull String path, @NotNull Action action) {
        List<Boolean> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null && arg.isList()) {
            try {
                List<Argument> args = (List<Argument>) arg.getValue(action);
                for (Argument textArg : args) {
                    Object textObject = textArg.getValue(action);
                    if (textObject instanceof Boolean bool) {
                        list.add(bool);
                    } else {
                        String textString = textObject.toString();
                        list.add(Boolean.parseBoolean(textString));
                    }
                }
            } catch (ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(planet,path,list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull List<String> getTextList(@NotNull String path, @NotNull Action action) {
        List<String> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null && arg.isList()) {
            try {
                List<Argument> args = (List<Argument>) arg.getValue(action);
                for (Argument textArg : args) {
                    Object textObject = textArg.getValue(action);
                    String textString = textObject.toString();
                    if (textObject instanceof ItemStack item) {
                        if (item.hasItemMeta() && item.getItemMeta() != null) {
                            textString = item.getItemMeta().getDisplayName();
                        } else {
                            textString = item.getType().name();
                        }
                    }
                    list.add(Argument.parseEntity(textString,action.getHandler().getMainActionHandler(),action));
                }
            } catch (ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(planet,path,list);
        return list;
    }

    public final @NotNull List<Component> getComponentList(@NotNull String path, @NotNull Action action) {
        List<Component> list = new ArrayList<>();
        List<String> texts = getTextList(path,action);
        for (String text : texts) {
            list.add(textToComponent(text));
        }
        sendCodingDebugVariable(planet,path,list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull List<Double> getNumbersList(@NotNull String path, @NotNull Action action) {
        List<Double> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null && arg.isList()) {
            try {
                List<Argument> args = (List<Argument>) arg.getValue(action);
                for (Argument numberArg : args) {
                    Object object = numberArg.getValue(action);
                    list.add(parseObject(object,0.0d));
                }
            } catch (ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(planet,path,list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull List<ItemStack> getItemList(@NotNull String path, @NotNull Action action) {
        List<ItemStack> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null && arg.isList()) {
            try {
                List<Argument> args = (List<Argument>) arg.getValue(action);
                for (Argument itemArg : args) {
                    list.add((ItemStack) itemArg.getValue(action));
                }
            } catch (ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(planet,path,list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull List<Location> getLocationList(@NotNull String path, @NotNull Action action) {
        List<Location> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null && arg.isList()) {
            try {
                List<Argument> args = (List<Argument>) arg.getValue(action);
                for (Argument itemArg : args) {
                    if (itemArg.getValue(action) instanceof Location loc) {
                        loc.setWorld(planet.getTerritory().getWorld());
                        if (!BlockUtils.isOutOfBorders(loc)) {
                            list.add(loc);
                        }
                    }
                }
            } catch (ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(planet,path,list);
        return list;
    }

    /**
     * Returns variable link stored in arguments.
     * @param path key of argument, that stores boolean value.
     * @param action action, that asks for variable link.
     * @return variable link, or null - if not found.
     */
    public @Nullable VariableLink getVariableLink(@NotNull String path, @NotNull Action action) {
        Argument arg = getArg(path);
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
            return null;
        }
        if (arg.value instanceof VariableLink link) {
            sendCodingDebugVariable(planet,path,link);
            return link;
        }
        sendCodingDebugNotFoundVariable(planet,path);
        return null;
    }

    /**
     * Returns material value stored in arguments.
     * @param path key of argument, that stores material value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public @NotNull Material getMaterial(@NotNull String path, @NotNull Material defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
            return defaultValue;
        }
        if (arg.getValue(action) instanceof ItemStack item) {
            sendCodingDebugVariable(planet,path,item.getType());
            return item.getType();
        }
        if (arg.getValue(action) instanceof String text) {
            Material material = Material.getMaterial(text.toUpperCase());
            if (material != null) {
                sendCodingDebugVariable(planet,path,material);
                return material;
            }
        }
        if (arg.getValue(action) instanceof Block block) {
            sendCodingDebugVariable(planet,path,block.getType());
            return block.getType();
        }
        if (arg.getValue(action) instanceof Location location) {
            sendCodingDebugVariable(planet,path,location.getBlock().getType());
            return location.getBlock().getType();
        }
        sendCodingDebugNotFoundVariable(planet,path);
        return defaultValue;
    }

    /**
     * Returns item value stored in arguments.
     * @param path key of argument, that stores item value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public @NotNull ItemStack getItem(@NotNull String path, @NotNull ItemStack defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
            return defaultValue;
        }
        if (arg.getValue(action) instanceof ItemStack) {
            sendCodingDebugVariable(planet,path,arg.getValue(action));
            return (ItemStack) arg.getValue(action);
        }
        sendCodingDebugNotFoundVariable(planet,path);
        return defaultValue;
    }

    /**
     * Returns boolean value stored in arguments.
     * @param path key of argument, that stores boolean value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public boolean getBoolean(@NotNull String path, boolean defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        boolean value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
        } else {
            value = parseObject(arg.getValue(action),defaultValue);
            sendCodingDebugVariable(planet,path,value);
        }
        return value;
    }

    public Object getValue(String path, Action action) {
        Argument arg = getArg(path);
        Object value = "";
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
        } else {
            value = arg.getValue(action);
            sendCodingDebugVariable(planet,path,value);
        }
        return value;
    }

    /**
     * Returns integer number value stored in arguments.
     * @param path key of argument, that stores integer number value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public int getInt(@NotNull String path, int defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        int value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
        } else {
            value = parseObject(arg.getValue(action),defaultValue);
            sendCodingDebugVariable(planet,path,value);
        }
        return value;
    }

    /**
     * Returns long number value stored in arguments.
     * @param path key of argument, that stores long number value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public long getLong(@NotNull String path, long defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        long value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
        } else {
            value = parseObject(arg.getValue(action),defaultValue);
            sendCodingDebugVariable(planet,path,value);
        }
        return value;
    }

    /**
     * Returns color value stored in arguments.
     * @param path key of argument, that stores color value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public @NotNull Color getColor(@NotNull String path, Color defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        Color value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
        } else if (arg.getValue(action) instanceof Color color){
            value = color;
            sendCodingDebugVariable(planet,path,color);
        }
        return value;
    }

    /**
     * Returns float value stored in arguments.
     * @param path key of argument, that stores float value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public float getFloat(@NotNull String path, float defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        float value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
        } else {
            value = parseObject(arg.getValue(action),defaultValue);
            sendCodingDebugVariable(planet,path,value);
        }
        return value;
    }

    /**
     * Returns double value stored in arguments.
     * @param path key of argument, that stores double value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public double getDouble(@NotNull String path, double defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        double value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet, path);
        } else {
            value = parseObject(arg.getValue(action), defaultValue);
            sendCodingDebugVariable(planet,path,value);
        }
        return value;
    }

    /**
     * Returns text value stored in arguments.
     * @param path key of argument, that stores text value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public @NotNull String getText(@NotNull String path, @NotNull String defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
            return defaultValue;
        }
        sendCodingDebugVariable(planet,path,arg.getValue(action));
        return arg.getValue(action).toString();
    }

    /**
     * Returns component value stored in arguments.
     * @param path key of argument, that stores component value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public @NotNull Component getComponent(@NotNull String path, @NotNull Component defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
            return defaultValue;
        }
        sendCodingDebugVariable(planet,path,arg.getValue(action));
        String text = arg.getValue(action).toString();
        return textToComponent(text);
    }

    public Component textToComponent(String text) {
        try {
            if (text.contains("§")) {
                return LegacyComponentSerializer.legacySection().deserialize(text);
            } else {
                Component miniMessage = MiniMessage.miniMessage().deserialize(text);
                ClickEvent clickEvent = miniMessage.clickEvent();
                if (clickEvent != null && clickEvent.action() == ClickEvent.Action.RUN_COMMAND) {
                    miniMessage = miniMessage.clickEvent(null);
                }
                return miniMessage;
            }
        } catch (Exception ignored) {}
        return Component.text(text);
    }

    /**
     * Returns particle value stored in arguments.
     * @param path key of argument, that stores particle value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public @NotNull Particle getParticle(@NotNull String path, @NotNull Particle defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        if (arg != null && arg.getValue(action) instanceof Particle particle) {
            sendCodingDebugVariable(planet,path,arg.getValue(action));
            return particle;
        }
        sendCodingDebugNotFoundVariable(planet,path);
        return defaultValue;
    }

    /**
     * Returns character value stored in arguments.
     * @param path key of argument, that stores character value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public char getCharacter(@NotNull String path, char defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        if (arg != null) {
            String value = arg.getValue(action).toString();
            if (value != null && !value.isEmpty()) {
                sendCodingDebugVariable(planet, path, value.charAt(0));
                return value.charAt(0);
            }
        }
        sendCodingDebugNotFoundVariable(planet,path);
        return defaultValue;
    }

    /**
     * Returns location value stored in arguments.
     * @param path key of argument, that stores location value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public @NotNull Location getLocation(@NotNull String path, @NotNull Location defaultValue, @NotNull Action action) {
        Argument arg = getArg(path);
        Location locationValue = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
        } else if (arg.getValue(action) instanceof Location) {
            locationValue = (Location) arg.getValue(action);
            sendCodingDebugVariable(planet,path,locationValue.getX()+" "+locationValue.getY()+" "+locationValue.getZ()+" "+locationValue.getYaw()+" "+locationValue.getPitch());
        }
        locationValue.setWorld(planet.getTerritory().getWorld());
        if (BlockUtils.isOutOfBorders(locationValue)) {
            sendCodingDebugLog(planet,"Location is out of borders! " + locationValue);
            return defaultValue;
        }
        return locationValue.clone();
    }

    /**
     * Returns vector value stored in arguments.
     * @param path key of argument, that stores vector value.
     * @param defaultValue default value.
     * @param action action, that asks for value.
     * @return argument value, or default value - if argument is empty or not found.
     */
    public @NotNull Vector getVector(String path, Vector defaultValue, Action action) {
        Argument arg = getArg(path);
        Vector vectionValue = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(planet,path);
        } else if (arg.getValue(action) instanceof Vector) {
            vectionValue = (Vector) arg.getValue(action);
            sendCodingDebugVariable(planet,path,vectionValue.getX()+" "+vectionValue.getY()+" "+vectionValue.getZ());
        }
        return vectionValue;
    }

    private Object getVariableValue(VariableLink link, Action action) {
        return planet.getVariables().getVariableValue(link,action);
    }

    public int parseObject(Object object, int defaultValue) {
        return switch (object) {
            case Float f -> f.intValue();
            case Long l -> l.intValue();
            case Double d -> d.intValue();
            case Integer i -> i;
            case String s -> NumberUtils.toInt(s,defaultValue);
            default -> defaultValue;
        };
    }

    public long parseObject(Object object, long defaultValue) {
        return switch (object) {
            case Float f -> f.longValue();
            case Long l -> l;
            case Double d -> d.longValue();
            case Integer i -> i.longValue();
            case String s -> NumberUtils.toLong(s,defaultValue);
            default -> defaultValue;
        };
    }

    public boolean parseObject(Object object, boolean defaultValue) {
        return switch (object) {
            case Boolean b -> b;
            case Number n -> parseObject(n,defaultValue ? 2 : 1) > 1;
            case String s -> s.equalsIgnoreCase("true");
            default -> defaultValue;
        };
    }

    public float parseObject(Object object, float defaultValue) {
        return switch (object) {
            case Float f -> f;
            case Long l -> l.floatValue();
            case Double d -> d.floatValue();
            case Integer i -> i.floatValue();
            case String s -> NumberUtils.toFloat(s,defaultValue);
            default -> defaultValue;
        };
    }

    public double parseObject(Object object, double defaultValue) {
        return switch (object) {
            case Float f -> f.doubleValue();
            case Long l -> l.doubleValue();
            case Double d -> d;
            case Integer i -> i.doubleValue();
            case String s -> NumberUtils.toDouble(s,defaultValue);
            default -> defaultValue;
        };
    }

    public void setArgumentValue(@NotNull String path, @NotNull ValueType type, @NotNull Object value) {
        argumentList.removeIf(it -> path.equals(it.path));
        argumentList.add(new Argument(planet,type,path,value));
    }

    public void removeArgumentValue(@NotNull String... paths) {
        for (String path : paths) {
            argumentList.removeIf(it -> path.equals(it.path));
        }
    }

    public @NotNull List<Argument> getArgumentList() {
        return argumentList;
    }
}
