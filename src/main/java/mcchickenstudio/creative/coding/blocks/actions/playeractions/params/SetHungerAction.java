package mcchickenstudio.creative.coding.blocks.actions.playeractions.params;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class SetHungerAction extends PlayerAction {
    public SetHungerAction(Executor executor, int x, List<String> arguments) {
        super(executor, x, arguments);
    }

    @Override
    public void execute(List<Entity> selection) {
        for (Entity entity : selection) {
            int food = 20;
            if (!getArguments().isEmpty()) {
                food = Math.round(Float.parseFloat(getArguments().get(1)));
            }
            //FIXME: Add parameters into layout
            //if (getParameter() == 1) {
                ((Player) entity).setFoodLevel(food);
            //} else {
            //    ((Player) entity).setFoodLevel(((Player) entity).getFoodLevel()+food);
            //}
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SET_HUNGER;
    }
}
