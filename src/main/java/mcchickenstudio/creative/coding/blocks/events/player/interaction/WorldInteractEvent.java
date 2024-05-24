package mcchickenstudio.creative.coding.blocks.events.player.interaction;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class WorldInteractEvent extends CreativeEvent {

    public WorldInteractEvent(Player player) {
        super(player);
    }

}
