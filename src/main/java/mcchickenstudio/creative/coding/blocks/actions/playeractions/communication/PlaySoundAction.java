package mcchickenstudio.creative.coding.blocks.actions.playeractions.communication;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class PlaySoundAction extends PlayerAction {

    public PlaySoundAction(Executor executor, int x, List<String> arguments) {
        super(executor, x, arguments);
    }

    @Override
    public void execute(List<Entity> selection) {
        String sound = "entity.player.levelup";
        float volume = 100f;
        float pitch = 1.0f;
        if (!getArguments().isEmpty()) {
             sound = getArguments().get(0);
        }
        if (getArguments().size() > 1) {
            volume = Float.parseFloat(getArguments().get(1));
        }
        if (getArguments().size() > 2) {
            pitch = Float.parseFloat(getArguments().get(2));
        }
        for (Entity entity : selection) {
            ((Player) entity).playSound(entity.getLocation(),sound,volume,pitch);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PLAY_SOUND;
    }
}
