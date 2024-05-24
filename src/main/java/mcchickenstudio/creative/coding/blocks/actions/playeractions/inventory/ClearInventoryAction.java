package mcchickenstudio.creative.coding.blocks.actions.playeractions.inventory;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class ClearInventoryAction extends PlayerAction {
    public ClearInventoryAction(Executor executor, int x, List<String> arguments) {
        super(executor, x, arguments);
    }

    @Override
    public void execute(List<Entity> selection) {
        for (Entity entity : selection) {
            ((Player) entity).getInventory().clear();
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.CLEAR_INVENTORY;
    }
}
