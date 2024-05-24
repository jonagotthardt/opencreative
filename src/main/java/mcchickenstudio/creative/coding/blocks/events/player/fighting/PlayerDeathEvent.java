package mcchickenstudio.creative.coding.blocks.events.player.fighting;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class PlayerDeathEvent extends CreativeEvent {

    public PlayerDeathEvent(Player player) {
        super(player);
    }

}
