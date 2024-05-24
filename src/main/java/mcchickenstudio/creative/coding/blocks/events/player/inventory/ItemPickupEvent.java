package mcchickenstudio.creative.coding.blocks.events.player.inventory;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class ItemPickupEvent extends CreativeEvent {

    public ItemPickupEvent(Player player) {
        super(player);
    }

}
