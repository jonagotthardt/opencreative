package mcchickenstudio.creative.coding.blocks.events.player.movement;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class StopSneakingEvent extends CreativeEvent {

    public StopSneakingEvent(Player player) {
        super(player);
    }

}
