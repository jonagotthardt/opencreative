package mcchickenstudio.creative.coding.blocks.executors.player.fighting;

import mcchickenstudio.creative.coding.blocks.executors.ExecutorType;
import mcchickenstudio.creative.coding.blocks.executors.player.PlayerExecutor;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.entity.Player;

public class PlayerTotemRespawnExecutor extends PlayerExecutor {

    public PlayerTotemRespawnExecutor(Plot plot, int x, int y, int z) {
        super(plot, x, y, z);
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.PLAYER_TOTEM_RESPAWN;
    }
}
