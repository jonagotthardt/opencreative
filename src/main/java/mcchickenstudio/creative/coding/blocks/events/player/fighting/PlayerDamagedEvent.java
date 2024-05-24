package mcchickenstudio.creative.coding.blocks.events.player.fighting;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class PlayerDamagedEvent extends CreativeEvent {

    public PlayerDamagedEvent(Player player) {
        super(player);
    }

}
