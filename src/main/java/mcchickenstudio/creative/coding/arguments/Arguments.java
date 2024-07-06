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

package mcchickenstudio.creative.coding.arguments;

import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionsHandler;
import mcchickenstudio.creative.coding.blocks.events.EventValues;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.debug.values.EventValueLink;
import mcchickenstudio.creative.coding.variables.ValueType;
import mcchickenstudio.creative.debug.values.VariableLink;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.utils.ErrorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingDebugNotFoundVariable;
import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingDebugVariable;

public class Arguments {

    private final Plot plot;
    private final Executor executor;
    private final List<Argument> argumentList = new ArrayList<>();

    private final static Pattern INT_PATTERN = Pattern.compile("^-?[0-9]*$");
    private final static Pattern FLOAT_PATTERN = Pattern.compile("^-?[0-9]*\\.?[0-9]+$");
    private final static Pattern BOOLEAN_PATTERN = Pattern.compile("(?i)true|yes|t|y|1");

    public Arguments(Plot plot, Executor executor) {
        this.plot = plot;
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
        return new Argument(plot,type,name,value);
    }

    private Object parseValue(ConfigurationSection section, String name, ValueType type, Object configValue) {
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
                    return plot.world.getSpawnLocation();
                }
                x = listSection.getDouble("x");
                y = listSection.getDouble("y");
                z = listSection.getDouble("z");
                yaw = (float) listSection.getDouble("yaw");
                pitch = (float) listSection.getDouble("pitch");
                return new Location(plot.world,x,y,z,yaw,pitch);
            case VARIABLE: {
                if (listSection == null) {
                    return null;
                }
                String varName = listSection.getString("name");
                String typeString = listSection.getString("type");
                VariableLink.VariableType varType = VariableLink.VariableType.getEnum(typeString);
                if (varType == null) {
                    varType = VariableLink.VariableType.GLOBAL;
                }
                return new VariableLink(varName,varType);
            }
            case EVENT_VALUE: {
                    if (listSection == null) {
                        return null;
                    }
                    String typeString = listSection.getString("name");
                    EventValues.Variable varType;
                    if (typeString.isEmpty()) return null;
                    try {
                        varType = EventValues.Variable.valueOf(typeString);
                    } catch (Exception e) {
                        return null;
                    }
                    return new EventValueLink(varType,executor);
            }
            case NUMBER:
                if (INT_PATTERN.matcher(stringValue).matches()) {
                    return Integer.parseInt(stringValue);
                } else if (FLOAT_PATTERN.matcher(stringValue).matches()) {
                    return Float.parseFloat(stringValue);
                }
                return 0;
            case TEXT:
                return stringValue;
            case BOOLEAN:
                return Boolean.parseBoolean(stringValue);
            case PARAMETER:
                return Float.parseFloat(stringValue);
            case ITEM:
                if (listSection == null) {
                    return new ItemStack(Material.AIR);
                }
                return ItemStack.deserialize(listSection.getValues(true));
            default:
                return stringValue;
        }
    }

    public final boolean pathExists(String path) {
        for (Argument argument : argumentList) {
            if (argument.getPath().equals(path)) return true;
        }
        return false;
    }

    private Argument getArg(String path) {
        for (Argument argument : argumentList) {
            if (argument.getPath().equals(path)) {
                return argument;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public final <T> Map<T,T> getMap(String path, Action action) {
        Map<T,T> map = new HashMap<>();
        Argument arg = getArg(path);
        if (arg != null) {
            try {
                if (arg.getType() == ValueType.VARIABLE) {
                    return (Map<T,T>) arg.getValue(action);
                }
            } catch(ClassCastException e) {
                return map;
            }
        }
        sendCodingDebugVariable(plot,path,map);
        return map;
    }

    @SuppressWarnings("unchecked")
    public final <T> List<T> getList(String path, Action action) {
        List<T> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null) {
            try {
                if (arg.getType() == ValueType.VARIABLE) {
                    return (List<T>) arg.getValue(action);
                } else if (arg.isList()) {
                    List<Argument> args = (List<Argument>) arg.getValue(action);
                    for (Argument argument : args) {
                        list.add((T) (argument.getValue(action)));
                    }
                }
            } catch(ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(plot,path,list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public final List<VariableLink> getVarLinksList(String path, Action action) {
        List<VariableLink> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null && arg.isList()) {
            try {
                List<Argument> args = (List<Argument>) arg.getValue(action);
                for (Argument argument : args) {
                    if (argument.value instanceof VariableLink) {
                        list.add((VariableLink) argument.value);
                    }
                }
            } catch (ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(plot,path,list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public final List<String> getTextList(String path, Action action) {
        List<String> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null && arg.isList()) {
            try {
                List<Argument> args = (List<Argument>) arg.getValue(action);
                for (Argument textArg : args) {
                    list.add(Argument.parseEntity(textArg.getValue(action).toString(),action));
                }
            } catch (ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(plot,path,list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public final List<Double> getNumbersList(String path, Action action) {
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
        sendCodingDebugVariable(plot,path,list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public final List<ItemStack> getItemList(String path, Action action) {
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
        sendCodingDebugVariable(plot,path,list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public final List<Location> getLocationList(String path, Action action) {
        List<Location> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null && arg.isList()) {
            try {
                List<Argument> args = (List<Argument>) arg.getValue(action);
                for (Argument itemArg : args) {
                    list.add((Location) itemArg.getValue(action));
                }
            } catch (ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(plot,path,list);
        return list;
    }

    public VariableLink getVariableLink(String path, Action action) {
        Argument arg = getArg(path);
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,null);
            return null;
        }
        if (arg.value instanceof VariableLink link) {
            if (link.getVariableType() == VariableLink.VariableType.LOCAL) {
                link.setHandler(action.getHandler().getMainActionHandler());
            }
            sendCodingDebugVariable(plot,path,link);
            return link;
        }
        sendCodingDebugNotFoundVariable(plot,path,null);
        return null;
    }

    public ItemStack getValue(String path, ItemStack defaultValue, Action action) {
        Argument arg = getArg(path);
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,defaultValue);
            return defaultValue;
        }
        if (arg.getValue(action) instanceof ItemStack) {
            sendCodingDebugVariable(plot,path,arg.getValue(action));
            return (ItemStack) arg.getValue(action);
        }
        sendCodingDebugNotFoundVariable(plot,path,defaultValue);
        return defaultValue;
    }

    public boolean getValue(String path, boolean defaultValue, Action action) {
        Argument arg = getArg(path);
        boolean value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,defaultValue);
        } else if (arg.getValue(action) instanceof Boolean) {
            value = (boolean) arg.getValue(action);
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue(action) instanceof Integer) {
            value = (getValue(path,(defaultValue ? 2 : 1), action) > 1);
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue(action) instanceof Float) {
            value = (getValue(path,(defaultValue ? 2f : 1f), action) > 1f);
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue(action) instanceof Double) {
            value = (getValue(path,(defaultValue ? 2d : 1d), action) > 1d);
            sendCodingDebugVariable(plot,path,value);
        }
        return value;
    }

    public Object getValue(String path, Action action) {
        Argument arg = getArg(path);
        Object value = "";
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,value);
        } else {
            value = arg.getValue(action);
            sendCodingDebugVariable(plot,path,value);
        }
        return value;
    }

    public byte getValue(String path, byte defaultValue, Action action) {
        Argument arg = getArg(path);
        byte value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,defaultValue);
        } else {
            value = parseObject(arg.getValue(action),defaultValue);
            sendCodingDebugVariable(plot,path,value);
        }
        return value;
    }

    public int getValue(String path, int defaultValue, Action action) {
        Argument arg = getArg(path);
        int value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot, path, defaultValue);
        } else if (arg.getValue(action) instanceof Long l) {
            value = l.intValue();
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue(action) instanceof Integer) {
            value = (int) arg.getValue(action);
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue(action) instanceof Float) {
            value = Math.round((float) arg.getValue(action));
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue(action) instanceof Double) {
            value = (int) Math.round((Double) arg.getValue(action));
            sendCodingDebugVariable(plot,path,value);
        }
        return value;
    }

    public float getValue(String path, float defaultValue, Action action) {
        Argument arg = getArg(path);
        float value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,defaultValue);
        } else {
            value = parseObject(arg.getValue(action),defaultValue);
            sendCodingDebugVariable(plot,path,value);
        }
        return value;
    }

    public double getValue(String path, double defaultValue, Action action) {
        Argument arg = getArg(path);
        double value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot, path, defaultValue);
        } else if (arg.getValue(action) instanceof Float) {
            value = (float) arg.getValue(action);
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue(action) instanceof Long || arg.getValue(action) instanceof Double) {
            value = (double) arg.getValue(action);
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue(action) instanceof Integer) {
            value = Double.parseDouble(String.valueOf(arg.getValue(action)));
            sendCodingDebugVariable(plot,path,value);
        }
        return value;
    }

    public String getValue(String path, String defaultValue, Action action) {
        Argument arg = getArg(path);
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,defaultValue);
            return defaultValue;
        }
        sendCodingDebugVariable(plot,path,arg.getValue(action));
        return arg.getValue(action).toString();
    }

    public char getValue(String path, char defaultValue, Action action) {
        Argument arg = getArg(path);
        if (arg != null && arg.getValue(action) != null) {
            String value = arg.getValue(action).toString();
            if (value != null && !value.isEmpty()) {
                sendCodingDebugVariable(plot,path,value.charAt(0));
                return value.charAt(0);
            }
        }
        sendCodingDebugNotFoundVariable(plot,path,defaultValue);
        return defaultValue;
    }

    public Location getValue(String path, Location defaultValue, Action action) {
        Argument arg = getArg(path);
        Location locationValue = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,locationValue.getX()+" "+locationValue.getY()+" "+locationValue.getZ()+" "+locationValue.getYaw()+" "+locationValue.getPitch());
        } else if (arg.getValue(action) instanceof Location) {
            locationValue = (Location) arg.getValue(action);
            sendCodingDebugVariable(plot,path,locationValue.getX()+" "+locationValue.getY()+" "+locationValue.getZ()+" "+locationValue.getYaw()+" "+locationValue.getPitch());
        }
        locationValue.setWorld(plot.world);
        return locationValue;
    }

    private Object getVariableValue(VariableLink link) {
        return plot.getWorldVariables().getVariableValue(link);
    }

    public float parseObject(Object object, float defaultValue) {
        float value = defaultValue;
        if (object instanceof Integer || object instanceof Float) {
            value = (float) object;
        } else if (object instanceof Double) {
            value = ((Double) object).floatValue();
        }
        return value;
    }

    public double parseObject(Object object, double defaultValue) {
        double value = defaultValue;
        if (object instanceof Integer || object instanceof Float || object instanceof Double) {
            value = Double.parseDouble(String.valueOf(object));
        }
        if (object == null) {
            return defaultValue;
        } else if (object instanceof Float f) {
            value = f;
        } else if (object instanceof Long || object instanceof Double) {
            value = (double) object;
        } else if (object instanceof Integer) {
            value = Double.parseDouble(String.valueOf(object));
        }
        return value;
    }

    public byte parseObject(Object object, byte defaultValue) {
        byte value = defaultValue;
        if (object instanceof VariableLink) {
            VariableLink link = (VariableLink) object;
            return parseObject(getVariableValue(link),defaultValue);
        } else if (object instanceof Integer) {
            value = (byte) object;
        } else if (object instanceof Float) {
            value = (byte) Math.round((float) object);
        } else if (object instanceof Double) {
            value = (byte) Math.round((Double) object);
        }
        return value;
    }
}
