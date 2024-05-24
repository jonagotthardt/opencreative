package mcchickenstudio.creative.coding.blocks.events.player.movement;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class TeleportEvent extends CreativeEvent {

    public TeleportEvent(Player player) {
        super(player);
    }

}
