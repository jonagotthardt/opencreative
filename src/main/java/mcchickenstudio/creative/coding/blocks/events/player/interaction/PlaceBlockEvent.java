package mcchickenstudio.creative.coding.blocks.events.player.interaction;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class PlaceBlockEvent extends CreativeEvent {

    public PlaceBlockEvent(Player player) {
        super(player);
    }

}
