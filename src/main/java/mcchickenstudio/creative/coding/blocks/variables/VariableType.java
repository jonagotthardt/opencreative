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

package mcchickenstudio.creative.coding.blocks.variables;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.MessageUtils.messageExists;

public enum VariableType {

    TEXT(Material.BOOK, Material.BROWN_STAINED_GLASS_PANE),
    NUMBER(Material.SLIME_BALL, Material.LIME_STAINED_GLASS_PANE),
    LOCATION(Material.PAPER, Material.WHITE_STAINED_GLASS_PANE),
    BOOLEAN(Material.CLOCK, Material.YELLOW_STAINED_GLASS_PANE),
    ITEM(Material.ITEM_FRAME, Material.ORANGE_STAINED_GLASS_PANE),
    LIST(Material.PAINTING,Material.GREEN_STAINED_GLASS_PANE),
    PARAMETER(Material.ANVIL, Material.BLACK_STAINED_GLASS_PANE),
    PARTICLE(Material.FIRE_CHARGE, Material.PURPLE_STAINED_GLASS_PANE),
    POTION(Material.POTION, Material.PINK_STAINED_GLASS);

    private final Material itemMaterial;
    private final Material menuGlass;

    VariableType(Material item, Material menuGlass) {
        this.itemMaterial = item;
        this.menuGlass = menuGlass;
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public Material getMenuGlass() {
        return menuGlass;
    }

    public ItemStack getGlassItem() {
        return createItem(getMenuGlass(),1,"items.developer.placeholders."+this.name().toLowerCase());
    }

    public ItemStack getGlassItem(ActionType action, Byte number) {
        String messagePath = "items.developer.actions."+action.name().toLowerCase().replace("_","-")+".placeholders." + number;
        if (messageExists(messagePath+".name") && messageExists(messagePath+".lore")) {
            return createItem(getMenuGlass(),1,messagePath);
        }
        return createItem(getMenuGlass(),1,"items.developer.placeholders."+this.name().toLowerCase());
    }

    public static VariableType parseString(String type) {
        for (VariableType varType : values()) {
            if (varType.name().equalsIgnoreCase(type)) return varType;
        }
        return TEXT;
    }

    public static VariableType getByMaterial(Material material) {
        for (VariableType varType : values()) {
            if (varType.itemMaterial == material) return varType;
        }
        return TEXT;
    }

}
