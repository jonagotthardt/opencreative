package mcchickenstudio.creative.coding.blocks.events.player.interaction;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class RightClickEvent extends CreativeEvent {

    public RightClickEvent(Player player) {
        super(player);
    }

}
