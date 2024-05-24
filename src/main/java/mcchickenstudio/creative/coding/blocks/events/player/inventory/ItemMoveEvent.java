package mcchickenstudio.creative.coding.blocks.events.player.inventory;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class ItemMoveEvent extends CreativeEvent {

    public ItemMoveEvent(Player player) {
        super(player);
    }

}
