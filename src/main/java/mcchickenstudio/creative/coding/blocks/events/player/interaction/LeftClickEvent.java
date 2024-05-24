package mcchickenstudio.creative.coding.blocks.events.player.interaction;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class LeftClickEvent extends CreativeEvent {

    public LeftClickEvent(Player player) {
        super(player);
    }

}
