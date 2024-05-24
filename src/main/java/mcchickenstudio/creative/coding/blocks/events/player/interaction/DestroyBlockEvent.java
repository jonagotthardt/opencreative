package mcchickenstudio.creative.coding.blocks.events.player.interaction;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class DestroyBlockEvent extends CreativeEvent {

    public DestroyBlockEvent(Player player) {
        super(player);
    }

}
