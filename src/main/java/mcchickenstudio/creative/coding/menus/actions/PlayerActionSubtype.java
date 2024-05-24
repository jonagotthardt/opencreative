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

package mcchickenstudio.creative.coding.menus.actions;

import mcchickenstudio.creative.coding.menus.PlayerActionsMenu;
import mcchickenstudio.creative.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;

/**
 * List of all player actions in menu. Will be removed soon.
 */
@Deprecated
public enum PlayerActionSubtype {

    NONE("none",null,null,0,true),

    SEND_MESSAGE("send-message", Material.BOOK, PlayerActionsMenu.PlayerActionCategory.COMMUNICATION, 2),
    PLAY_SOUND("play-sound", Material.MUSIC_DISC_CAT, PlayerActionsMenu.PlayerActionCategory.COMMUNICATION, 1),
    SHOW_TITLE("show-title", Material.OAK_SIGN, PlayerActionsMenu.PlayerActionCategory.COMMUNICATION, 1),

    SET_GAMEMODE("set-gamemode", Material.CRAFTING_TABLE, PlayerActionsMenu.PlayerActionCategory.PARAMETERS, 1,true),
    SET_HEALTH("set-health",Material.POTION, PlayerActionsMenu.PlayerActionCategory.PARAMETERS,2,true),
    SET_HUNGER("set-hunger",Material.COOKED_CHICKEN, PlayerActionsMenu.PlayerActionCategory.PARAMETERS,2,true),

    GIVE_ITEMS("give-items",Material.CHEST_MINECART, PlayerActionsMenu.PlayerActionCategory.INVENTORY,2,true),
    CLEAR_INVENTORY("clear-inventory",Material.FEATHER, PlayerActionsMenu.PlayerActionCategory.INVENTORY,0,true),

    TELEPORT_PLAYER("teleport-player",Material.ENDER_PEARL, PlayerActionsMenu.PlayerActionCategory.MOVEMENT,1,true),
    SADDLE_ENTITY("saddle-entity",Material.SADDLE, PlayerActionsMenu.PlayerActionCategory.MOVEMENT,1,true);

    private final PlayerActionsMenu.PlayerActionCategory actionCategory;
    private final Material material;
    private final String messagePath;
    private final int maxParameters;
    private final boolean isPlayerInWorldRequired;

    PlayerActionSubtype(String messagePath, Material material, PlayerActionsMenu.PlayerActionCategory actionCategory, int maxParameters) {
        this.messagePath = messagePath;
        this.material = material;
        this.actionCategory = actionCategory;
        this.maxParameters = maxParameters;
        this.isPlayerInWorldRequired = false;
    }

    PlayerActionSubtype(String messagePath, Material material, PlayerActionsMenu.PlayerActionCategory actionCategory, int maxParameters, boolean isRequiredPlayerToBeInWorld) {
        this.messagePath = messagePath;
        this.material = material;
        this.actionCategory = actionCategory;
        this.maxParameters = maxParameters;
        this.isPlayerInWorldRequired = isRequiredPlayerToBeInWorld;
    }

    public boolean isChestRequired() {
        return (maxParameters != 0);
    }

    public boolean isPlayerInWorldRequired() {
        return this.isPlayerInWorldRequired;
    }

    public ItemStack getIcon() {
        return createItem(this.material,1,"items.developer.actions." + this.messagePath);
    }

    public PlayerActionsMenu.PlayerActionCategory getActionCategory() {
        return actionCategory;
    }

    public String getLocaleName() {
        return MessageUtils.getLocaleMessage("items.developer.actions." + this.messagePath + ".name",false);
    }

}
