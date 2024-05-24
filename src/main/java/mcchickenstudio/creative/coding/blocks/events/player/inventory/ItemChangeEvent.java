package mcchickenstudio.creative.coding.blocks.events.player.inventory;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class ItemChangeEvent extends CreativeEvent {

    public ItemChangeEvent(Player player) {
        super(player);
    }

}
