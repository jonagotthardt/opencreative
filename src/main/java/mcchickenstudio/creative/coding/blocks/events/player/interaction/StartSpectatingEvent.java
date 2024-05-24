package mcchickenstudio.creative.coding.blocks.events.player.interaction;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class StartSpectatingEvent extends CreativeEvent {

    public StartSpectatingEvent(Player player) {
        super(player);
    }

}
