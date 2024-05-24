package mcchickenstudio.creative.coding.blocks.executors.player;

import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorCategory;
import mcchickenstudio.creative.plots.Plot;

public abstract class PlayerExecutor extends Executor {
    
    public PlayerExecutor(Plot plot, int x, int y, int z) {
        super(plot, x, y, z);
    }

    @Override
    public ExecutorCategory getExecutorCategory() {
        return ExecutorCategory.EVENT_PLAYER;
    }
}
