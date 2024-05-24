package mcchickenstudio.creative.coding.blocks.actions.playeractions;

import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.executors.Executor;

import java.util.List;

public abstract class PlayerAction extends Action {

    public PlayerAction(Executor executor, int x, List<String> arguments) {
        super(executor, x, arguments);
    }

    public ActionCategory getActionCategory() {
        return ActionCategory.PLAYER_ACTION;
    }
}
