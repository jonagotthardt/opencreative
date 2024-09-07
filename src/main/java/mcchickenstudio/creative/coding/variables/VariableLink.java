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

package mcchickenstudio.creative.coding.variables;


import mcchickenstudio.creative.coding.blocks.actions.ActionsHandler;
import org.bukkit.ChatColor;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

/**
 * This class represents a link to existing or not existing world variable.
 * Links, as variables, have specified name and type. It's used for searching
 * and getting variables values.
 */
public class VariableLink {

    private String name;
    private final VariableType type;
    private ActionsHandler handler;

    public VariableLink(String name, VariableType type) {
        this.name = name;
        this.type = type;
        this.handler = null;
    }

    public void setHandler(ActionsHandler handler) {
        this.handler = handler;
    }

    public VariableType getVariableType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ActionsHandler getHandler() {
        return handler;
    }

    public enum VariableType {

        LOCAL(1,ChatColor.RED),
        GLOBAL(2,ChatColor.YELLOW),
        SAVED(3,ChatColor.GREEN);

        private final ChatColor color;
        private final int id;

        VariableType(int id, ChatColor color) {
            this.id = id;
            this.color = color;
        }

        public ChatColor getColor() {
            return color;
        }

        public int getId() {
            return id;
        }

        public static VariableType getById(int id) {
            for (VariableType type : values()) {
                if (type.getId() == id) {
                    return type;
                }
            }
            return LOCAL;
        }

        public static VariableType getEnum(String string) {
            for (VariableType type : VariableType.values()) {
                if (type.name().equals(string.toUpperCase())) {
                    return type;
                }
            }
            return null;
        }

        public String getLocalized() {
            return getLocaleMessage("items.developer.variable." + name().toLowerCase());
        }
    }

    @Override
    public String toString() {
        return "null! " + name + " - " + type.name();
    }

}
