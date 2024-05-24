package mcchickenstudio.creative.coding.blocks.events.player.fighting;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import mcchickenstudio.creative.coding.blocks.events.EventVariables;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamagesMobEvent extends CreativeEvent {

    private final Entity damager;
    private final double damage;

    public PlayerDamagesMobEvent(Player player, EntityDamageByEntityEvent event) {
        super(player);
        damager = event.getDamager();
        damage = event.getDamage();
    }

    public double getDamage() {
        return damage;
    }

    public Entity getDamager() {
        return damager;
    }
}
