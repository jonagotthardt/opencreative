package mcchickenstudio.creative.coding.blocks.events.player.world;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class PlayEvent extends CreativeEvent {

    public PlayEvent(Player player) {
        super(player);
    }

}
