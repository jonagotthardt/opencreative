package mcchickenstudio.creative.coding.blocks.executors.player.movement;

import mcchickenstudio.creative.coding.blocks.executors.ExecutorType;
import mcchickenstudio.creative.coding.blocks.executors.player.PlayerExecutor;
import mcchickenstudio.creative.plots.Plot;

public class StopSneakingExecutor extends PlayerExecutor {

    public StopSneakingExecutor(Plot plot, int x, int y, int z) {
        super(plot, x, y, z);
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.PLAYER_STOP_SNEAKING;
    }
}
