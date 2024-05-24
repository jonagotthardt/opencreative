package mcchickenstudio.creative.coding.blocks.executors.player.world;

import mcchickenstudio.creative.coding.blocks.executors.ExecutorType;
import mcchickenstudio.creative.coding.blocks.executors.player.PlayerExecutor;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.entity.Player;

public class LikeExecutor extends PlayerExecutor {

    public LikeExecutor(Plot plot, int x, int y, int z) {
        super(plot, x, y, z);
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.PLAYER_LIKED;
    }
}
