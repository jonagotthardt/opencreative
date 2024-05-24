package mcchickenstudio.creative.coding.blocks.actions.playeractions.communication;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;

import java.util.List;

public class SendMessageAction extends PlayerAction {

    public SendMessageAction(Executor executor, int x, List<String> arguments) {
        super(executor, x, arguments);
    }

    @Override
    public void execute(List<Entity> selection) {
        for (Entity entity : selection) {
            for (String message : getArguments()) {
                entity.sendMessage(parseText(message,entity));
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SEND_MESSAGE;
    }
}
