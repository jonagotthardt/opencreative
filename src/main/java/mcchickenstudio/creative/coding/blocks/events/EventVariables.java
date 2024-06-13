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

package mcchickenstudio.creative.coding.blocks.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class EventVariables {

    private final Map<Variable,Object> variables = new HashMap<>();

    public void setVariable(Variable var, Object value) {
        variables.put(var,value);
    }
    public Object getVarValue(Variable var) {
        return variables.get(var);
    }

    public enum Variable {
        PLAYER (Player.class),
        DAMAGER (Entity.class),
        KILLER (Entity.class),
        SHOOTER (Entity.class),
        ITEM (ItemStack.class),
        CURRENT_ITEM (ItemStack.class),
        CLICKED_ITEM (ItemStack.class),
        ENTITY (Entity.class),
        UNIX_TIME (Long.class),
        PLOT_NAME (String.class),
        PLOT_DESCRIPTION (String.class),
        PLOT_ONLINE (Integer.class),
        MESSAGE(String.class),
        BLOCK(Block.class);

        final Class<?> valueClass;

        Variable(Class<?> valueClass) {
            this.valueClass = valueClass;
        }

        public Class<?> getValueClass() {
            return valueClass;
        }

        public String getLocaleName() {
            return getLocaleMessage("items.developer.temp-vars." + this.name().toLowerCase() + ".name" ,false);
        }
    }


}
