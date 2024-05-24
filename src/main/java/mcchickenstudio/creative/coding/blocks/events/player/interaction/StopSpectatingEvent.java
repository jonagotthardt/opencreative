package mcchickenstudio.creative.coding.blocks.events.player.interaction;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class StopSpectatingEvent extends CreativeEvent {

    public StopSpectatingEvent(Player player) {
        super(player);
    }

}
