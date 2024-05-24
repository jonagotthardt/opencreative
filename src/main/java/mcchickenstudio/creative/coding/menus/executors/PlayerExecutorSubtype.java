/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package mcchickenstudio.creative.coding.menus.executors;

import mcchickenstudio.creative.coding.menus.PlayerEventsMenu;
import mcchickenstudio.creative.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;

public enum PlayerExecutorSubtype {

    PLAYER_JOIN("player-join",Material.POTATO, PlayerEventsMenu.PlayerEventCategory.WORLD,  false),
    PLAYER_QUIT("player-quit",Material.POISONOUS_POTATO, PlayerEventsMenu.PlayerEventCategory.WORLD,false),
    PLAYER_LIKED("player-liked",Material.DIAMOND, PlayerEventsMenu.PlayerEventCategory.WORLD, false),
    PLAYER_ADVERTISED("player-advertised",Material.BEACON, PlayerEventsMenu.PlayerEventCategory.WORLD, false),
    PLAYER_PLAY("player-play",Material.COAL, PlayerEventsMenu.PlayerEventCategory.WORLD, true),
    PLAYER_SEND_MESSAGE("player-send-message",Material.BOOK, PlayerEventsMenu.PlayerEventCategory.WORLD, false),

    PLAYER_LEFT_CLICK("player-left-click",Material.GOLDEN_PICKAXE, PlayerEventsMenu.PlayerEventCategory.WORLD_INTERACTION, true),
    PLAYER_RIGHT_CLICK("player-right-click",Material.DIAMOND_PICKAXE, PlayerEventsMenu.PlayerEventCategory.WORLD_INTERACTION, true),
    PLAYER_INTERACT("player-interact",Material.GOLDEN_HOE, PlayerEventsMenu.PlayerEventCategory.WORLD_INTERACTION, true),
    PLAYER_PLACE_BLOCK("player-place-block",Material.GRASS_BLOCK, PlayerEventsMenu.PlayerEventCategory.WORLD_INTERACTION, true),
    PLAYER_DESTROY_BLOCK("player-destroy-block",Material.STONE, PlayerEventsMenu.PlayerEventCategory.WORLD_INTERACTION, true),
    PLAYER_DESTROYING_BLOCK("player-destroying-block",Material.COBBLESTONE, PlayerEventsMenu.PlayerEventCategory.WORLD_INTERACTION, true),
    PLAYER_BLOCK_INTERACT("player-block-interact",Material.CHEST, PlayerEventsMenu.PlayerEventCategory.WORLD_INTERACTION,  true),
    PLAYER_MOB_INTERACT("player-mob-interact",Material.VILLAGER_SPAWN_EGG, PlayerEventsMenu.PlayerEventCategory.WORLD_INTERACTION, true),
    PLAYER_FISHING("player-fishing",Material.FISHING_ROD, PlayerEventsMenu.PlayerEventCategory.WORLD_INTERACTION, true),
    PLAYER_SPECTATING("player-spectating",Material.GLASS, PlayerEventsMenu.PlayerEventCategory.WORLD_INTERACTION, true),
    PLAYER_STOP_SPECTATING("player-stop-spectating",Material.GLASS_PANE, PlayerEventsMenu.PlayerEventCategory.WORLD_INTERACTION, true),

    PLAYER_OPEN_INVENTORY("player-open-inventory",Material.CHEST, PlayerEventsMenu.PlayerEventCategory.INVENTORY, true),
    PLAYER_CLICK_INVENTORY("player-click-inventory",Material.TRIPWIRE_HOOK, PlayerEventsMenu.PlayerEventCategory.INVENTORY, true),
    PLAYER_DRAG_ITEM("player-drag-item",Material.PAPER, PlayerEventsMenu.PlayerEventCategory.INVENTORY, true),
    PLAYER_SWAP_HAND("player-swap-hand",Material.SHIELD, PlayerEventsMenu.PlayerEventCategory.INVENTORY, true),
    PLAYER_WRITE_BOOK("player-write-book",Material.WRITABLE_BOOK, PlayerEventsMenu.PlayerEventCategory.INVENTORY, true),
    PLAYER_CHANGE_SLOT("player-change-slot",Material.SLIME_BALL, PlayerEventsMenu.PlayerEventCategory.INVENTORY, true),
    PLAYER_DROP_ITEM("player-drop-item",Material.HOPPER, PlayerEventsMenu.PlayerEventCategory.INVENTORY, true),
    PLAYER_PICKUP_ITEM("player-pickup-item",Material.GLOWSTONE_DUST, PlayerEventsMenu.PlayerEventCategory.INVENTORY, true),
    PLAYER_CLOSE_INVENTORY("player-close-inventory",Material.STRUCTURE_VOID, PlayerEventsMenu.PlayerEventCategory.INVENTORY, true),

    PLAYER_GET_DAMAGED("player-get-damaged",Material.DEAD_BUSH, PlayerEventsMenu.PlayerEventCategory.COMBAT, true),
    MOB_DAMAGE_PLAYER("mob-damage-player",Material.ZOMBIE_HEAD, PlayerEventsMenu.PlayerEventCategory.COMBAT, true),
    PLAYER_DAMAGE_MOB("player-damage-mob",Material.SKELETON_SKULL, PlayerEventsMenu.PlayerEventCategory.COMBAT, true),
    PLAYER_HUNGER_CHANGE("player-hunger-change",Material.COOKED_CHICKEN, PlayerEventsMenu.PlayerEventCategory.COMBAT, true),
    PLAYER_DEATH("player-death",Material.REDSTONE, PlayerEventsMenu.PlayerEventCategory.COMBAT, true),
    PLAYER_RESPAWN("player-respawn",Material.PLAYER_HEAD, PlayerEventsMenu.PlayerEventCategory.COMBAT, false),
    PLAYER_TOTEM_RESPAWN("player-totem-respawn",Material.TOTEM_OF_UNDYING, PlayerEventsMenu.PlayerEventCategory.COMBAT, false),

    PLAYER_WALK("player-walk",Material.LEATHER_BOOTS, PlayerEventsMenu.PlayerEventCategory.MOVEMENT, true),
    PLAYER_JUMP("player-jump",Material.RABBIT_FOOT, PlayerEventsMenu.PlayerEventCategory.MOVEMENT, true),
    PLAYER_RUNNING("player-running",Material.GOLDEN_BOOTS, PlayerEventsMenu.PlayerEventCategory.MOVEMENT, true),
    PLAYER_STOP_RUNNING("player-stop-running",Material.CHAINMAIL_BOOTS, PlayerEventsMenu.PlayerEventCategory.MOVEMENT, true),
    PLAYER_FLYING("player-flying",Material.FEATHER, PlayerEventsMenu.PlayerEventCategory.MOVEMENT, true),
    PLAYER_STOP_FLYING("player-stop-flying",Material.FEATHER, PlayerEventsMenu.PlayerEventCategory.MOVEMENT, true),
    PLAYER_SNEAKING("player-sneaking",Material.CHAINMAIL_LEGGINGS, PlayerEventsMenu.PlayerEventCategory.MOVEMENT, true),
    PLAYER_STOP_SNEAKING("player-stop-sneaking",Material.IRON_LEGGINGS, PlayerEventsMenu.PlayerEventCategory.MOVEMENT, true),
    PLAYER_TELEPORT("player-teleport",Material.ENDER_PEARL, PlayerEventsMenu.PlayerEventCategory.MOVEMENT, true);

    private final boolean isCancellable;
    private final PlayerEventsMenu.PlayerEventCategory category;
    private final Material material;
    private final String messagePath;

    PlayerExecutorSubtype(String messagePath, Material material, PlayerEventsMenu.PlayerEventCategory category, boolean isCancellable) {
        this.messagePath = messagePath;
        this.material = material;
        this.category = category;
        this.isCancellable = isCancellable;
    }

    public ItemStack getIcon() {
        return createItem(this.material,1,"items.developer.events." + this.messagePath);
    }

    public PlayerEventsMenu.PlayerEventCategory getCategory() {
        return category;
    }

    public String getLocaleName() {
        return MessageUtils.getLocaleMessage("items.developer.events." + this.messagePath + ".name",false);
    }

}
