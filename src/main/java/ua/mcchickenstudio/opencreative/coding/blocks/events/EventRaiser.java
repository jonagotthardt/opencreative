/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2025, McChicken Studio, mcchickenstudio@gmail.com
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

package ua.mcchickenstudio.opencreative.coding.blocks.events;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.block.BeaconActivatedEvent;
import io.papermc.paper.event.block.BeaconDeactivatedEvent;
import io.papermc.paper.event.block.TargetHitEvent;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import org.bukkit.block.Block;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.entities.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerRespawnEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.movement.PlayerMoveEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.blocks.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.*;
import ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInDevPlanet;

public class EventRaiser {

    // Player Events
    // World

    public static boolean cantRaiseEvent(Player player) {
        if (OpenCreative.getPlanetsManager().getPlanetByPlayer(player) == null) return true;
        if (OpenCreative.getPlanetsManager().getDevPlanet(player) != null) return true;
        if (OpenCreative.getPlanetsManager().getPlanetByPlayer(player).getMode() == Planet.Mode.BUILD) return true;
        return ChangedWorld.isPlayerWithLocation(player);
    }

    public static boolean cantRaiseEvent(Entity entity) {
        if (OpenCreative.getPlanetsManager().getPlanetByWorld(entity.getWorld()) == null) return true;
        if (OpenCreative.getPlanetsManager().getPlanetByWorld(entity.getWorld()).getMode() == Planet.Mode.BUILD) return true;
        return isEntityInDevPlanet(entity);
    }

    public static boolean cantRaiseEvent(Planet planet) {
        if (planet == null) return true;
        return planet.getMode() == Planet.Mode.BUILD;
    }

    public static void raiseChunkLoadEvent(PlayerChunkLoadEvent event) {
        if (cantRaiseEvent(event.getPlayer())) {
            return;
        }
        WorldEvent creativeEvent = new ChunkLoadEvent(event.getPlayer(),event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseChunkUnloadEvent(PlayerChunkUnloadEvent event) {
        if (cantRaiseEvent(event.getPlayer())) {
            return;
        }
        WorldEvent creativeEvent = new ChunkUnloadEvent(event.getPlayer(),event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseWorldPlayEvent(Planet planet) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new GamePlayEvent(planet);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseVariableTransferEvent(Planet planet, String key, Object value) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new VariableTransferEvent(planet,key,value);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseWebResponseEvent(Planet planet, String url, int code, String text) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new WebResponseEvent(planet,url,code,text);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseEntitySpawnEvent(org.bukkit.event.entity.EntitySpawnEvent event) {
        if (cantRaiseEvent(event.getEntity())) {
            return;
        }
        WorldEvent creativeEvent = new EntitySpawnEvent(event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseJoinEvent(Player player) {
        if (cantRaiseEvent(player)) {
            return;
        }
        JoinEvent creativeEvent = new JoinEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseQuitEvent(Player player) {
        if (cantRaiseEvent(player)) {
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
        if (cantRaiseEvent(player)) {
            return;
        }
        LikeEvent creativeEvent = new LikeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseAdvertisedEvent(Player player) {
        if (cantRaiseEvent(player)) {
            return;
        }
        AdvertisedEvent creativeEvent = new AdvertisedEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static boolean raiseChatEvent(Player player, String message) {
        if (cantRaiseEvent(player)) {
            return true;
        }
        ChatEvent creativeEvent = new ChatEvent(player, message);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return !creativeEvent.isCancelled();
    }

    // Movement

    public static void raiseJumpEvent(Player player, PlayerJumpEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        JumpEvent creativeEvent = new JumpEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseMoveEvent(Player player, org.bukkit.event.player.PlayerMoveEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        PlayerMoveEvent creativeEvent = new PlayerMoveEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStartFlyingEvent(Player player, Event bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        StartFlyingEvent creativeEvent = new StartFlyingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStopFlyingEvent(Player player, Event bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        StopFlyingEvent creativeEvent = new StopFlyingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStartSneakingEvent(Player player, PlayerToggleSneakEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        StartSneakingEvent creativeEvent = new StartSneakingEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStopSneakingEvent(Player player, PlayerToggleSneakEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        StopSneakingEvent creativeEvent = new StopSneakingEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStartRunningEvent(Player player, Event bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        StartRunningEvent creativeEvent = new StartRunningEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStopRunningEvent(Player player, Event bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        StopRunningEvent creativeEvent = new StopRunningEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseTeleportEvent(Player player, Event bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        TeleportEvent creativeEvent = new TeleportEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    // Inventory

    public static void raiseBookWriteEvent(Player player, PlayerEditBookEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        BookWriteEvent creativeEvent = new BookWriteEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemBreakEvent(Player player, PlayerItemBreakEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        WorldEvent creativeEvent = new ItemBreakEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemConsumeEvent(Player player, PlayerItemConsumeEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        WorldEvent creativeEvent = new ItemConsumeEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseCloseInventoryEvent(Player player, Event bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        CloseInventoryEvent creativeEvent = new CloseInventoryEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemChangeEvent(Player player, Event bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        ItemChangeEvent creativeEvent = new ItemChangeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemClickEvent(Player player, InventoryClickEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        ItemClickEvent creativeEvent = new ItemClickEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemDropEvent(Player player, PlayerDropItemEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        ItemDropEvent creativeEvent = new ItemDropEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseSlotChangeEvent(Player player, PlayerItemHeldEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        SlotChangeEvent creativeEvent = new SlotChangeEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemMoveEvent(Player player, InventoryClickEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        ItemMoveEvent creativeEvent = new ItemMoveEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemPickupEvent(Player player, EntityPickupItemEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        ItemPickupEvent creativeEvent = new ItemPickupEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseOpenInventoryEvent(Player player, Event bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        OpenInventoryEvent creativeEvent = new OpenInventoryEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    // Interaction

    public static void raiseBlockInteractionEvent(Player player, PlayerInteractEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        BlockInteractionEvent creativeEvent = new BlockInteractionEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseDamageBlockEvent(Player player, BlockDamageEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        DamageBlockEvent creativeEvent = new DamageBlockEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseDestroyEvent(Player player, BlockBreakEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        DestroyBlockEvent creativeEvent = new DestroyBlockEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseFishEvent(Player player, PlayerFishEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        FishEvent creativeEvent = new FishEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseLeftClickEvent(Player player, PlayerInteractEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        LeftClickEvent creativeEvent = new LeftClickEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseRightClickEvent(Player player, PlayerInteractEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        RightClickEvent creativeEvent = new RightClickEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStartSpectatingEvent(Player player, Event bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        StartSpectatingEvent creativeEvent = new StartSpectatingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseStopSpectatingEvent(Player player, Event bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        StopSpectatingEvent creativeEvent = new StopSpectatingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseWorldInteractEvent(Player player, PlayerInteractEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        WorldInteractEvent creativeEvent = new WorldInteractEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlaceBlockEvent(Player player, BlockPlaceEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        PlaceBlockEvent creativeEvent = new PlaceBlockEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseMobInteractionEvent(Player player, PlayerInteractAtEntityEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        MobInteractionEvent creativeEvent = new MobInteractionEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseMobInteractionEvent(Player player, HangingBreakByEntityEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        MobInteractionEvent creativeEvent = new MobInteractionEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    // Fighting

    public static void raiseHungerChangeEvent(Player player, FoodLevelChangeEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        HungerChangeEvent creativeEvent = new HungerChangeEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseMobDamagesPlayerEvent(Player player, EntityDamageByEntityEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        if (!(bukkitEvent.getEntity() instanceof Player)) {
            return;
        }
        MobDamagesPlayerEvent creativeEvent = new MobDamagesPlayerEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerDamagedEvent(Player player, EntityDamageEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        PlayerDamagedEvent creativeEvent = new PlayerDamagedEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerDamagedMobEvent(Player player, EntityDamageByEntityEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        PlayerDamagesMobEvent creativeEvent = new PlayerDamagesMobEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerDeathEvent(Player player, org.bukkit.event.entity.PlayerDeathEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        PlayerDeathEvent creativeEvent = new PlayerDeathEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerKilledPlayerEvent(Player killer, Player victim, org.bukkit.event.entity.PlayerDeathEvent bukkitEvent) {
        if (cantRaiseEvent(killer) || cantRaiseEvent(victim)) {
            return;
        }
        WorldEvent creativeEvent = new PlayerKilledPlayerEvent(killer,victim,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerKilledMobEvent(Player killer, Entity victim, org.bukkit.event.entity.EntityDeathEvent bukkitEvent) {
        if (cantRaiseEvent(killer) || cantRaiseEvent(victim)) {
            return;
        }
        WorldEvent creativeEvent = new PlayerKilledMobEvent(killer,victim,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerDamagesPlayerEvent(Player damager, Player victim, EntityDamageByEntityEvent bukkitEvent) {
        if (cantRaiseEvent(damager) || cantRaiseEvent(victim)) {
            return;
        }
        WorldEvent creativeEvent = new PlayerDamagesPlayerEvent(damager,victim,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerRespawnEvent(Player player, Event bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        PlayerRespawnEvent creativeEvent = new PlayerRespawnEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerTotemRespawnEvent(Entity entity, Event bukkitEvent) {
        if (!(entity instanceof Player player)) return;
        if (cantRaiseEvent(player)) {
            return;
        }
        PlayerTotemRespawnEvent creativeEvent = new PlayerTotemRespawnEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerPurchaseEvent(Player player, String id, String name, int price, boolean save) {
        if (cantRaiseEvent(player)) {
            return;
        }
        WorldEvent creativeEvent = new PlayerPurchaseEvent(player,id,name,price,save);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerBedEnterEvent(Player player, PlayerBedEnterEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        WorldEvent creativeEvent = new BedEnterEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePlayerBedLeaveEvent(Player player, PlayerBedLeaveEvent bukkitEvent) {
        if (cantRaiseEvent(player)) {
            return;
        }
        WorldEvent creativeEvent = new BedLeaveEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemCraftEvent(Player player, CraftItemEvent event) {
        if (cantRaiseEvent(player)) {
            return;
        }
        WorldEvent creativeEvent = new PlayerItemCraftEvent(player,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseItemDamageEvent(@NotNull Player player, PlayerItemDamageEvent event) {
        if (cantRaiseEvent(player)) {
            return;
        }
        WorldEvent creativeEvent = new PlayerItemDamagedEvent(player,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockBurnedEvent(Planet planet, BlockBurnEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockBurnedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockCookedEvent(Planet planet, BlockCookEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockCookedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockExplodedEvent(Planet planet, BlockExplodeEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockExplodedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockDispensedEvent(Planet planet, BlockDispenseEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockDispensedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockTntPrimeEvent(Planet planet, TNTPrimeEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockTntPrimeEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockFadedEvent(Planet planet, BlockFadeEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockFadedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockPistonExtendedEvent(Planet planet, BlockPistonExtendEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockPistonExtendedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockPistonRetractedEvent(Planet planet, BlockPistonRetractEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockPistonRetractedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockIgnitedEvent(Planet planet, BlockIgniteEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockIgnitedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockGrownEvent(Planet planet, BlockGrowEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockGrownEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockPhysicsEvent(Planet planet, BlockPhysicsEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new ua.mcchickenstudio.opencreative.coding.blocks.events.world.blocks.BlockPhysicsEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockRedstoneEvent(Planet planet, BlockRedstoneEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new ua.mcchickenstudio.opencreative.coding.blocks.events.world.blocks.BlockRedstoneEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockFormedEvent(Planet planet, BlockFormEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockFormedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raisePortalCreatedEvent(Planet planet, PortalCreateEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new PortalCreatedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBeaconActivatedEvent(Planet planet, BeaconActivatedEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockBeaconActivatedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBeaconDeactivatedEvent(Planet planet, BeaconDeactivatedEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockBeaconDeactivatedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseLimitReachedRedstoneEvent(Planet planet) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new LimitReachedRedstoneEvent(planet);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseLimitReachedVariablesEvent(Planet planet) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new LimitReachedVariablesEvent(planet);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseLimitReachedEntitiesEvent(Planet planet) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new LimitReachedEntitiesEvent(planet);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseLimitReachedBlocksEvent(Planet planet) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new LimitReachedBlocksEvent(planet);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseLightningStrikeEvent(Planet planet, LightningStrikeEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.LightningStrikeEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockExperienceDropEvent(Planet planet, BlockExpEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockExperienceDropEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockBrewingEndEvent(Planet planet, BrewEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockBrewingEndEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockFurnaceBurnedEvent(Planet planet, FurnaceBurnEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockFurnaceBurnedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockBrewingFuelEvent(Planet planet, BrewingStandFuelEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockBrewingFuelEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockFluidChangeEvent(Planet planet, FluidLevelChangeEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockFluidChangeEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockCauldronChangeEvent(Planet planet, CauldronLevelChangeEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockCauldronChangeEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockCampfireStartEvent(Planet planet, CampfireStartEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockCampfireStartEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockBrewingStartEvent(Planet planet, BrewingStartEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockBrewingStartEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockAnvilDamagedEvent(Planet planet, AnvilDamagedEvent event, Block anvil) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockAnvilDamagedEvent(planet,event,anvil);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockTargetHitEvent(Planet planet, TargetHitEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockTargetHitEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockNotePlayedEvent(Planet planet, NotePlayEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockNotePlayedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockSculkBloomedEvent(Planet planet, SculkBloomEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockSculkBloomedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockBellRungEvent(Planet planet, BellRingEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockBellRungEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseBlockCrafterCraftedEvent(Planet planet, CrafterCraftEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockCrafterCraftedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseLeavesDecayedEvent(Planet planet, LeavesDecayEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockLeavesDecayedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

    public static void raiseSpongeAbsorbed(Planet planet, SpongeAbsorbEvent event) {
        if (cantRaiseEvent(planet)) {
            return;
        }
        WorldEvent creativeEvent = new BlockSpongeAbsorbedEvent(planet,event);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
    }

}
