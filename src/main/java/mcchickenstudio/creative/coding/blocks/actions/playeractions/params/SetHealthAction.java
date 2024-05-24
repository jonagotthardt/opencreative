package mcchickenstudio.creative.coding.blocks.actions.playeractions.params;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class SetHealthAction extends PlayerAction {
    public SetHealthAction(Executor executor, int x, List<String> arguments) {
        super(executor, x, arguments);
    }

    @Override
    public void execute(List<Entity> selection) {
        for (Entity entity : selection) {
            Double health = 20.0d;
            if (!getArguments().isEmpty()) {
                health = Double.parseDouble(getArguments().get(1));
            }
            //FIXME: Add parameters into layout
            //if (getParameter() == 1) {
                ((Player) entity).setHealth(health);
            //} else {
            //    ((Player) entity).setHealth(((Player) entity).getHealth()+health);
            //}
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SET_HEALTH;
    }
}
