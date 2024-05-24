package mcchickenstudio.creative.coding.blocks.events.player.inventory;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class SlotChangeEvent extends CreativeEvent {

    public SlotChangeEvent(Player player) {
        super(player);
    }

}
