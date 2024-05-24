package mcchickenstudio.creative.coding.blocks.events.player.fighting;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;

public class MobDamagesPlayerEvent extends CreativeEvent {

    public MobDamagesPlayerEvent(Player player) {
        super(player);
    }

}
