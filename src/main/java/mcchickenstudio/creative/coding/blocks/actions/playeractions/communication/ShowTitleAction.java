package mcchickenstudio.creative.coding.blocks.actions.playeractions.communication;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class ShowTitleAction extends PlayerAction {
    public ShowTitleAction(Executor executor, int x, List<String> arguments) {
        super(executor, x, arguments);
    }

    @Override
    public void execute(List<Entity> selection) {
        String title = " ";
        String subtitle = " ";
        int fadeIn = 20;
        int fadeOut = 10;
        int stay = 60;
        if (!getArguments().isEmpty()) {
            title = getArguments().get(0);
        }
        if (getArguments().get(1) != null) {
            subtitle = getArguments().get(1);
        }
        if (getArguments().get(2) != null) {
            fadeIn = Math.round(Float.parseFloat(getArguments().get(2)));
        }
        if (getArguments().size() > 3) {
            stay = Math.round(Float.parseFloat(getArguments().get(3)));
        }
        if (getArguments().size() > 4) {
            fadeOut = Math.round(Float.parseFloat(getArguments().get(4)));
        }
        for (Entity entity : selection) {
            ((Player) entity).sendTitle(title,subtitle,fadeIn,stay,fadeOut);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SHOW_TITLE;
    }
}
