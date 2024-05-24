package mcchickenstudio.creative.coding.blocks.events.player.inventory;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class CloseInventoryEvent extends CreativeEvent {

    public CloseInventoryEvent(Player player) {
        super(player);
    }

}
