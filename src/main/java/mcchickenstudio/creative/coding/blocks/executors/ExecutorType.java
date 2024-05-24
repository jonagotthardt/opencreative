package mcchickenstudio.creative.coding.blocks.executors;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.*;
import mcchickenstudio.creative.coding.blocks.events.player.interaction.*;
import mcchickenstudio.creative.coding.blocks.events.player.inventory.*;
import mcchickenstudio.creative.coding.blocks.events.player.movement.*;
import mcchickenstudio.creative.coding.blocks.events.player.world.*;
import mcchickenstudio.creative.coding.blocks.executors.player.fighting.*;
import mcchickenstudio.creative.coding.blocks.executors.player.interaction.*;
import mcchickenstudio.creative.coding.blocks.executors.player.inventory.*;
import mcchickenstudio.creative.coding.blocks.executors.player.movement.*;
import mcchickenstudio.creative.coding.blocks.executors.player.world.*;
import mcchickenstudio.creative.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;

/**
 * <h1>ExecutorType</h1>
 * This enum defines different types of all executors in coding.
 * Every type contains event class, executor class, item material, cancellable.
 * Foe example: PLAYER_JOIN, WORLD_START, FUNCTION_EXEC.
 * @since 1.5
 * @version 1.5
 * @author McChicken Studio
 */
public enum ExecutorType {

    // Player Executors

    PLAYER_JOIN(JoinExecutor.class, JoinEvent.class, Material.POTATO, false),
    PLAYER_QUIT(QuitExecutor.class, QuitEvent.class, Material.POISONOUS_POTATO, false),
    PLAYER_LIKED(LikeExecutor.class, LikeEvent.class, Material.DIAMOND,false),
    PLAYER_ADVERTISED(AdvertisedExecutor.class, AdvertisedEvent.class, Material.BEACON,false),
    PLAYER_PLAY(PlayExecutor.class, PlayEvent.class, Material.COAL,true),
    PLAYER_SEND_MESSAGE(ChatExecutor.class, ChatEvent.class, Material.BOOK,false),

    PLAYER_LEFT_CLICK(LeftClickExecutor.class, LeftClickEvent.class, Material.GOLDEN_PICKAXE,true),
    PLAYER_RIGHT_CLICK(RightClickExecutor.class, RightClickEvent.class, Material.DIAMOND_PICKAXE,true),
    PLAYER_INTERACT(WorldInteractExecutor.class, WorldInteractEvent.class, Material.GOLDEN_HOE,true),
    PLAYER_PLACE_BLOCK(PlaceBlockExecutor.class, PlaceBlockEvent.class, Material.GRASS_BLOCK,true),
    PLAYER_DESTROY_BLOCK(DestroyBlockExecutor.class, DestroyBlockEvent.class, Material.STONE,true),
    PLAYER_DESTROYING_BLOCK(DamageBlockExecutor.class, DamageBlockEvent.class, Material.COBBLESTONE,true),
    PLAYER_BLOCK_INTERACT(BlockInteractionExecutor.class, BlockInteractionEvent.class, Material.CHEST,true),
    PLAYER_MOB_INTERACT(MobInteractionExecutor.class, MobInteractionEvent.class, Material.VILLAGER_SPAWN_EGG,true),
    PLAYER_FISHING(FishExecutor.class, FishEvent.class, Material.FISHING_ROD,true),
    PLAYER_SPECTATING(StartSpectatingExecutor.class, StartSpectatingEvent.class, Material.GLASS,true),
    PLAYER_STOP_SPECTATING(StopSpectatingExecutor.class, StopSpectatingEvent.class, Material.GLASS_PANE,true),

    PLAYER_OPEN_INVENTORY(OpenInventoryExecutor.class, OpenInventoryEvent.class, Material.CHEST,true),
    PLAYER_CLICK_INVENTORY(ItemClickExecutor.class, ItemClickEvent.class, Material.TRIPWIRE_HOOK,true),
    PLAYER_DRAG_ITEM(ItemMoveExecutor.class, ItemClickEvent.class, Material.PAPER,true),
    PLAYER_SWAP_HAND(ItemChangeExecutor.class, ItemChangeEvent.class, Material.SHIELD,true),
    PLAYER_WRITE_BOOK(BookWriteExecutor.class, BookWriteEvent.class, Material.WRITABLE_BOOK,true),
    PLAYER_CHANGE_SLOT(SlotChangeExecutor.class, SlotChangeEvent.class, Material.SLIME_BALL,true),
    PLAYER_DROP_ITEM(ItemDropExecutor.class, ItemDropEvent.class, Material.HOPPER,true),
    PLAYER_PICKUP_ITEM(ItemPickupExecutor.class, ItemPickupEvent.class, Material.GLOWSTONE_DUST,true),
    PLAYER_CLOSE_INVENTORY(CloseInventoryExecutor.class, CloseInventoryEvent.class, Material.STRUCTURE_VOID,true),

    PLAYER_GET_DAMAGED(PlayerDamagedExecutor.class, PlayerDamagedEvent.class, Material.DEAD_BUSH,true),
    MOB_DAMAGE_PLAYER(MobDamagesPlayerExecutor.class, MobDamagesPlayerEvent.class, Material.ZOMBIE_HEAD,true),
    PLAYER_DAMAGE_MOB(PlayerDamagesMobExecutor.class, PlayerDamagesMobEvent.class, Material.SKELETON_SKULL,true),
    PLAYER_HUNGER_CHANGE(HungerChangeExecutor.class, HungerChangeEvent.class, Material.COOKED_CHICKEN,true),
    PLAYER_DEATH(PlayerDeathExecutor.class, PlayerDeathEvent.class,Material.REDSTONE,true),
    PLAYER_RESPAWN(PlayerRespawnExecutor.class, PlayerRespawnEvent.class, Material.PLAYER_HEAD,false),
    PLAYER_TOTEM_RESPAWN(PlayerTotemRespawnExecutor.class, PlayerTotemRespawnEvent.class, Material.TOTEM_OF_UNDYING,false),

    PLAYER_WALK(PlayerMoveExecutor.class, PlayerMoveEvent.class, Material.LEATHER_BOOTS,true),
    PLAYER_JUMP(JumpExecutor.class, JumpEvent.class, Material.RABBIT_FOOT,true),
    PLAYER_RUNNING(StartRunningExecutor.class, StartRunningEvent.class, Material.GOLDEN_BOOTS,true),
    PLAYER_STOP_RUNNING(StopRunningExecutor.class, StopRunningEvent.class, Material.CHAINMAIL_BOOTS,true),
    PLAYER_FLYING(StartFlyingExecutor.class, StartFlyingEvent.class, Material.FEATHER,true),
    PLAYER_STOP_FLYING(StopFlyingExecutor.class, StopFlyingEvent.class, Material.FEATHER,true),
    PLAYER_SNEAKING(StartSneakingExecutor.class, StartSneakingEvent.class, Material.CHAINMAIL_LEGGINGS,true),
    PLAYER_STOP_SNEAKING(StopSneakingExecutor.class, StopSneakingEvent.class, Material.IRON_LEGGINGS,true),
    PLAYER_TELEPORT(TeleportExecutor.class, TeleportEvent.class, Material.ENDER_PEARL,true);

    private final Class<? extends Executor> executor;
    private final Class<? extends CreativeEvent> creativeEvent;
    private final Material material;
    private final boolean isCancellable;

    ExecutorType(Class<? extends Executor> executor, Class<? extends CreativeEvent> event, Material material, boolean isCancellable) {
        this.executor = executor;
        this.creativeEvent = event;
        this.material = material;
        this.isCancellable = isCancellable;
    }

    public final ItemStack getIcon() {
        return createItem(this.material, 1, "items.developer.events." + this.name().toLowerCase().replace("_","-"));
    }

    public final String getLocaleName() {
        return MessageUtils.getLocaleMessage("items.developer.events." + this.name().toLowerCase().replace("_","-") + ".name", false);
    }

    public final Class<? extends CreativeEvent> getEventClass() {
        return this.creativeEvent;
    }

    public final Class<? extends Executor> getExecutorClass() {
        return this.executor;
    }

}

