package mcchickenstudio.creative.coding.blocks.events.player.world;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.entity.Player;

public class JoinEvent extends CreativeEvent {

    public JoinEvent(Player player) {
        super(player);
    }

}
