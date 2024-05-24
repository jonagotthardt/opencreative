package mcchickenstudio.creative.coding.blocks.events.player.movement;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class StopFlyingEvent extends CreativeEvent {

    public StopFlyingEvent(Player player) {
        super(player);
    }

}
