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

package mcchickenstudio.creative.coding.menus.conditions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import mcchickenstudio.creative.coding.menus.PlayerConditionsMenu;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.MessageUtils.*;

public enum PlayerConditionSubtype {

    NONE("none",null,null,0,true),

    NICKNAME_EQUALS("nickname-equals", Material.NAME_TAG, PlayerConditionsMenu.PlayerConditionCategory.PARAMETERS, 2),

    INVENTORY_FULL("inventory-full", Material.PUFFERFISH_BUCKET, PlayerConditionsMenu.PlayerConditionCategory.INVENTORY, 0),

    NEAR_LOCATION("near-location", Material.COMPASS, PlayerConditionsMenu.PlayerConditionCategory.BLOCK, 4),

    IS_SNEAKING("is-sneaking",Material.LEATHER_BOOTS, PlayerConditionsMenu.PlayerConditionCategory.PARAMETERS,0);


    private final PlayerConditionsMenu.PlayerConditionCategory conditionCategory;
    private final Material material;
    private final String messagePath;
    private final int maxParameters;

    PlayerConditionSubtype(String messagePath, Material material, PlayerConditionsMenu.PlayerConditionCategory conditionCategory, int maxParameters) {
        this.messagePath = messagePath;
        this.material = material;
        this.conditionCategory = conditionCategory;
        this.maxParameters = maxParameters;
    }

    PlayerConditionSubtype(String messagePath, Material material, PlayerConditionsMenu.PlayerConditionCategory conditionCategory, int maxParameters, boolean isRequiredPlayerToBeInWorld) {
        this.messagePath = messagePath;
        this.material = material;
        this.conditionCategory = conditionCategory;
        this.maxParameters = maxParameters;
    }

    public boolean isChestRequired() {
        return (maxParameters != 0);
    }

    public ItemStack getIcon() {
        return createItem(this.material,1,"items.developer.conditions." + this.messagePath);
    }

    public PlayerConditionsMenu.PlayerConditionCategory getConditionSubtype() {
        return conditionCategory;
    }

    public String getLocaleName() {
        return getLocaleMessage("items.developer.conditions." + this.messagePath + ".name",false);
    }

}
