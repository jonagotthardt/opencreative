package mcchickenstudio.creative.coding.blocks.events.player.inventory;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class ItemDropEvent extends CreativeEvent {

    public ItemDropEvent(Player player) {
        super(player);
    }

}
