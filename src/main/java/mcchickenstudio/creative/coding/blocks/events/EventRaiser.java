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
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.*;
import mcchickenstudio.creative.coding.blocks.events.player.interaction.*;
import mcchickenstudio.creative.coding.blocks.events.player.inventory.*;
import mcchickenstudio.creative.coding.blocks.events.player.movement.*;
import mcchickenstudio.creative.coding.blocks.events.player.world.*;
import mcchickenstudio.creative.events.player.ChangedWorld;
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
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return true;
        if (PlotManager.getInstance().getDevPlot(player) != null) return true;
        if (PlotManager.getInstance().getPlotByPlayer(player).getPlotMode() == Plot.Mode.BUILD) return true;
        return ChangedWorld.isPlayerWithLocation(player);
    }

    public static void raiseChunkLoadEvent(PlayerChunkLoadEvent event) {
        if (canRaiseEvent(event.getPlayer())) {
            return;
        }
        CreativeEvent creativeEvent = new ChunkLoadEvent(event.getPlayer(),event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseChunkUnloadEvent(PlayerChunkUnloadEvent event) {
        if (canRaiseEvent(event.getPlayer())) {
            return;
        }
        CreativeEvent creativeEvent = new ChunkUnloadEvent(event.getPlayer(),event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseJoinEvent(Player player) {
        if (canRaiseEvent(player)) {
            return;
        }
        JoinEvent creativeEvent = new JoinEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseQuitEvent(Player player) {
        if (canRaiseEvent(player)) {
            return;
        }
        QuitEvent creativeEvent = new QuitEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static boolean raisePlayEvent(Player player) {
        PlayEvent creativeEvent = new PlayEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return !creativeEvent.isCancelled();
    }

    public static void raiseLikeEvent(Player player) {
        if (canRaiseEvent(player)) {
            return;
        }
        LikeEvent creativeEvent = new LikeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseAdvertisedEvent(Player player) {
        if (canRaiseEvent(player)) {
            return;
        }
        AdvertisedEvent creativeEvent = new AdvertisedEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseChatEvent(Player player, PlayerChatEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        ChatEvent creativeEvent = new ChatEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    // Movement

    public static void raiseJumpEvent(Player player, PlayerJumpEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        JumpEvent creativeEvent = new JumpEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseMoveEvent(Player player, org.bukkit.event.player.PlayerMoveEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        PlayerMoveEvent creativeEvent = new PlayerMoveEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStartFlyingEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        StartFlyingEvent creativeEvent = new StartFlyingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStopFlyingEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        StopFlyingEvent creativeEvent = new StopFlyingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStartSneakingEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        StartSneakingEvent creativeEvent = new StartSneakingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStopSneakingEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        StopSneakingEvent creativeEvent = new StopSneakingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStartRunningEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        StartRunningEvent creativeEvent = new StartRunningEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStopRunningEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        StopRunningEvent creativeEvent = new StopRunningEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseTeleportEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        TeleportEvent creativeEvent = new TeleportEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    // Inventory

    public static void raiseBookWriteEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        BookWriteEvent creativeEvent = new BookWriteEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseCloseInventoryEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        CloseInventoryEvent creativeEvent = new CloseInventoryEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemChangeEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        ItemChangeEvent creativeEvent = new ItemChangeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemClickEvent(Player player, InventoryClickEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        ItemClickEvent creativeEvent = new ItemClickEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemDropEvent(Player player, PlayerDropItemEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        ItemDropEvent creativeEvent = new ItemDropEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseSlotChangeEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        SlotChangeEvent creativeEvent = new SlotChangeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemMoveEvent(Player player, InventoryClickEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        ItemMoveEvent creativeEvent = new ItemMoveEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemPickupEvent(Player player, EntityPickupItemEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        ItemPickupEvent creativeEvent = new ItemPickupEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseOpenInventoryEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        OpenInventoryEvent creativeEvent = new OpenInventoryEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    // Interaction

    public static void raiseBlockInteractionEvent(Player player, PlayerInteractEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        BlockInteractionEvent creativeEvent = new BlockInteractionEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseDamageBlockEvent(Player player, BlockDamageEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        DamageBlockEvent creativeEvent = new DamageBlockEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseDestroyEvent(Player player, BlockBreakEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        DestroyBlockEvent creativeEvent = new DestroyBlockEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseFishEvent(Player player, PlayerFishEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        FishEvent creativeEvent = new FishEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseLeftClickEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        LeftClickEvent creativeEvent = new LeftClickEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseRightClickEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        RightClickEvent creativeEvent = new RightClickEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStartSpectatingEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        StartSpectatingEvent creativeEvent = new StartSpectatingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStopSpectatingEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        StopSpectatingEvent creativeEvent = new StopSpectatingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseWorldInteractEvent(Player player, PlayerInteractEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        WorldInteractEvent creativeEvent = new WorldInteractEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlaceBlockEvent(Player player, BlockPlaceEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        PlaceBlockEvent creativeEvent = new PlaceBlockEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseMobInteractionEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        MobInteractionEvent creativeEvent = new MobInteractionEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    // Fighting

    public static void raiseHungerChangeEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        HungerChangeEvent creativeEvent = new HungerChangeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseMobDamagesPlayerEvent(Player player, EntityDamageByEntityEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        if (!(bukkitEvent.getEntity() instanceof Player)) {
            return;
        }
        MobDamagesPlayerEvent creativeEvent = new MobDamagesPlayerEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerDamagedEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        PlayerDamagedEvent creativeEvent = new PlayerDamagedEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerDamagedMobEvent(Player player, EntityDamageByEntityEvent bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        PlayerDamagesMobEvent creativeEvent = new PlayerDamagesMobEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerDeathEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        PlayerDeathEvent creativeEvent = new PlayerDeathEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerRespawnEvent(Player player, Event bukkitEvent) {
        if (canRaiseEvent(player)) {
            return;
        }
        PlayerRespawnEvent creativeEvent = new PlayerRespawnEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerTotemRespawnEvent(Entity entity, Event bukkitEvent) {
        if (!(entity instanceof Player player)) return;
        if (canRaiseEvent(player)) {
            return;
        }
        PlayerTotemRespawnEvent creativeEvent = new PlayerTotemRespawnEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerPurchaseEvent(Player player, String id, String name, int price, boolean save) {
        if (canRaiseEvent(player)) {
            return;
        }
        CreativeEvent creativeEvent = new PlayerPurchaseEvent(player,id,name,price,save);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

}
