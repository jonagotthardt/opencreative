package mcchickenstudio.creative.coding.blocks.events.player.interaction;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class MobInteractionEvent extends CreativeEvent {

    public MobInteractionEvent(Player player) {
        super(player);
    }

}
