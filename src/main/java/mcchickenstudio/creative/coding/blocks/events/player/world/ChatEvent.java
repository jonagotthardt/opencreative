package mcchickenstudio.creative.coding.blocks.events.player.world;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatEvent extends CreativeEvent {

    private final String message;

    public ChatEvent(Player player, PlayerChatEvent event) {
        super(player);
        message = event.getMessage();
    }

    public String getMessage() {
        return message;
    }
}
