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

import mcchickenstudio.creative.coding.blocks.variables.VariableType;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingDebugNotFoundVariable;
import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingDebugVariable;

public class Arguments {

    private final Plot plot;
    private final List<Argument> argumentList = new ArrayList<>();

    private final static Pattern INT_PATTERN = Pattern.compile("^-?[0-9]*$");//"-?[1-9]+[0-9]*");
    private final static Pattern FLOAT_PATTERN = Pattern.compile("^-?[0-9]*\\.?[0-9]+$");//-?[0-9]+\\.?[0-9]*");
    private final static Pattern BOOLEAN_PATTERN = Pattern.compile("(?i)true|yes|t|y|1");

    public Arguments(Plot plot) {
        this.plot = plot;
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
        VariableType type = VariableType.parseString(configType.toUpperCase());
        Object value = parseValue(section,name,type,configValue);
        return new Argument(type,name,value);
    }

    private Object parseValue(ConfigurationSection section, String name, VariableType type, Object configValue) {
        String stringValue = configValue.toString();
        ConfigurationSection listSection = section.getConfigurationSection(name + ".value");
        switch (type) {
            case LIST:
                List<Argument> arguments = new ArrayList<>();
                if (listSection == null) {
                    System.out.println("null");
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
    public final List<String> getTextList(String path) {
        List<String> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null && arg.isList()) {
            try {
                List<Argument> args = (List<Argument>) arg.getValue();
                for (Argument textArg : args) {
                    list.add(textArg.value.toString());
                }
            } catch (ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(plot,path,list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public final List<ItemStack> getItemList(String path) {
        List<ItemStack> list = new ArrayList<>();
        Argument arg = getArg(path);
        if (arg != null && arg.isList()) {
            try {
                List<Argument> args = (List<Argument>) arg.getValue();
                for (Argument itemArg : args) {
                    list.add((ItemStack) itemArg.getValue());
                }
            } catch (ClassCastException e) {
                return list;
            }
        }
        sendCodingDebugVariable(plot,path,list);
        return list;
    }

    public ItemStack getValue(String path, ItemStack defaultValue) {
        Argument arg = getArg(path);
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,defaultValue);
            return defaultValue;
        }
        if (arg.getValue() instanceof ItemStack) {
            sendCodingDebugVariable(plot,path,arg.getValue());
            return (ItemStack) arg.getValue();
        }
        sendCodingDebugNotFoundVariable(plot,path,defaultValue);
        return defaultValue;
    }

    public boolean getValue(String path, boolean defaultValue) {
        Argument arg = getArg(path);
        boolean value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,defaultValue);
        } else if (arg.getValue() instanceof Boolean) {
            value = (boolean) arg.getValue();
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue() instanceof Integer) {
            value = (getValue(path,(defaultValue ? 2 : 1)) > 1);
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue() instanceof Float) {
            value = (getValue(path,(defaultValue ? 2f : 1f)) > 1f);
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue() instanceof Double) {
            value = (getValue(path,(defaultValue ? 2d : 1d)) > 1d);
            sendCodingDebugVariable(plot,path,value);
        }
        return value;
    }

    public byte getValue(String path, byte defaultValue) {
        Argument arg = getArg(path);
        byte value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,defaultValue);
        } else if (arg.getValue() instanceof Integer) {
            value = (byte) arg.getValue();
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue() instanceof Float) {
            value = (byte) Math.round((float) arg.getValue());
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue() instanceof Double) {
            value = (byte) Math.round((Double) arg.getValue());
            sendCodingDebugVariable(plot,path,value);
        }
        return value;
    }

    public int getValue(String path, int defaultValue) {
        Argument arg = getArg(path);
        int value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,defaultValue);
        } else if (arg.getValue() instanceof Integer) {
            value = (int) arg.getValue();
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue() instanceof Float) {
            value = Math.round((float) arg.getValue());
            sendCodingDebugVariable(plot,path,value);
        } else if (arg.getValue() instanceof Double) {
            value = (int) Math.round((Double) arg.getValue());
            sendCodingDebugVariable(plot,path,value);
        }
        return value;
    }

    public float getValue(String path, float defaultValue) {
        Argument arg = getArg(path);
        float value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,defaultValue);
        } else if (arg.getValue() instanceof Integer || arg.getValue() instanceof Float || arg.getValue() instanceof Double) {
            value = (float) arg.getValue();
            sendCodingDebugVariable(plot,path,value);
        }
        return value;
    }

    public double getValue(String path, double defaultValue) {
        Argument arg = getArg(path);
        double value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,defaultValue);
        } else if (arg.getValue() instanceof Integer || arg.getValue() instanceof Float || arg.getValue() instanceof Double) {
            value = Double.parseDouble(String.valueOf(arg.getValue()));
            sendCodingDebugVariable(plot,path,defaultValue);
        }
        return value;
    }

    public String getValue(String path, String defaultValue) {
        Argument arg = getArg(path);
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,defaultValue);
            return defaultValue;
        }
        sendCodingDebugVariable(plot,path,arg.getValue());
        return arg.getValue().toString();
    }

    public Location getValue(String path, Location defaultValue) {
        Argument arg = getArg(path);
        Location value = defaultValue;
        if (arg == null) {
            sendCodingDebugNotFoundVariable(plot,path,value.getX()+" "+value.getY()+" "+value.getZ()+" "+value.getYaw()+" "+value.getPitch());
        } else if (arg.getType() == VariableType.LOCATION && arg.getValue() instanceof Location) {
            value = (Location) arg.getValue();
            sendCodingDebugVariable(plot,path,value.getX()+" "+value.getY()+" "+value.getZ()+" "+value.getYaw()+" "+value.getPitch());
        }
        return value;
    }
}
