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

package mcchickenstudio.creative.coding.blocks.executors;

import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.*;
import mcchickenstudio.creative.coding.blocks.events.player.interaction.*;
import mcchickenstudio.creative.coding.blocks.events.player.inventory.*;
import mcchickenstudio.creative.coding.blocks.events.player.movement.*;
import mcchickenstudio.creative.coding.blocks.events.player.world.*;
import mcchickenstudio.creative.coding.blocks.executors.other.Cycle;
import mcchickenstudio.creative.coding.blocks.executors.other.Function;
import mcchickenstudio.creative.coding.blocks.executors.other.Method;
import mcchickenstudio.creative.coding.blocks.executors.player.fighting.*;
import mcchickenstudio.creative.coding.blocks.executors.player.interaction.*;
import mcchickenstudio.creative.coding.blocks.executors.player.inventory.*;
import mcchickenstudio.creative.coding.blocks.executors.player.movement.*;
import mcchickenstudio.creative.coding.blocks.executors.player.world.*;
import mcchickenstudio.creative.coding.menus.MenusCategory;
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

import static mcchickenstudio.creative.utils.ItemUtils.addLoreAtEnd;
import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

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

    // Other

    FUNCTION(               ExecutorCategory.FUNCTION, Function.class),
    METHOD(                 ExecutorCategory.METHOD, Method.class),
    CYCLE(                 ExecutorCategory.CYCLE, Cycle.class),

    // Player Executors

    PLAYER_JOIN(            ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, JoinExecutor.class, JoinEvent.class, Material.POTATO),
    PLAYER_QUIT(            ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, QuitExecutor.class, QuitEvent.class, Material.POISONOUS_POTATO),
    PLAYER_LIKED(           ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, LikeExecutor.class, LikeEvent.class, Material.DIAMOND),
    PLAYER_ADVERTISED(      ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, AdvertisedExecutor.class, AdvertisedEvent.class, Material.BEACON),
    PLAYER_PLAY(            ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, PlayExecutor.class, PlayEvent.class, Material.COAL),
    PLAYER_CHAT(            ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, ChatExecutor.class, ChatEvent.class, Material.BOOK),
    PLAYER_PURCHASE(        ExecutorCategory.EVENT_PLAYER, MenusCategory.WORLD, PurchaseExecutor.class, PlayerPurchaseEvent.class, Material.GOLD_BLOCK),

    PLAYER_LEFT_CLICK(      ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, LeftClickExecutor.class, LeftClickEvent.class, Material.GOLDEN_PICKAXE),
    PLAYER_RIGHT_CLICK(     ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, RightClickExecutor.class, RightClickEvent.class, Material.DIAMOND_PICKAXE),
    PLAYER_INTERACT(        ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, WorldInteractExecutor.class, WorldInteractEvent.class, Material.GOLDEN_HOE),
    PLAYER_PLACE_BLOCK(     ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, PlaceBlockExecutor.class, PlaceBlockEvent.class, Material.GRASS_BLOCK),
    PLAYER_DESTROY_BLOCK(   ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, DestroyBlockExecutor.class, DestroyBlockEvent.class, Material.STONE),
    PLAYER_DESTROYING_BLOCK(ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, DamageBlockExecutor.class, DamageBlockEvent.class, Material.COBBLESTONE),
    PLAYER_BLOCK_INTERACT(  ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, BlockInteractionExecutor.class, BlockInteractionEvent.class, Material.CHEST),
    PLAYER_MOB_INTERACT(    ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, MobInteractionExecutor.class, MobInteractionEvent.class, Material.VILLAGER_SPAWN_EGG),
    PLAYER_FISHING(         ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, FishExecutor.class, FishEvent.class, Material.FISHING_ROD),
    PLAYER_SPECTATING(      ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, StartSpectatingExecutor.class, StartSpectatingEvent.class, Material.GLASS),
    PLAYER_STOP_SPECTATING( ExecutorCategory.EVENT_PLAYER, MenusCategory.INTERACTION, StopSpectatingExecutor.class, StopSpectatingEvent.class, Material.GLASS_PANE),

    PLAYER_OPEN_INVENTORY(  ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, OpenInventoryExecutor.class, OpenInventoryEvent.class, Material.CHEST),
    PLAYER_CLICK_INVENTORY( ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemClickExecutor.class, ItemClickEvent.class, Material.TRIPWIRE_HOOK),
    PLAYER_DRAG_ITEM(       ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemMoveExecutor.class, ItemMoveEvent.class, Material.PAPER),
    PLAYER_SWAP_HAND(       ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemChangeExecutor.class, ItemChangeEvent.class, Material.SHIELD),
    PLAYER_WRITE_BOOK(      ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, BookWriteExecutor.class, BookWriteEvent.class, Material.WRITABLE_BOOK),
    PLAYER_CHANGE_SLOT(     ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, SlotChangeExecutor.class, SlotChangeEvent.class, Material.SLIME_BALL),
    PLAYER_DROP_ITEM(       ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemDropExecutor.class, ItemDropEvent.class, Material.HOPPER),
    PLAYER_PICKUP_ITEM(     ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, ItemPickupExecutor.class, ItemPickupEvent.class, Material.GLOWSTONE_DUST),
    PLAYER_CLOSE_INVENTORY( ExecutorCategory.EVENT_PLAYER, MenusCategory.INVENTORY, CloseInventoryExecutor.class, CloseInventoryEvent.class, Material.STRUCTURE_VOID),

    PLAYER_GET_DAMAGED(     ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerDamagedExecutor.class, PlayerDamagedEvent.class, Material.DEAD_BUSH),
    MOB_DAMAGE_PLAYER(      ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, MobDamagesPlayerExecutor.class, MobDamagesPlayerEvent.class, Material.ZOMBIE_HEAD),
    PLAYER_DAMAGE_MOB(      ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerDamagesMobExecutor.class, PlayerDamagesMobEvent.class, Material.SKELETON_SKULL),
    PLAYER_HUNGER_CHANGE(   ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, HungerChangeExecutor.class, HungerChangeEvent.class, Material.COOKED_CHICKEN),
    PLAYER_DEATH(           ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerDeathExecutor.class, PlayerDeathEvent.class,Material.REDSTONE),
    PLAYER_RESPAWN(         ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerRespawnExecutor.class, PlayerRespawnEvent.class, Material.PLAYER_HEAD),
    PLAYER_TOTEM_RESPAWN(   ExecutorCategory.EVENT_PLAYER, MenusCategory.FIGHTING, PlayerTotemRespawnExecutor.class, PlayerTotemRespawnEvent.class, Material.TOTEM_OF_UNDYING),

    PLAYER_WALK(            ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, PlayerMoveExecutor.class, PlayerMoveEvent.class, Material.LEATHER_BOOTS),
    PLAYER_JUMP(            ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, JumpExecutor.class, JumpEvent.class, Material.RABBIT_FOOT),
    PLAYER_RUNNING(         ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StartRunningExecutor.class, StartRunningEvent.class, Material.GOLDEN_BOOTS),
    PLAYER_STOP_RUNNING(    ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StopRunningExecutor.class, StopRunningEvent.class, Material.CHAINMAIL_BOOTS),
    PLAYER_FLYING(          ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StartFlyingExecutor.class, StartFlyingEvent.class, Material.FEATHER),
    PLAYER_STOP_FLYING(     ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StopFlyingExecutor.class, StopFlyingEvent.class, Material.FEATHER),
    PLAYER_SNEAKING(        ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StartSneakingExecutor.class, StartSneakingEvent.class, Material.CHAINMAIL_LEGGINGS),
    PLAYER_STOP_SNEAKING(   ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, StopSneakingExecutor.class, StopSneakingEvent.class, Material.IRON_LEGGINGS),
    PLAYER_TELEPORT(        ExecutorCategory.EVENT_PLAYER, MenusCategory.MOVEMENT, TeleportExecutor.class, TeleportEvent.class, Material.ENDER_PEARL);

    private final Class<? extends Executor> executor;
    private final Class<? extends CreativeEvent> creativeEvent;
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

    ExecutorType(ExecutorCategory category, MenusCategory menusCategory, Class<? extends Executor> executor, Class<? extends CreativeEvent> event, Material material) {
        this.executor = executor;
        this.menusCategory = menusCategory;
        this.creativeEvent = event;
        this.category = category;
        this.material = material;
    }

    public final ItemStack getIcon() {
        return addLoreAtEnd(createItem(this.material, 1, "items.developer.events." + this.name().toLowerCase().replace("_","-")),(isCancellable() ? getLocaleMessage("items.developer.events.cancellable",false) : ""));
    }

    public final String getLocaleName() {
        return getLocaleMessage("items.developer.events." + this.name().toLowerCase().replace("_","-") + ".name", false);
    }

    public final Class<? extends CreativeEvent> getEventClass() {
        return this.creativeEvent;
    }

    public final Class<? extends Executor> getExecutorClass() {
        return this.executor;
    }

    public static ExecutorType getType(Block block) {
        if (block.getType() == Material.LAPIS_BLOCK) {
            return FUNCTION;
        } else if (block.getType() == Material.OXIDIZED_COPPER) {
            return CYCLE;
        }
        Block signBlock = block.getRelative(BlockFace.SOUTH);
        if (signBlock.getType().toString().contains("WALL_SIGN")) {
            Sign sign = (Sign) signBlock.getState();
            if (sign.lines().size() >= 3) {
                Component signText = sign.line(2);
                for (ExecutorType executorType : values()) {
                    if (executorType.name().equals(((TextComponent) signText).content().toUpperCase())) return executorType;
                }
            }
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

    public boolean isCancellable() {
        Class<?> executorClass = getExecutorClass();
        return (executorClass != null && Cancellable.class.isAssignableFrom(executorClass));
    }

}

