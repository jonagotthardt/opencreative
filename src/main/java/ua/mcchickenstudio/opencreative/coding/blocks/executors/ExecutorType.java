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

package ua.mcchickenstudio.opencreative.coding.blocks.executors;

import org.bukkit.block.sign.Side;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.entities.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.state.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.fightning.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.interaction.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.blocks.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.entities.EntitySpawnExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.fightning.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.interaction.EntityInteractedBlockExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.interaction.FireworkExplodedExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.interaction.PiglinBarteredExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.interaction.TurtleLaysEggExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.movement.EndermanEscapedExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.movement.EntityEnteredBlockExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.movement.EntityJumpedExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.movement.HorseJumpedExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.state.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Cycle;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Function;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Method;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.fighting.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.interaction.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.world.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.world.blocks.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.world.other.*;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>ExecutorType</h1>
 * This enum defines different types of all executors in coding.
 * Every type contains event class, executor class, item material, cancellable.
 * Foe example: PLAYER_JOIN, WORLD_START, FUNCTION.
 * @since 5.0
 * @version 5.6
 * @author McChicken Studio
 */
public enum ExecutorType {

    // Other

    FUNCTION(               ExecutorCategory.FUNCTION, Function.class),
    METHOD(                 ExecutorCategory.METHOD, Method.class),
    CYCLE(                  ExecutorCategory.CYCLE, Cycle.class),

    // Player Executors

    PLAYER_JOIN(            ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, JoinExecutor.class, JoinEvent.class, Material.POTATO),
    PLAYER_QUIT(            ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, QuitExecutor.class, QuitEvent.class, Material.POISONOUS_POTATO),
    PLAYER_LIKED(           ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, LikeExecutor.class, LikeEvent.class, Material.DIAMOND),
    PLAYER_ADVERTISED(      ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, AdvertisedExecutor.class, AdvertisedEvent.class, Material.BEACON),
    PLAYER_PLAY(            ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, PlayExecutor.class, PlayEvent.class, Material.COAL),
    PLAYER_CHAT(            ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, ChatExecutor.class, ChatEvent.class, Material.BOOK),
    PLAYER_PURCHASE(        ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, PurchaseExecutor.class, PlayerPurchaseEvent.class, Material.GOLD_BLOCK),
    PLAYER_CHUNK_LOAD(      ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, ChunkLoadExecutor.class, ChunkLoadEvent.class, Material.DIRT_PATH),
    PLAYER_CHUNK_UNLOAD(    ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, ChunkUnloadExecutor.class, ChunkUnloadEvent.class, Material.RED_STAINED_GLASS),

    PLAYER_LEFT_CLICK(      ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, LeftClickExecutor.class, LeftClickEvent.class, Material.GOLDEN_PICKAXE),
    PLAYER_RIGHT_CLICK(     ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, RightClickExecutor.class, RightClickEvent.class, Material.DIAMOND_PICKAXE),
    PLAYER_INTERACT(        ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, WorldInteractExecutor.class, WorldInteractEvent.class, Material.GOLDEN_HOE),

    PLAYER_PLACE_BLOCK(     ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, PlaceBlockExecutor.class, PlaceBlockEvent.class, Material.GRASS_BLOCK),
    PLAYER_DESTROY_BLOCK(   ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, DestroyBlockExecutor.class, DestroyBlockEvent.class, Material.STONE),
    PLAYER_DESTROYING_BLOCK(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, DamageBlockExecutor.class, DamageBlockEvent.class, Material.COBBLESTONE),
    PLAYER_BLOCK_INTERACT(  ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, BlockInteractionExecutor.class, BlockInteractionEvent.class, Material.CHEST),
    PLAYER_MOB_INTERACT(    ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, MobInteractionExecutor.class, MobInteractionEvent.class, Material.VILLAGER_SPAWN_EGG),
    PLAYER_BED_ENTER(       ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, BedEnterExecutor.class, BedEnterEvent.class, Material.RED_BED),
    PLAYER_BED_LEAVE(       ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, BedLeaveExecutor.class, BedLeaveEvent.class, Material.ORANGE_BED),
    PLAYER_FISHING(         ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, FishExecutor.class, FishEvent.class, Material.FISHING_ROD),
    PLAYER_SPECTATING(      ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, StartSpectatingExecutor.class, StartSpectatingEvent.class, Material.GLASS),
    PLAYER_STOP_SPECTATING( ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, StopSpectatingExecutor.class, StopSpectatingEvent.class, Material.GLASS_PANE),
    PLAYER_CHANGED_SIGN(    ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, ChangedSignExecutor.class, ChangedSignEvent.class, Material.OAK_SIGN),

    PLAYER_OPEN_INVENTORY(  ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, OpenInventoryExecutor.class, OpenInventoryEvent.class, Material.CHEST),
    PLAYER_CLICK_INVENTORY( ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemClickExecutor.class, ItemClickEvent.class, Material.TRIPWIRE_HOOK),
    PLAYER_DRAG_ITEM(       ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemMoveExecutor.class, ItemMoveEvent.class, Material.PAPER),
    PLAYER_SWAP_HAND(       ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemChangeExecutor.class, ItemChangeEvent.class, Material.SHIELD),
    PLAYER_WRITE_BOOK(      ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, BookWriteExecutor.class, BookWriteEvent.class, Material.WRITABLE_BOOK),
    PLAYER_CHANGE_SLOT(     ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, SlotChangeExecutor.class, SlotChangeEvent.class, Material.SLIME_BALL),
    PLAYER_DROP_ITEM(       ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemDropExecutor.class, ItemDropEvent.class, Material.HOPPER),
    PLAYER_PICKUP_ITEM(     ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemPickupExecutor.class, ItemPickupEvent.class, Material.GLOWSTONE_DUST),
    PLAYER_CLOSE_INVENTORY( ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, CloseInventoryExecutor.class, CloseInventoryEvent.class, Material.STRUCTURE_VOID),
    PLAYER_ITEM_CONSUME(    ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemConsumeExecutor.class, ItemConsumeEvent.class, Material.BREAD),
    PLAYER_ITEM_CRAFT(      ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemCraftExecutor.class, PlayerItemCraftEvent.class, Material.CRAFTING_TABLE),
    PLAYER_ITEM_DAMAGE(     ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemDamageExecutor.class, PlayerItemDamagedEvent.class, Material.DEAD_BUSH),
    PLAYER_ITEM_BREAK(      ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemBreakExecutor.class, ItemBreakEvent.class, Material.GOLDEN_PICKAXE),

    PLAYER_GET_DAMAGED(     ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerDamagedExecutor.class, PlayerDamagedEvent.class, Material.DEAD_BUSH),
    MOB_DAMAGE_PLAYER(      ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, MobDamagesPlayerExecutor.class, MobDamagesPlayerEvent.class, Material.ZOMBIE_HEAD),
    PLAYER_DAMAGE_MOB(      ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerDamagesMobExecutor.class, PlayerDamagesMobEvent.class, Material.SKELETON_SKULL),
    PLAYER_DAMAGE_PLAYER(   ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerDamagesPlayerExecutor.class, PlayerDamagesPlayerEvent.class, Material.PLAYER_HEAD),
    PLAYER_HUNGER_CHANGE(   ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, HungerChangeExecutor.class, HungerChangeEvent.class, Material.COOKED_CHICKEN),
    PLAYER_KILLED_PLAYER(   ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerKilledPlayerExecutor.class, PlayerKilledPlayerEvent.class, Material.DIAMOND_SWORD),
    PLAYER_KILLED_MOB(      ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerKilledMobExecutor.class, PlayerKilledMobEvent.class, Material.IRON_AXE),
    PLAYER_DEATH(           ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerDeathExecutor.class, PlayerDeathEvent.class,Material.REDSTONE),
    PLAYER_RESPAWN(         ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerRespawnExecutor.class, PlayerRespawnEvent.class, Material.NETHER_STAR),
    PLAYER_TOTEM_RESPAWN(   ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerTotemRespawnExecutor.class, PlayerTotemRespawnEvent.class, Material.TOTEM_OF_UNDYING),

    PLAYER_WALK(            ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, PlayerMoveExecutor.class, PlayerMoveEvent.class, Material.LEATHER_BOOTS),
    PLAYER_JUMP(            ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, JumpExecutor.class, JumpEvent.class, Material.RABBIT_FOOT),
    PLAYER_RUNNING(         ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StartRunningExecutor.class, StartRunningEvent.class, Material.GOLDEN_BOOTS),
    PLAYER_STOP_RUNNING(    ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StopRunningExecutor.class, StopRunningEvent.class, Material.CHAINMAIL_BOOTS),
    PLAYER_FLYING(          ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StartFlyingExecutor.class, StartFlyingEvent.class, Material.FEATHER),
    PLAYER_STOP_FLYING(     ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StopFlyingExecutor.class, StopFlyingEvent.class, Material.FEATHER),
    PLAYER_SNEAKING(        ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StartSneakingExecutor.class, StartSneakingEvent.class, Material.CHAINMAIL_LEGGINGS),
    PLAYER_STOP_SNEAKING(   ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StopSneakingExecutor.class, StopSneakingEvent.class, Material.IRON_LEGGINGS),
    PLAYER_TELEPORT(        ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, TeleportExecutor.class, TeleportEvent.class, Material.ENDER_PEARL),

    WORLD_PLAY_MODE(        ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, GamePlayExecutor.class, GamePlayEvent.class, Material.EMERALD),
    WORLD_VARIABLE_TRANSFER(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, VariableTransferExecutor.class, VariableTransferEvent.class, Material.CALIBRATED_SCULK_SENSOR),
    WORLD_WEB_RESPONSE(     ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, WebResponseExecutor.class, WebResponseEvent.class, Material.BEACON),
    WORLD_LIGHTNING_STRIKE(     ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, LightningStrikeExecutor.class, LightningStrikeEvent.class, Material.TRIDENT),
    WORLD_REACHED_REDSTONE_LIMIT(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, LimitReachedRedstoneExecutor.class, LimitReachedRedstoneEvent.class, Material.REDSTONE),
    WORLD_REACHED_BLOCKS_LIMIT( ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, LimitReachedBlocksExecutor.class, LimitReachedBlocksEvent.class, Material.GRASS_BLOCK),
    WORLD_REACHED_ENTITIES_LIMIT(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, LimitReachedEntitiesExecutor.class, LimitReachedEntitiesEvent.class, Material.CHICKEN_SPAWN_EGG),
    WORLD_REACHED_VARIABLES_LIMIT(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, LimitReachedVariablesExecutor.class, LimitReachedVariablesEvent.class, Material.MAGMA_CREAM),

    WORLD_BLOCK_BURNED(     ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockBurnedExecutor.class, BlockBurnedEvent.class, Material.CAMPFIRE),
    WORLD_BLOCK_COOKED(     ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_INVENTORY, BlockCookedExecutor.class, BlockCookedEvent.class, Material.COOKED_CHICKEN),
    WORLD_BLOCK_FURNACE_BURNED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_INVENTORY, BlockFurnaceBurnedExecutor.class, BlockFurnaceBurnedEvent.class, Material.COAL),
    WORLD_BLOCK_DISPENSED(  ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_INVENTORY, BlockDispensedExecutor.class, BlockDispensedEvent.class, Material.DROPPER),
    WORLD_BLOCK_BREWING_FUEL(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_INVENTORY, BlockBrewingFuelExecutor.class, BlockBrewingFuelEvent.class, Material.BLAZE_POWDER),
    WORLD_BLOCK_CRAFTER_CRAFTED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_INVENTORY, BlockCrafterCraftedExecutor.class, BlockCrafterCraftedEvent.class, Material.CRAFTER),

    WORLD_BLOCK_EXPLODED(   ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockExplodedExecutor.class, BlockExplodedEvent.class, Material.TNT),
    WORLD_BLOCK_TNT_PRIME(  ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockTntPrimeExecutor.class, BlockTntPrimeEvent.class, Material.TNT_MINECART),
    WORLD_BLOCK_EXPERIENCE_DROP(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockExperienceDropExecutor.class, BlockExperienceDropEvent.class, Material.EXPERIENCE_BOTTLE),
    WORLD_BLOCK_FADED(      ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockFadedExecutor.class, BlockFadedEvent.class, Material.ICE),
    WORLD_BLOCK_FORMED(     ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockFormedExecutor.class, BlockFormedEvent.class, Material.SNOW_BLOCK),
    WORLD_BLOCK_GROWN(      ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockGrownExecutor.class, BlockGrownEvent.class, Material.WHEAT),
    WORLD_BLOCK_IGNITED(    ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockIgnitedExecutor.class, BlockIgnitedEvent.class, Material.FLINT_AND_STEEL),
    WORLD_BLOCK_PHYSICS(    ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockPhysicsExecutor.class, BlockPhysicsEvent.class, Material.SAND),
    WORLD_BLOCK_REDSTONE(   ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockRedstoneExecutor.class, BlockRedstoneEvent.class, Material.REDSTONE),
    WORLD_PORTAL_CREATED(       ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, PortalCreatedExecutor.class, PortalCreatedEvent.class, Material.CRYING_OBSIDIAN),
    WORLD_BLOCK_PISTON_EXTENDED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockPistonExtendedExecutor.class, BlockPistonExtendedEvent.class, Material.STICKY_PISTON),
    WORLD_BLOCK_PISTON_RETRACTED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockPistonRetractedExecutor.class, BlockPistonRetractedEvent.class, Material.PISTON),
    WORLD_BLOCK_BREWING_START(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockBrewingStartExecutor.class, BlockBrewingStartEvent.class, Material.BREWING_STAND),
    WORLD_BLOCK_BREWING_END(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockBrewingEndExecutor.class, BlockBrewingEndEvent.class, Material.POTION),
    WORLD_BLOCK_CAMPFIRE_START(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockCampfireStartExecutor.class, BlockCampfireStartEvent.class, Material.CAMPFIRE),
    WORLD_BLOCK_CAULDRON_CHANGE(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockCauldronChangeExecutor.class, BlockCauldronChangeEvent.class, Material.CAULDRON),
    WORLD_BLOCK_FLUID_CHANGED(  ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockFluidChangeExecutor.class, BlockFluidChangeEvent.class, Material.WATER_BUCKET),
    WORLD_BLOCK_LEAVES_DECAYED( ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockLeavesDecayedExecutor.class, BlockLeavesDecayedEvent.class, Material.OAK_LEAVES),
    WORLD_BLOCK_NOTE_PLAYED(    ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockNotePlayedExecutor.class, BlockNotePlayedEvent.class, Material.NOTE_BLOCK),
    WORLD_BLOCK_SCULK_BLOOMED(  ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockSculkBloomedExecutor.class, BlockSculkBloomedEvent.class, Material.SCULK_SENSOR),
    WORLD_BLOCK_BEACON_ACTIVATED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockBeaconActivatedExecutor.class, BlockBeaconActivatedEvent.class, Material.BEACON),
    WORLD_BLOCK_BEACON_DEACTIVATED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockBeaconDeactivatedExecutor.class, BlockBeaconActivatedEvent.class, Material.BEACON),
    WORLD_BLOCK_ANVIL_DAMAGED(  ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockAnvilDamagedExecutor.class, BlockAnvilDamagedEvent.class, Material.DAMAGED_ANVIL),
    WORLD_BLOCK_TARGET_HIT(     ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockTargetHitExecutor.class, BlockTargetHitEvent.class, Material.TARGET),
    WORLD_BLOCK_SPONGE_ABSORBED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockSpongeAbsorbedExecutor.class, BlockSpongeAbsorbedEvent.class, Material.WET_SPONGE),
    WORLD_BLOCK_BELL_RUNG(      ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockBellRungExecutor.class, BlockBellRungEvent.class, Material.BELL),
    //WORLD_WEATHER_CHANGED(      ExecutorCategory.EVENT_WORLD, MenusCategory.OTHER, null, null, Material.WATER_BUCKET),
    //WORLD_CODE_ERROR_OCCURRED(  ExecutorCategory.EVENT_WORLD, MenusCategory.OTHER, null, null, Material.BARRIER),

    ENTITY_PIG_ZOMBIE_ANGERED(      ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, PigZombieAngeredExecutor.class, PigZombieAngeredEvent.class, Material.ZOMBIFIED_PIGLIN_SPAWN_EGG),
    ENTITY_BAT_TOGGLED_SLEEP_MODE(  ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, EntityBatToggledSleepModeExecutor.class, EntityBatToggledSleepModeEvent.class, Material.BAT_SPAWN_EGG),
    ENTITY_SLIME_SPLIT(             ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, SlimeSplittedExecutor.class, SlimeSplittedEvent.class, Material.SLIME_SPAWN_EGG),
    ENTITY_WITCH_READY_POTION(      ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, WitchReadyPotionExecutor.class, WitchReadyPotionEvent.class, Material.SPLASH_POTION),
    ENTITY_SHEEP_REGROWN_WOOL(      ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, SheepRegrownWoolExecutor.class, SheepRegrownWoolEvent.class, Material.PINK_WOOL),
    ENTITY_PUFFERFISH_STATE_CHANGED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, PufferfishStateChangedExecutor.class, PufferfishStateChangedEvent.class, Material.PUFFERFISH),
    ENTITY_CREEPER_IGNITED(         ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, CreeperIgnitedExecutor.class, CreeperIgnitedEvent.class, Material.CREEPER_HEAD),
    ENTITY_CREEPER_POWERED(         ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, CreeperPoweredExecutor.class, CreeperPoweredEvent.class, Material.CREEPER_SPAWN_EGG),
    ENTITY_ENTERED_LOVE_MODE(       ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, EntityEnteredLoveModeExecutor.class, EntityEnteredLoveModeEvent.class, Material.WHEAT),
    ENTITY_TURTLE_GOES_HOME(        ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, TurtleGoesHomeExecutor.class, TurtleGoesHomeEvent.class, Material.TURTLE_SPAWN_EGG),
    ENTITY_RESURRECTED(             ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, EntityResurrectedExecutor.class, EntityResurrectedEvent.class, Material.TOTEM_OF_UNDYING),
    ENTITY_POTION_EFFECTED(         ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, EntityPotionEffectedExecutor.class, EntityPotionEffectedEvent.class, Material.POTION),
    ENTITY_WARDEN_ANGER_CHANGED(    ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, WardenAngerChangedExecutor.class, WardenAngerChangedEvent.class, Material.WARDEN_SPAWN_EGG),
    ENTITY_AIR_CHANGED(             ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, EntityAirChangedExecutor.class, EntityAirChangedEvent.class, Material.WATER_BUCKET),

    ENTITY_ENTERED_BLOCK(           ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, EntityEnteredBlockExecutor.class, EntityEnteredBlockEvent.class, Material.SILVERFISH_SPAWN_EGG),
    ENTITY_JUMPED(                  ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, EntityJumpedExecutor.class, EntityJumpedEvent.class, Material.RABBIT_FOOT),
    ENTITY_HORSE_JUMPED(            ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, HorseJumpedExecutor.class, HorseJumpedEvent.class, Material.HORSE_SPAWN_EGG),
    ENTITY_ENDERMAN_ESCAPED(        ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, EndermanEscapedExecutor.class, EndermanEscapedEvent.class, Material.ENDERMAN_SPAWN_EGG),

    ENTITY_SPAWNED(                 ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntitySpawnExecutor.class, EntitySpawnEvent.class, Material.CHICKEN_SPAWN_EGG),
    ENTITY_DROPPED_ITEM(            ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntityDroppedItemExecutor.class, EntityDroppedItemEvent.class, Material.GUNPOWDER),
    ENTITY_PICKED_UP_ITEM(          ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntityPickedUpItemExecutor.class, EntityPickedUpItemEvent.class, Material.GLOWSTONE_DUST),
    ENTITY_ITEM_MERGED(             ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, ItemMergedExecutor.class, ItemMergedEvent.class, Material.BEETROOT_SEEDS),
    ENTITY_ITEM_DESPAWNED(          ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, ItemDespawnedExecutor.class, ItemDespawnedEvent.class, Material.STRUCTURE_VOID),
    ENTITY_DAMAGED_ITEM(            ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntityDamagedItemExecutor.class, EntityDamagedItemEvent.class, Material.GOLDEN_PICKAXE),
    ENTITY_PIGLIN_BARTERED(         ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, PiglinBarteredExecutor.class, PiglinBarteredEvent.class, Material.PIGLIN_HEAD),
    ENTITY_INTERACTED_BLOCK(        ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntityInteractedBlockExecutor.class, EntityInteractedBlockEvent.class, Material.CRAFTING_TABLE),
    ENTITY_TURTLE_LAYS_EGG(         ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, TurtleLaysEggExecutor.class, TurtleLaysEggEvent.class, Material.TURTLE_EGG),
    ENTITY_FIREWORK_EXPLODED(       ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, FireworkExplodedExecutor.class, FireworkExplodedEvent.class, Material.FIREWORK_ROCKET),

    ENTITY_DIED(                    ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, EntityDiedExecutor.class, EntityDiedEvent.class, Material.REDSTONE),
    ENTITY_SHOT_BOW(                ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, EntityShotBowExecutor.class, EntityShotBowEvent.class, Material.BOW),
    ENTITY_WITCH_THROWN_POTION(     ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, WitchThrownPotionExecutor.class, WitchThrownPotionEvent.class, Material.SPLASH_POTION),
    ENTITY_WITCH_CONSUMED_POTION(   ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, WitchConsumedPotionExecutor.class, WitchConsumedPotionEvent.class, Material.POTION),
    ENTITY_LOADED_CROSSBOW(         ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, EntityLoadedCrossbowExecutor.class, EntityLoadedCrossbowEvent.class, Material.CROSSBOW),
    ;

    private final Class<? extends Executor> executor;
    private final Class<? extends WorldEvent> creativeEvent;
    private final ExecutorCategory category;
    private final MenusCategory menusCategory;
    private final Material material;

    ExecutorType(ExecutorCategory category, Class<? extends Executor> executor) {
        this.executor = executor;
        this.menusCategory = null;
        this.creativeEvent = null;
        this.category = category;
        this.material = null;
    }

    ExecutorType(ExecutorCategory category, MenusCategory menusCategory, Class<? extends Executor> executor, Class<? extends WorldEvent> event, Material material) {
        this.executor = executor;
        this.menusCategory = menusCategory;
        this.creativeEvent = event;
        this.category = category;
        this.material = material;
    }

    public boolean isDisabled() {
        return getExecutorClass() == null || OpenCreative.getSettings().isDisabledEvent(this);
    }

    public final ItemStack getIcon() {
        ItemStack icon = createItem(this.material, 1, "items.developer.events." + this.name().toLowerCase().replace("_","-"));
        icon = addLoreAtEnd(icon,(isCancellable() ? getLocaleMessage("items.developer.events.cancellable",false) : ""));
        if (isDisabled()) {
            icon.setType(Material.LIGHT_GRAY_STAINED_GLASS);
            icon = addLoreAtEnd(icon,getLocaleMessage("disabled"));
        }
        setPersistentData(icon,getCodingValueKey(),name());
        return icon;
    }

    public final String getLocaleName() {
        return getLocaleMessage("items.developer.events." + this.name().toLowerCase().replace("_","-") + ".name", false);
    }

    public final Class<? extends WorldEvent> getEventClass() {
        return this.creativeEvent;
    }

    public final Class<? extends Executor> getExecutorClass() {
        return this.executor;
    }

    public static ExecutorType getType(Block block) {
        if (block.getType() == Material.LAPIS_BLOCK) {
            return FUNCTION;
        } else if (block.getType() == Material.EMERALD_BLOCK) {
            return METHOD;
        } else if (block.getType() == Material.OXIDIZED_COPPER) {
            return CYCLE;
        }
        Block signBlock = block.getRelative(BlockFace.SOUTH);
        if (signBlock.getType().toString().contains("WALL_SIGN")) {
            Sign sign = (Sign) signBlock.getState();
            if (sign.getSide(Side.FRONT).lines().size() >= 3) {
                Component signText = sign.getSide(Side.FRONT).line(2);
                String text = ((TextComponent) signText).content().toUpperCase();
                return getType(text);
            }
        }
        return null;
    }

    public static @Nullable ExecutorType getType(@NotNull String text) {
        for (ExecutorType executorType : values()) {
            if (executorType.name().equalsIgnoreCase(text)) return executorType;
        }
        return null;
    }

    public static Set<MenusCategory> getMenusCategories(ExecutorCategory executorCategory) {
        Set<MenusCategory> set = new HashSet<>();
        for (ExecutorType executorType : values()) {
            if (executorType.category == executorCategory) {
                set.add(executorType.menusCategory);
            }
        }
        return set;
    }

    public static List<ExecutorType> getExecutorsByCategories(ExecutorCategory executorCategory, MenusCategory menusCategory) {
        List<ExecutorType> list = new ArrayList<>();
        for (ExecutorType executorType : values()) {
            if (executorType.category == executorCategory && executorType.menusCategory == menusCategory) {
                list.add(executorType);
            }
        }
        return list;
    }

    public ExecutorCategory getCategory() {
        return category;
    }

    public boolean isCancellable() {
        Class<?> eventClass = getEventClass();
        return (eventClass != null && Cancellable.class.isAssignableFrom(eventClass));
    }

}

