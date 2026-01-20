/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.CodingBlockType;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.entities.EntitySpawnEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.fighting.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.interaction.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.state.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.blocks.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Cycle;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Function;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Method;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>ExecutorType</h1>
 * This enum defines different types of all executors in coding.
 * Every type contains event class, executor class, item material, cancellable.
 * Foe example: PLAYER_JOIN, WORLD_START, FUNCTION.
 *
 * @author McChicken Studio
 * @version 6.0
 * @since 5.0
 */
public enum ExecutorType implements CodingBlockType {

    // Other

    FUNCTION(ExecutorCategory.FUNCTION, Function.class),
    METHOD(ExecutorCategory.METHOD, Method.class),
    CYCLE(ExecutorCategory.CYCLE, Cycle.class),

    // Player Executors

    PLAYER_JOIN(ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, JoinEvent.class, Material.POTATO),
    PLAYER_QUIT(ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, QuitEvent.class, Material.POISONOUS_POTATO),
    PLAYER_LIKED(ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, LikeEvent.class, Material.DIAMOND),
    PLAYER_ADVERTISED(ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, AdvertisedEvent.class, Material.BEACON),
    PLAYER_PLAY(ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, PlayEvent.class, Material.COAL),
    PLAYER_CHAT(ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, ChatEvent.class, Material.BOOK),
    PLAYER_PURCHASE(ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, PlayerPurchaseEvent.class, Material.GOLD_BLOCK),
    PLAYER_CHUNK_LOAD(ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, ChunkLoadEvent.class, Material.DIRT_PATH),
    PLAYER_CHUNK_UNLOAD(ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, ChunkUnloadEvent.class, Material.RED_STAINED_GLASS),

    PLAYER_LEFT_CLICK(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, LeftClickEvent.class, Material.GOLDEN_PICKAXE),
    PLAYER_RIGHT_CLICK(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, RightClickEvent.class, Material.DIAMOND_PICKAXE),
    PLAYER_INTERACT(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, WorldInteractEvent.class, Material.GOLDEN_HOE),
    PLAYER_PLACE_BLOCK(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, PlaceBlockEvent.class, Material.GRASS_BLOCK),
    PLAYER_DESTROY_BLOCK(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, DestroyBlockEvent.class, Material.STONE),
    PLAYER_DESTROYING_BLOCK(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, DamageBlockEvent.class, Material.COBBLESTONE),
    PLAYER_BLOCK_INTERACT(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, BlockInteractionEvent.class, Material.CHEST),
    PLAYER_MOB_INTERACT(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, MobInteractionEvent.class, Material.VILLAGER_SPAWN_EGG),
    PLAYER_BED_ENTER(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, BedEnterEvent.class, Material.RED_BED),
    PLAYER_BED_LEAVE(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, BedLeaveEvent.class, Material.ORANGE_BED),
    PLAYER_FISHING(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, FishEvent.class, Material.FISHING_ROD),
    PLAYER_SPECTATING(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, StartSpectatingEvent.class, Material.GLASS),
    PLAYER_STOP_SPECTATING(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, StopSpectatingEvent.class, Material.GLASS_PANE),
    PLAYER_CHANGED_SIGN(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, ChangedSignEvent.class, Material.OAK_SIGN),
    PLAYER_BUCKET_FILL(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, BucketFillEvent.class, Material.WATER_BUCKET),
    PLAYER_BUCKET_EMPTY(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, BucketEmptyEvent.class, Material.BUCKET),
    PLAYER_BUCKET_ENTITY(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, BucketEntityEvent.class, Material.PUFFERFISH_BUCKET),

    PLAYER_CLICK_INVENTORY(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemClickEvent.class, Material.TRIPWIRE_HOOK),
    PLAYER_DROP_ITEM(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemDropEvent.class, Material.HOPPER),
    PLAYER_PICKUP_ITEM(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemPickupEvent.class, Material.GLOWSTONE_DUST),
    PLAYER_SWAP_HAND(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemChangeEvent.class, Material.SHIELD),
    PLAYER_OPEN_INVENTORY(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, OpenInventoryEvent.class, Material.CHEST),
    PLAYER_WRITE_BOOK(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, BookWriteEvent.class, Material.WRITABLE_BOOK),
    PLAYER_CHANGE_SLOT(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, SlotChangeEvent.class, Material.SLIME_BALL),
    PLAYER_ITEM_CONSUME(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemConsumeEvent.class, Material.BREAD),
    PLAYER_ITEM_CRAFT(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, PlayerItemCraftEvent.class, Material.CRAFTING_TABLE),
    PLAYER_ITEM_DAMAGE(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, PlayerItemDamagedEvent.class, Material.DEAD_BUSH),
    PLAYER_ITEM_BREAK(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemBreakEvent.class, Material.GOLDEN_PICKAXE),
    PLAYER_DRAG_ITEM(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemMoveEvent.class, Material.PAPER),
    PLAYER_CLOSE_INVENTORY(ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, CloseInventoryEvent.class, Material.STRUCTURE_VOID),

    PLAYER_GET_DAMAGED(ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerDamagedEvent.class, Material.DEAD_BUSH),
    MOB_DAMAGE_PLAYER(ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, MobDamagesPlayerEvent.class, Material.ZOMBIE_HEAD),
    PLAYER_DAMAGE_MOB(ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerDamagesMobEvent.class, Material.SKELETON_SKULL),
    PLAYER_DAMAGE_PLAYER(ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerDamagesPlayerEvent.class, Material.PLAYER_HEAD),
    PLAYER_HUNGER_CHANGE(ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, HungerChangeEvent.class, Material.COOKED_CHICKEN),
    PLAYER_KILLED_PLAYER(ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerKilledPlayerEvent.class, Material.DIAMOND_SWORD),
    PLAYER_KILLED_MOB(ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerKilledMobEvent.class, Material.IRON_AXE),
    PLAYER_DEATH(ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerDeathEvent.class, Material.REDSTONE),
    PLAYER_RESPAWN(ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerRespawnEvent.class, Material.NETHER_STAR),
    PLAYER_TOTEM_RESPAWN(ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerTotemRespawnEvent.class, Material.TOTEM_OF_UNDYING),

    PLAYER_WALK(ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, PlayerMoveEvent.class, Material.LEATHER_BOOTS),
    PLAYER_JUMP(ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, JumpEvent.class, Material.RABBIT_FOOT),
    PLAYER_RUNNING(ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StartRunningEvent.class, Material.GOLDEN_BOOTS),
    PLAYER_STOP_RUNNING(ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StopRunningEvent.class, Material.CHAINMAIL_BOOTS),
    PLAYER_FLYING(ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StartFlyingEvent.class, Material.FEATHER),
    PLAYER_STOP_FLYING(ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StopFlyingEvent.class, Material.FEATHER),
    PLAYER_SNEAKING(ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StartSneakingEvent.class, Material.CHAINMAIL_LEGGINGS),
    PLAYER_STOP_SNEAKING(ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StopSneakingEvent.class, Material.IRON_LEGGINGS),
    PLAYER_TELEPORT(ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, TeleportEvent.class, Material.ENDER_PEARL),
    PLAYER_ENTERED_VEHICLE(ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, EnteredVehicleEvent.class, Material.MINECART),
    PLAYER_VEHICLE_EXIT(ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, PlayerVehicleExitEvent.class, Material.TNT_MINECART),

    WORLD_PLAY_MODE(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, GamePlayEvent.class, Material.EMERALD),
    WORLD_VARIABLE_TRANSFER(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, VariableTransferEvent.class, Material.CALIBRATED_SCULK_SENSOR),
    WORLD_WEB_RESPONSE(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, WebResponseEvent.class, Material.BEACON),
    WORLD_LIGHTNING_STRIKE(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, LightningStrikeEvent.class, Material.TRIDENT),
    WORLD_REACHED_REDSTONE_LIMIT(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, LimitReachedRedstoneEvent.class, Material.REDSTONE),
    WORLD_REACHED_BLOCKS_LIMIT(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, LimitReachedBlocksEvent.class, Material.GRASS_BLOCK),
    WORLD_REACHED_ENTITIES_LIMIT(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, LimitReachedEntitiesEvent.class, Material.CHICKEN_SPAWN_EGG),
    WORLD_REACHED_VARIABLES_LIMIT(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_OTHER, LimitReachedVariablesEvent.class, Material.MAGMA_CREAM),

    WORLD_BLOCK_BURNED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockBurnedEvent.class, Material.CAMPFIRE),
    WORLD_BLOCK_COOKED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_INVENTORY, BlockCookedEvent.class, Material.COOKED_CHICKEN),
    WORLD_BLOCK_FURNACE_BURNED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_INVENTORY, BlockFurnaceBurnedEvent.class, Material.COAL),
    WORLD_BLOCK_DISPENSED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_INVENTORY, BlockDispensedEvent.class, Material.DROPPER),
    WORLD_BLOCK_BREWING_FUEL(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_INVENTORY, BlockBrewingFuelEvent.class, Material.BLAZE_POWDER),
    WORLD_BLOCK_CRAFTER_CRAFTED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_INVENTORY, BlockCrafterCraftedEvent.class, Material.CRAFTER),

    WORLD_BLOCK_EXPLODED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockExplodedEvent.class, Material.TNT),
    WORLD_BLOCK_TNT_PRIME(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockTntPrimeEvent.class, Material.TNT_MINECART),
    WORLD_BLOCK_EXPERIENCE_DROP(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockExperienceDropEvent.class, Material.EXPERIENCE_BOTTLE),
    WORLD_BLOCK_FADED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockFadedEvent.class, Material.ICE),
    WORLD_BLOCK_FORMED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockFormedEvent.class, Material.SNOW_BLOCK),
    WORLD_BLOCK_GROWN(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockGrownEvent.class, Material.WHEAT),
    WORLD_BLOCK_IGNITED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockIgnitedEvent.class, Material.FLINT_AND_STEEL),
    WORLD_BLOCK_PHYSICS(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockPhysicsEvent.class, Material.SAND),
    WORLD_BLOCK_REDSTONE(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockRedstoneEvent.class, Material.REDSTONE),
    WORLD_PORTAL_CREATED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, PortalCreatedEvent.class, Material.CRYING_OBSIDIAN),
    WORLD_BLOCK_PISTON_EXTENDED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockPistonExtendedEvent.class, Material.STICKY_PISTON),
    WORLD_BLOCK_PISTON_RETRACTED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockPistonRetractedEvent.class, Material.PISTON),
    WORLD_BLOCK_BREWING_START(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockBrewingStartEvent.class, Material.BREWING_STAND),
    WORLD_BLOCK_BREWING_END(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockBrewingEndEvent.class, Material.POTION),
    WORLD_BLOCK_CAMPFIRE_START(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockCampfireStartEvent.class, Material.CAMPFIRE),
    WORLD_BLOCK_CAULDRON_CHANGE(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockCauldronChangeEvent.class, Material.CAULDRON),
    WORLD_BLOCK_FLUID_CHANGED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockFluidChangeEvent.class, Material.WATER_BUCKET),
    WORLD_BLOCK_LEAVES_DECAYED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockLeavesDecayedEvent.class, Material.OAK_LEAVES),
    WORLD_BLOCK_NOTE_PLAYED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockNotePlayedEvent.class, Material.NOTE_BLOCK),
    WORLD_BLOCK_SCULK_BLOOMED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockSculkBloomedEvent.class, Material.SCULK_SENSOR),
    WORLD_BLOCK_BEACON_ACTIVATED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockBeaconActivatedEvent.class, Material.BEACON),
    WORLD_BLOCK_BEACON_DEACTIVATED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockBeaconActivatedEvent.class, Material.BEACON),
    WORLD_BLOCK_ANVIL_DAMAGED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockAnvilDamagedEvent.class, Material.DAMAGED_ANVIL),
    WORLD_BLOCK_TARGET_HIT(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockTargetHitEvent.class, Material.TARGET),
    WORLD_BLOCK_SPONGE_ABSORBED(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockSpongeAbsorbedEvent.class, Material.WET_SPONGE),
    WORLD_BLOCK_BELL_RUNG(ExecutorCategory.EVENT_WORLD, MenusCategory.WORLD_BLOCKS, BlockBellRungEvent.class, Material.BELL),
    //WORLD_WEATHER_CHANGED(      ExecutorCategory.EVENT_WORLD, MenusCategory.OTHER, Material.WATER_BUCKET),
    //WORLD_CODE_ERROR_OCCURRED(  ExecutorCategory.EVENT_WORLD, MenusCategory.OTHER, Material.BARRIER),

    ENTITY_PIG_ZOMBIE_ANGERED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, PigZombieAngeredEvent.class, Material.ZOMBIFIED_PIGLIN_SPAWN_EGG),
    ENTITY_BAT_TOGGLED_SLEEP_MODE(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, EntityBatToggledSleepModeEvent.class, Material.BAT_SPAWN_EGG),
    ENTITY_SLIME_SPLIT(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, SlimeSplittedEvent.class, Material.SLIME_SPAWN_EGG),
    ENTITY_SHULKER_DUPLICATED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, ShulkerDuplicationEvent.class, Material.SHULKER_BOX),
    ENTITY_WITCH_READY_POTION(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, WitchReadyPotionEvent.class, Material.SPLASH_POTION),
    ENTITY_SHEEP_REGROWN_WOOL(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, SheepRegrownWoolEvent.class, Material.PINK_WOOL),
    ENTITY_PUFFERFISH_STATE_CHANGED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, PufferfishStateChangedEvent.class, Material.PUFFERFISH),
    ENTITY_CREEPER_IGNITED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, CreeperIgnitedEvent.class, Material.CREEPER_HEAD),
    ENTITY_CREEPER_POWERED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, CreeperPoweredEvent.class, Material.CREEPER_SPAWN_EGG),
    ENTITY_ENTERED_LOVE_MODE(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, EntityEnteredLoveModeEvent.class, Material.WHEAT),
    ENTITY_TURTLE_GOES_HOME(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, TurtleGoesHomeEvent.class, Material.TURTLE_SPAWN_EGG),
    ENTITY_RESURRECTED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, EntityResurrectedEvent.class, Material.TOTEM_OF_UNDYING),
    ENTITY_POTION_EFFECTED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, EntityPotionEffectedEvent.class, Material.POTION),
    ENTITY_WARDEN_ANGER_CHANGED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, WardenAngerChangedEvent.class, Material.WARDEN_SPAWN_EGG),
    ENTITY_AIR_CHANGED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_STATE, EntityAirChangedEvent.class, Material.WATER_BUCKET),

    ENTITY_PROJECTILE_HIT(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, ProjectileHitBlockEvent.class, Material.TARGET),
    ENTITY_ENTERED_BLOCK(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, EntityEnteredBlockEvent.class, Material.SILVERFISH_SPAWN_EGG),
    ENTITY_MOUNTED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, EntityMountedEvent.class, Material.SADDLE),
    ENTITY_DISMOUNTED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, EntityDismountedEvent.class, Material.SADDLE),
    ENTITY_ENTERED_VEHICLE(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, EntityEnteredVehicleEvent.class, Material.MINECART),
    ENTITY_VEHICLE_EXIT(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, EntityVehicleExitEvent.class, Material.TNT_MINECART),
    ENTITY_JUMPED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, EntityJumpedEvent.class, Material.RABBIT_FOOT),
    ENTITY_HORSE_JUMPED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, HorseJumpedEvent.class, Material.HORSE_SPAWN_EGG),
    ENTITY_ENDERMAN_ESCAPED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_MOVEMENT, EndermanEscapedEvent.class, Material.ENDERMAN_SPAWN_EGG),

    ENTITY_SPAWNED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntitySpawnEvent.class, Material.CHICKEN_SPAWN_EGG),
    ENTITY_REMOVED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntityRemovedEvent.class, Material.BARRIER),
    ENTITY_BORN(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntityBornEvent.class, Material.RABBIT_FOOT),
    ENTITY_DROPPED_ITEM(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntityDroppedItemEvent.class, Material.GUNPOWDER),
    ENTITY_PICKED_UP_ITEM(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntityPickedUpItemEvent.class, Material.GLOWSTONE_DUST),
    ENTITY_ITEM_MERGED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, ItemMergedEvent.class, Material.BEETROOT_SEEDS),
    ENTITY_ITEM_DESPAWNED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, ItemDespawnedEvent.class, Material.STRUCTURE_VOID),
    ENTITY_EXPLODED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntityExplodedEvent.class, Material.TNT),
    ENTITY_DAMAGED_ITEM(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntityDamagedItemEvent.class, Material.GOLDEN_PICKAXE),
    ENTITY_PIGLIN_BARTERED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, PiglinBarteredEvent.class, Material.PIGLIN_HEAD),
    ENTITY_INTERACTED_BLOCK(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, EntityInteractedBlockEvent.class, Material.CRAFTING_TABLE),
    ENTITY_TURTLE_LAYS_EGG(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, TurtleLaysEggEvent.class, Material.TURTLE_EGG),
    ENTITY_FIREWORK_EXPLODED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_INTERACTION, FireworkExplodedEvent.class, Material.FIREWORK_ROCKET),

    ENTITY_GET_DAMAGED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, EntityGetDamagedEvent.class, Material.DEAD_BUSH),
    ENTITY_DIED(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, EntityDiedEvent.class, Material.REDSTONE),
    ENTITY_SHOT_BOW(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, EntityShotBowEvent.class, Material.BOW),
    ENTITY_WITCH_THROWN_POTION(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, WitchThrownPotionEvent.class, Material.SPLASH_POTION),
    ENTITY_WITCH_CONSUMED_POTION(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, WitchConsumedPotionEvent.class, Material.POTION),
    ENTITY_LOADED_CROSSBOW(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, EntityLoadedCrossbowEvent.class, Material.CROSSBOW),
    ENTITY_COMBUSTED_BY_ENTITY(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, EntityCombustedByEntityEvent.class, Material.BLAZE_POWDER),
    ENTITY_COMBUSTED_BY_BLOCK(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, EntityCombustedByBlockEvent.class, Material.LAVA_BUCKET),
    ENTITY_REGAINED_HEALTH(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, EntityRegainedHealthEvent.class, Material.APPLE),
    ENTITY_HANGING_BREAK(ExecutorCategory.EVENT_ENTITY, MenusCategory.ENTITY_FIGHTING, HangingBreakEvent.class, Material.ITEM_FRAME),
    ;

    private final Class<? extends Executor> executor;
    private final Class<? extends WorldEvent> creativeEvent;
    private final ExecutorCategory category;
    private final MenusCategory menusCategory;
    private final Material material;

    ExecutorType(ExecutorCategory category, Class<? extends Executor> executor) {
        this.executor = executor;
        this.menusCategory = MenusCategory.OTHER;
        this.creativeEvent = null;
        this.category = category;
        this.material = Material.LIGHT_GRAY_STAINED_GLASS;
    }

    ExecutorType(ExecutorCategory category, MenusCategory menusCategory, Class<? extends WorldEvent> event, Material material) {
        this.executor = TypedExecutor.class;
        this.menusCategory = menusCategory;
        this.creativeEvent = event;
        this.category = category;
        this.material = material;
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
        Set<MenusCategory> set = new LinkedHashSet<>();
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

    public final Class<? extends WorldEvent> getEventClass() {
        return this.creativeEvent;
    }

    public final Class<? extends Executor> getExecutorClass() {
        return this.executor;
    }

    public boolean isCancellable() {
        Class<?> eventClass = getEventClass();
        return (eventClass != null && Cancellable.class.isAssignableFrom(eventClass));
    }

    @Override
    public @NotNull ExecutorCategory getCategory() {
        return category;
    }

    @Override
    public boolean isDisabled() {
        return getExecutorClass() == null || OpenCreative.getSettings().getCodingSettings().isDisabledEvent(this);
    }

    @Override
    public @NotNull ItemStack getIcon() {
        ItemStack icon = createItem(this.material, 1, "items.developer.events." + this.name().toLowerCase().replace("_", "-"));
        addLoreAtEnd(icon, (isCancellable() ? getLocaleMessage("items.developer.events.cancellable", false) : ""));
        if (isDisabled()) {
            icon.setType(Material.LIGHT_GRAY_STAINED_GLASS);
            addLoreAtEnd(icon, getLocaleMessage("disabled"));
        }
        setPersistentData(icon, getCodingValueKey(), name());
        return icon;
    }

    @Override
    public final @NotNull String getLocaleName() {
        return getLocaleMessage("items.developer.events." + this.name().toLowerCase().replace("_", "-") + ".name", false);
    }

}

