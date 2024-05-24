package mcchickenstudio.creative.coding.blocks.events.player.interaction;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class DamageBlockEvent extends CreativeEvent {

    public DamageBlockEvent(Player player) {
        super(player);
    }

}
