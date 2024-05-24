package mcchickenstudio.creative.coding.blocks.events.player.fighting;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class PlayerRespawnEvent extends CreativeEvent {

    public PlayerRespawnEvent(Player player) {
        super(player);
    }

}
