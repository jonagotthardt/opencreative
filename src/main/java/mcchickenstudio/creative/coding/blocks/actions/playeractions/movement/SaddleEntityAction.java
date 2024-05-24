package mcchickenstudio.creative.coding.blocks.actions.playeractions.movement;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

public class SaddleEntityAction extends PlayerAction {
    public SaddleEntityAction(Executor executor, int x, List<String> arguments) {
        super(executor, x, arguments);
    }

    @Override
    public void execute(List<Entity> selection) {
        String name = getArguments().get(0);
        World world = getExecutor().getPlot().world;
        if (world == null) return;
        for (Entity entity : world.getEntities()) {
            if (entity.getName().equalsIgnoreCase(name) || entity.getUniqueId().equals(name)) {
                for (Entity selected : selection) {
                    entity.addPassenger(selected);
                }
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SADDLE_ENTITY;
    }
}
