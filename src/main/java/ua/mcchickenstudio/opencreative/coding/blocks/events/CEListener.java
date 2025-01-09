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

import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.entities.EntitySpawnEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.GamePlayEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.VariableTransferEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.WebResponseEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executors;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * <h1>CEListener</h1>
 * This class represents listener of all Creative events in planet.
 * It activates executors on events listening.
 */
public class CEListener implements Listener {

    // Player Events
    // World

    @EventHandler
    public void onJoin(JoinEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onQuit(QuitEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onLike(LikeEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onAdvertise(AdvertisedEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onPlay(PlayEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onChat(ChunkLoadEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onChat(ChunkUnloadEvent event) {
        Executors.activate(event);
    }

    // Movement

    @EventHandler
    public void onJump(JumpEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onFlying(StartFlyingEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onRunning(StartRunningEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onSneaking(StartSneakingEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onStopFlying(StopFlyingEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onStopRunning(StopRunningEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onStopSneaking(StopSneakingEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onTeleport(TeleportEvent event) {
        Executors.activate(event);
    }

    // Inventory

    @EventHandler
    public void onBookWrite(BookWriteEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onCloseInventory(CloseInventoryEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onItemChange(ItemChangeEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onItemClick(ItemClickEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onItemDrop(ItemDropEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onItemMove(ItemMoveEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onItemPickup(ItemPickupEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onOpenInventory(OpenInventoryEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onSlotChange(SlotChangeEvent event) {
        Executors.activate(event);
    }

    // Interaction

    @EventHandler
    public void onBlockInteract(BlockInteractionEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onDamageBlock(DamageBlockEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onDestroyBlock(DestroyBlockEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onFishing(FishEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onLeftClick(LeftClickEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onMobInteract(MobInteractionEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onPlaceBlock(PlaceBlockEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onRightClick(RightClickEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onSpectating(StartSpectatingEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onStopSpectating(StopSpectatingEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onWorldInteract(WorldInteractEvent event) {
        Executors.activate(event);
    }

    // Fighting

    @EventHandler
    public void onHungerChange(HungerChangeEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onMobDamagePlayer(MobDamagesPlayerEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onDamage(PlayerDamagedEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onPlayerDamagedMob(PlayerDamagesMobEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onPlayerDamagedPlayer(PlayerDamagesPlayerEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onPlayerKilledPlayer(PlayerKilledPlayerEvent event) {
        Executors.activate(event);
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onTotemRespawn(PlayerTotemRespawnEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onPurchase(PlayerPurchaseEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onPlayMode(GamePlayEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onTransfer(VariableTransferEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onResponse(WebResponseEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onConsume(ItemConsumeEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onEntitySpawn(ItemBreakEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onBedEvent(BedEnterEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onBedEvent(BedLeaveEvent event) {
        Executors.activate(event);
    }

    @EventHandler
    public void onCraftEvent(PlayerItemCraftEvent event) {
        Executors.activate(event);
    }
}
