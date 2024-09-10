
/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package mcchickenstudio.creative.events.player;

import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.plots.PlotFlags;
import mcchickenstudio.creative.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import mcchickenstudio.creative.plots.Plot;

import mcchickenstudio.creative.plots.PlotManager;

import java.util.HashMap;
import java.util.Map;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class PlayerDeath implements Listener {

    public static final Map<Player, Location> deathLocations = new HashMap<>();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        event.deathMessage(null);
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot != null) {
            deathLocations.put(player, plot.world.getSpawnLocation());
            if (plot.getFlagValue(PlotFlags.PlotFlag.DEATH_MESSAGES) == 1) {
                for (Player p : plot.getPlayers()) {
                    p.sendMessage("§7 " + player.getName() + "§f " + translateDeathMessage(player));
                }
            }
            event.getDrops().remove(createItem(Material.COMPASS,1,"items.developer.world-settings"));
            EventRaiser.raisePlayerDeathEvent(event.getPlayer(),event);
        } else {
            event.setKeepInventory(true);
            PlayerUtils.teleportToLobby(player);
        }
        player.sendTitle(getLocaleMessage("deaths.title",false),"§7 " + player.getName() + "§f " + translateDeathMessage(player));
    }

    private String translateDeathMessage(Player player ) {
        EntityDamageEvent damageEvent = player.getLastDamageCause();
        if (damageEvent == null) return getLocaleMessage("deaths.custom");
        Entity damager = player.getKiller();
        if (damageEvent instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            damager = damageByEntityEvent.getDamager();
        }
        return switch (damageEvent.getCause()) {
            case BLOCK_EXPLOSION -> getLocaleMessage("deaths.block-explosion");
            case CONTACT -> getLocaleMessage("deaths.contact");
            case CRAMMING -> getLocaleMessage("deaths.cramming");
            case DRAGON_BREATH -> getLocaleMessage("deaths.dragon-breath");
            case DROWNING -> getLocaleMessage("deaths.drowning");
            case DRYOUT -> getLocaleMessage("deaths.dryout");
            case ENTITY_ATTACK ->
                    getLocaleMessage("deaths.entity-attack").replace("%entity%", (damager == null ? "" : damager.getName().substring(0, Math.min(damager.getName().length(),30))));
            case ENTITY_EXPLOSION ->
                    getLocaleMessage("deaths.entity-explosion").replace("%entity%", (damager == null ? "" : damager.getName().substring(0, Math.min(damager.getName().length(),30))));
            case ENTITY_SWEEP_ATTACK ->
                    getLocaleMessage("deaths.entity-sweep-attack").replace("%entity%", (damager == null ? "" : damager.getName().substring(0, Math.min(damager.getName().length(),30))));
            case FALL -> getLocaleMessage("deaths.fall");
            case FALLING_BLOCK -> getLocaleMessage("deaths.falling-block");
            case FIRE -> getLocaleMessage("deaths.fire");
            case FIRE_TICK -> getLocaleMessage("deaths.fire-tick");
            case FLY_INTO_WALL -> getLocaleMessage("deaths.fly-into-wall");
            case HOT_FLOOR -> getLocaleMessage("deaths.hot-floor");
            case LAVA -> getLocaleMessage("deaths.lava");
            case LIGHTNING -> getLocaleMessage("deaths.lightning");
            case MAGIC -> getLocaleMessage("deaths.magic");
            case MELTING -> getLocaleMessage("deaths.melting");
            case POISON -> getLocaleMessage("deaths.poison");
            case PROJECTILE -> getLocaleMessage("deaths.projectile");
            case STARVATION -> getLocaleMessage("deaths.starvation");
            case SUFFOCATION -> getLocaleMessage("deaths.suffocation");
            case SUICIDE -> getLocaleMessage("deaths.suicide");
            case THORNS -> getLocaleMessage("deaths.thorns");
            case VOID -> getLocaleMessage("deaths.void");
            case WITHER -> getLocaleMessage("deaths.wither");
            default -> getLocaleMessage("deaths.custom");
        };
    }
}
