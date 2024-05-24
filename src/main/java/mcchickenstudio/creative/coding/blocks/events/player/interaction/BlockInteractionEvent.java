package mcchickenstudio.creative.coding.blocks.events.player.interaction;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class BlockInteractionEvent extends CreativeEvent {

    public BlockInteractionEvent(Player player) {
        super(player);
    }

}
