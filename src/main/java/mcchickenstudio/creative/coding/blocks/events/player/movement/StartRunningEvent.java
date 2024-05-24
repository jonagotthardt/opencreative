package mcchickenstudio.creative.coding.blocks.events.player.movement;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class StartRunningEvent extends CreativeEvent {

    public StartRunningEvent(Player player) {
        super(player);
    }

}
