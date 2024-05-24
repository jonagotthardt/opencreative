package mcchickenstudio.creative.coding.blocks.events.player.fighting;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class HungerChangeEvent extends CreativeEvent {

    public HungerChangeEvent(Player player) {
        super(player);
    }

}
