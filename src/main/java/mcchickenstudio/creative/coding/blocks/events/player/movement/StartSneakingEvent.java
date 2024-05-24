package mcchickenstudio.creative.coding.blocks.events.player.movement;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class StartSneakingEvent extends CreativeEvent {

    public StartSneakingEvent(Player player) {
        super(player);
    }

}
