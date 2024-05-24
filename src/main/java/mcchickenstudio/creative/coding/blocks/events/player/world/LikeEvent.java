package mcchickenstudio.creative.coding.blocks.events.player.world;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class LikeEvent extends CreativeEvent {

    public LikeEvent(Player player) {
        super(player);
    }

}
