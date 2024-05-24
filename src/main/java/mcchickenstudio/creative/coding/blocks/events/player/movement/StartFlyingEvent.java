package mcchickenstudio.creative.coding.blocks.events.player.movement;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class StartFlyingEvent extends CreativeEvent {

    public StartFlyingEvent(Player player) {
        super(player);
    }

}
