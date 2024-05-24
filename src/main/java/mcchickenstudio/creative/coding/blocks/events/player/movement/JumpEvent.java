package mcchickenstudio.creative.coding.blocks.events.player.movement;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class JumpEvent extends CreativeEvent {

    public JumpEvent(Player player) {
        super(player);
    }

}
