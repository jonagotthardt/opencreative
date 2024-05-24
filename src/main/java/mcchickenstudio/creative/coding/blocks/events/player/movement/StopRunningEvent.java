package mcchickenstudio.creative.coding.blocks.events.player.movement;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class StopRunningEvent extends CreativeEvent {

    public StopRunningEvent(Player player) {
        super(player);
    }

}
