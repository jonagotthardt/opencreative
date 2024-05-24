package mcchickenstudio.creative.coding.blocks.events;

import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EventVariables {

    private final Map<Variable,Object> variables = new HashMap<>();

    public void setVariable(Variable var, Object value) {
        System.out.println(" set " + var.name() + " " + value.toString());
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
        CURRENT_ITEM (ItemStack.class),
        CLICKED_ITEM (ItemStack.class),
        ENTITY (Entity.class),
        UNIX_TIME (Long.class),
        PLOT_NAME (String.class),
        PLOT_DESCRIPTION (String.class),
        PLOT_ONLINE (Integer.class),
        MESSAGE(String.class),
        BLOCK (Block.class);

        final Class<?> valueClass;

        Variable(Class<?> valueClass) {
            this.valueClass = valueClass;
        }

        public Class<?> getValueClass() {
            return valueClass;
        }
    }


}
