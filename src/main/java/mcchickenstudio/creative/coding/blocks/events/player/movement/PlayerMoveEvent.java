package mcchickenstudio.creative.coding.blocks.events.player.movement;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class PlayerMoveEvent extends CreativeEvent {

    public PlayerMoveEvent(Player player) {
        super(player);
    }

}
