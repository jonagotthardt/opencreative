package mcchickenstudio.creative.coding.blocks.events.player.inventory;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class ItemClickEvent extends CreativeEvent {

    public ItemClickEvent(Player player) {
        super(player);
    }

}
