package mcchickenstudio.creative.coding.blocks.executors.player.interaction;

import mcchickenstudio.creative.coding.blocks.executors.ExecutorType;
import mcchickenstudio.creative.coding.blocks.executors.player.PlayerExecutor;
import mcchickenstudio.creative.plots.Plot;

public class DestroyBlockExecutor extends PlayerExecutor {

    public DestroyBlockExecutor(Plot plot, int x, int y, int z) {
        super(plot, x, y, z);
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.PLAYER_DESTROY_BLOCK;
    }
}
