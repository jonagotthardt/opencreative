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


import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.ChatColor;

public class VariableLink {

    private final String name;
    private final VariableType type;
    private final Executor executor;

    public VariableLink(String name, VariableType type, Executor executor) {
        this.name = name;
        this.type = type;
        this.executor = executor;
    }

    public String getName() {
        return name;
    }

    public Executor getExecutor() {
        return executor;
    }

    public enum VariableType {

        LOCAL(ChatColor.RED),
        GLOBAL(ChatColor.YELLOW),
        SAVED(ChatColor.GREEN);

        private final ChatColor color;

        VariableType(ChatColor color) {
            this.color = color;
        }

        public ChatColor getColor() {
            return color;
        }

        public String getId() {
            return "coding.variable." + this.name().toLowerCase();
        }

        public static VariableType getEnum(String string) {
            for (VariableType type : VariableType.values()) {
                if (type.name().equals(string.toUpperCase())) {
                    return type;
                }
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "Variable: " + name + " Type: " + type.name();
    }

    public VariableType getType() {
        return type;
    }
}
