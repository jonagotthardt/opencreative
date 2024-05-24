package mcchickenstudio.creative.coding.blocks.actions.playeractions.params;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class SetGameModeAction extends PlayerAction {
    public SetGameModeAction(Executor executor, int x, List<String> arguments) {
        super(executor, x, arguments);
    }

    @Override
    public void execute(List<Entity> selection) {
        for (Entity entity : selection) {
            GameMode gameMode = GameMode.ADVENTURE;
            if (!getArguments().isEmpty()) {
                float gm = Float.parseFloat(getArguments().get(0));
                if (gm == 2) gameMode = GameMode.SURVIVAL;
                else if (gm == 3) gameMode = GameMode.CREATIVE;
                else if (gm == 4) gameMode = GameMode.SPECTATOR;
            }
            ((Player) entity).setGameMode(gameMode);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SET_GAMEMODE;
    }
}
