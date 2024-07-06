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

package mcchickenstudio.creative.debug;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.events.EventValues;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.debug.values.*;
import mcchickenstudio.creative.debug.values.Boolean;
import mcchickenstudio.creative.debug.values.Number;
import mcchickenstudio.creative.debug.values.EventValueLink;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.regex.Pattern;

/**
 * IN DEVELOPMENT
 * This should merge and simplify manipulations with values, like setting, loading and saving data.
 */
public class ItemParser {

    private final static Pattern INT_PATTERN = Pattern.compile("^-?[0-9]*$");
    private final static Pattern FLOAT_PATTERN = Pattern.compile("^-?[0-9]*\\.?[0-9]+$");
    private final static Pattern BOOLEAN_PATTERN = Pattern.compile("(?i)true|yes|t|y|1");

    /**
     * Returns value from coding itemStack.
     * @param item itemStack to get value from it
     * @return the parsed value
     */
    public static CodingValue getItemValue(ItemStack item, Executor executor) {
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(),"opencreative");
        if (meta != null && meta.getPersistentDataContainer().has(key)) {
            Component displayName = meta.displayName();
            List<Component> lore = meta.lore();
            String name = displayName != null ? ((TextComponent) displayName).content() : "";
            switch (item.getType()) {
                case BOOK:
                    return new Text(name);
                case SLIME_BALL:
                    try {
                        double number = Double.parseDouble(ChatColor.stripColor(name));
                        return new Number(number);
                    } catch (Exception ignored) {
                        return new Item(item);
                    }
                case CLOCK:
                    try {
                        boolean value = java.lang.Boolean.parseBoolean(ChatColor.stripColor(name));
                        return new Boolean(value);
                    } catch (Exception ignored) {
                        return new Item(item);
                    }
                case NAME_TAG:
                    try {
                        EventValues.Variable type = EventValues.Variable.valueOf(ChatColor.stripColor(name));
                        return new EventValueLink(type,executor);
                    } catch (Exception ignored) {
                        return new Item(item);
                    }
                case MAGMA_CREAM:
                    try {
                        NamespacedKey varKey = new NamespacedKey(Main.getPlugin(),"variable_type");
                        if (meta.getPersistentDataContainer().has(varKey)) {
                            byte varByteType = meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BYTE, (byte) 1);
                            VariableLink.VariableType varType = VariableLink.VariableType.getById(varByteType);
                            return new VariableLink(ChatColor.stripColor(name),varType);
                        }

                    } catch (Exception ignored) {
                        return new Item(item);
                    }
                case PAPER:
                    double x,y,z;
                    float yaw,pitch;
                    String locationString = ChatColor.stripColor(name);
                    String[] locCoords = locationString.split(" ");
                    if (locCoords.length == 5) {
                        try {
                            x = Double.parseDouble(locCoords[0]);
                            y = Double.parseDouble(locCoords[1]);
                            z = Double.parseDouble(locCoords[2]);
                            yaw = Float.parseFloat(locCoords[3]);
                            pitch = Float.parseFloat(locCoords[4]);
                            return new WorldLocation(executor.getPlot(),x,y,z,yaw,pitch);
                        } catch (Exception error) {
                            return new Item(item);
                        }
                    }

            }
            return new Item(item);
        } else {
            return new Item(item);
        }
    }
}
