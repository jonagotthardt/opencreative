package mcchickenstudio.creative.coding.blocks.executors.player.fighting;

import mcchickenstudio.creative.coding.blocks.executors.ExecutorType;
import mcchickenstudio.creative.coding.blocks.executors.player.PlayerExecutor;
import mcchickenstudio.creative.plots.Plot;

public class MobDamagesPlayerExecutor extends PlayerExecutor {

    public MobDamagesPlayerExecutor(Plot plot, int x, int y, int z) {
        super(plot, x, y, z);
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.MOB_DAMAGE_PLAYER;
    }
}
