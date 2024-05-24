package mcchickenstudio.creative.coding.blocks.executors.player.world;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import mcchickenstudio.creative.coding.blocks.events.EventVariables;
import mcchickenstudio.creative.coding.blocks.events.player.world.ChatEvent;
import mcchickenstudio.creative.coding.blocks.executors.ExecutorType;
import mcchickenstudio.creative.coding.blocks.executors.player.PlayerExecutor;
import mcchickenstudio.creative.plots.Plot;

public class ChatExecutor extends PlayerExecutor {

    public ChatExecutor(Plot plot, int x, int y, int z) {
        super(plot, x, y, z);
    }

    @Override
    public void setTempVars(CreativeEvent event) {
        if (event instanceof ChatEvent) {
            ChatEvent chatEvent = (ChatEvent) event;
            setVar(EventVariables.Variable.MESSAGE, chatEvent.getMessage());
        }
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.PLAYER_SEND_MESSAGE;
    }
}
