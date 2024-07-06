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

package mcchickenstudio.creative.coding.blocks.events;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.*;
import mcchickenstudio.creative.coding.blocks.events.player.interaction.*;
import mcchickenstudio.creative.coding.blocks.events.player.inventory.*;
import mcchickenstudio.creative.coding.blocks.events.player.movement.*;
import mcchickenstudio.creative.coding.blocks.events.player.world.*;
import mcchickenstudio.creative.events.ChangedWorld;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventRaiser {

    // Player Events
    // World

    public static boolean canRaiseEvent(Player player) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        if (PlotManager.getInstance().getPlotByPlayer(player).getPlotMode() == Plot.Mode.BUILD) return false;
        if (ChangedWorld.isPlayerWithLocation(player)) return false;
        return true;
    }

    public static boolean raiseJoinEvent(Player player) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        JoinEvent creativeEvent = new JoinEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseQuitEvent(Player player) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        QuitEvent creativeEvent = new QuitEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlayEvent(Player player) {
        PlayEvent creativeEvent = new PlayEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return !creativeEvent.isCancelled();
    }

    public static boolean raiseLikeEvent(Player player) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        LikeEvent creativeEvent = new LikeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseAdvertisedEvent(Player player) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        AdvertisedEvent creativeEvent = new AdvertisedEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseChatEvent(Player player, PlayerChatEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        ChatEvent creativeEvent = new ChatEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    // Movement

    public static boolean raiseJumpEvent(Player player, PlayerJumpEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        JumpEvent creativeEvent = new JumpEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseMoveEvent(Player player, org.bukkit.event.player.PlayerMoveEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        PlayerMoveEvent creativeEvent = new PlayerMoveEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStartFlyingEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        StartFlyingEvent creativeEvent = new StartFlyingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStopFlyingEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        StopFlyingEvent creativeEvent = new StopFlyingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStartSneakingEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        StartSneakingEvent creativeEvent = new StartSneakingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStopSneakingEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        StopSneakingEvent creativeEvent = new StopSneakingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStartRunningEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        StartRunningEvent creativeEvent = new StartRunningEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStopRunningEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        StopRunningEvent creativeEvent = new StopRunningEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseTeleportEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        TeleportEvent creativeEvent = new TeleportEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    // Inventory

    public static boolean raiseBookWriteEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        BookWriteEvent creativeEvent = new BookWriteEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseCloseInventoryEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        CloseInventoryEvent creativeEvent = new CloseInventoryEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseItemChangeEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        ItemChangeEvent creativeEvent = new ItemChangeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseItemClickEvent(Player player, InventoryClickEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        ItemClickEvent creativeEvent = new ItemClickEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseItemDropEvent(Player player, PlayerDropItemEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        ItemDropEvent creativeEvent = new ItemDropEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseSlotChangeEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        SlotChangeEvent creativeEvent = new SlotChangeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseItemMoveEvent(Player player, InventoryClickEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        ItemMoveEvent creativeEvent = new ItemMoveEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseItemPickupEvent(Player player, EntityPickupItemEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        ItemPickupEvent creativeEvent = new ItemPickupEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseOpenInventoryEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        OpenInventoryEvent creativeEvent = new OpenInventoryEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    // Interaction

    public static boolean raiseBlockInteractionEvent(Player player, PlayerInteractEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        BlockInteractionEvent creativeEvent = new BlockInteractionEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseDamageBlockEvent(Player player, BlockDamageEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        DamageBlockEvent creativeEvent = new DamageBlockEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseDestroyEvent(Player player, BlockBreakEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        DestroyBlockEvent creativeEvent = new DestroyBlockEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseFishEvent(Player player, PlayerFishEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        FishEvent creativeEvent = new FishEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseLeftClickEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        LeftClickEvent creativeEvent = new LeftClickEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseRightClickEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        RightClickEvent creativeEvent = new RightClickEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStartSpectatingEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        StartSpectatingEvent creativeEvent = new StartSpectatingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStopSpectatingEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        StopSpectatingEvent creativeEvent = new StopSpectatingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseWorldInteractEvent(Player player, PlayerInteractEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        WorldInteractEvent creativeEvent = new WorldInteractEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlaceBlockEvent(Player player, BlockPlaceEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        PlaceBlockEvent creativeEvent = new PlaceBlockEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseMobInteractionEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        MobInteractionEvent creativeEvent = new MobInteractionEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    // Fighting

    public static boolean raiseHungerChangeEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        HungerChangeEvent creativeEvent = new HungerChangeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseMobDamagesPlayerEvent(Player player, EntityDamageByEntityEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        if (!(bukkitEvent.getEntity() instanceof Player)) {
            return false;
        }
        MobDamagesPlayerEvent creativeEvent = new MobDamagesPlayerEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlayerDamagedEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        PlayerDamagedEvent creativeEvent = new PlayerDamagedEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlayerDamagedMobEvent(Player player, EntityDamageByEntityEvent bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        PlayerDamagesMobEvent creativeEvent = new PlayerDamagesMobEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlayerDeathEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        PlayerDeathEvent creativeEvent = new PlayerDeathEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlayerRespawnEvent(Player player, Event bukkitEvent) {
        if (!canRaiseEvent(player)) {
            return false;
        }
        PlayerRespawnEvent creativeEvent = new PlayerRespawnEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlayerTotemRespawnEvent(Entity entity, Event bukkitEvent) {
        if (!(entity instanceof Player)) return false;
        Player player = (Player) entity;
        if (!canRaiseEvent(player)) {
            return false;
        }
        PlayerTotemRespawnEvent creativeEvent = new PlayerTotemRespawnEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

}
