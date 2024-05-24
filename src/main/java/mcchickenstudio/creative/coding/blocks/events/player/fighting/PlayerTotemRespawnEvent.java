package mcchickenstudio.creative.coding.blocks.events.player.fighting;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class PlayerTotemRespawnEvent extends CreativeEvent {

    public PlayerTotemRespawnEvent(Player player) {
        super(player);
    }

}
