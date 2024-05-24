package mcchickenstudio.creative.coding.blocks.events.player.inventory;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class BookWriteEvent extends CreativeEvent {

    public BookWriteEvent(Player player) {
        super(player);
    }

}
