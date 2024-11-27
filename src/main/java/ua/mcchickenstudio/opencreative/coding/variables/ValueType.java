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

package ua.mcchickenstudio.opencreative.coding.variables;

import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>ValueType</h1>
 * This enum stores all possible value types, that are used
 * in coding for handling and storing values.
 */
public enum ValueType {

    /**
     * Boolean stores true or false.
     */
    BOOLEAN(Material.CLOCK, Material.YELLOW_STAINED_GLASS_PANE),
    /**
     * Entity Type stores Minecraft mob type, for example: AXOLOTL.
     */
    ENTITY_TYPE(Material.PIG_SPAWN_EGG, Material.RED_STAINED_GLASS_PANE),
    /**
     * Item stores ItemStack with material and item meta.
     */
    ITEM(Material.ITEM_FRAME, Material.ORANGE_STAINED_GLASS_PANE),
    /**
     * List can store any value with any type. From list, we can get value, or we can add value to list.
     */
    LIST(Material.PAINTING, Material.GREEN_STAINED_GLASS_PANE),
    /**
     * Map stores keys and values with any type.
     */
    MAP(Material.CHEST_MINECART, Material.BROWN_STAINED_GLASS_PANE),
    /**
     * Location stores Minecraft location in plot's world, for example: 31 4 10 -4.5 3.0. It contains X,Y,Z coordinates, yaw, pitch values.
     */
    LOCATION(Material.PAPER, Material.WHITE_STAINED_GLASS_PANE),
    /**
     * Number stores float value, for example: 12.0.
     */
    NUMBER(Material.SLIME_BALL, Material.LIME_STAINED_GLASS_PANE),
    /**
     * Parameters are used in Layout menus for coding blocks. They can store text, boolean and number values.
     */
    PARAMETER(Material.ANVIL, Material.CYAN_STAINED_GLASS_PANE),
    /**
     * Particle stores Minecraft particle type, for example: EXPLOSION.
     */
    PARTICLE(Material.FIRE_CHARGE, Material.PURPLE_STAINED_GLASS_PANE),
    /**
     * Potion stores Minecraft potion, for example: BLINDNESS.
     */
    POTION(Material.POTION, Material.BLUE_STAINED_GLASS_PANE),
    /**
     * Text stores string value, for example: "Hello world". Text can be formatted with Minecraft color codes.
     */
    TEXT(Material.BOOK, Material.BROWN_STAINED_GLASS_PANE),
    /**
     * Variable Link has name of real variable in plot. It stores value from real variable.
     */
    VARIABLE(Material.MAGMA_CREAM, Material.YELLOW_STAINED_GLASS_PANE),
    /**
     * Event Value Link stores type of EventVariable for getting value.
     */
    EVENT_VALUE(Material.NAME_TAG, Material.YELLOW_STAINED_GLASS_PANE),
    /**
     * Vector stores Minecraft vector coordinates.
     */
    VECTOR(Material.PRISMARINE_SHARD, Material.LIGHT_BLUE_STAINED_GLASS_PANE),
    /**
     * Color stores RGB color.
     */
    COLOR(Material.BLACK_DYE, Material.LIGHT_BLUE_STAINED_GLASS),
    /**
     * Any stores ItemStack.
     */
    ANY(Material.AIR, Material.BLACK_STAINED_GLASS_PANE);

    private final Material itemMaterial;
    private final Material menuGlass;

    ValueType(Material item, Material menuGlass) {
        this.itemMaterial = item;
        this.menuGlass = menuGlass;
    }

    public Material getMenuGlass() {
        return menuGlass;
    }

    public ItemStack getGlassItem(ActionType action, String path) {
        String messagePath = "items.developer.actions." + action.name().toLowerCase().replace("_", "-") + ".placeholders." + path;
        ItemStack itemStack = createItem(getMenuGlass(), 1);
        ItemMeta meta = itemStack.getItemMeta();
        if (!messageExists(messagePath + ".name")) {
            meta.setDisplayName(getLocaleItemName("items.developer.placeholders." + this.name().toLowerCase() + ".name"));
        } else {
            meta.setDisplayName(getLocaleItemName(messagePath + ".name"));
        }
        if (!messageExists(messagePath + ".lore")) {
            meta.setLore(getLocaleItemDescription("items.developer.placeholders." + this.name().toLowerCase() + ".lore"));
        } else {
            meta.setLore(getLocaleItemDescription(messagePath + ".lore"));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ValueType parseString(String type) {
        for (ValueType varType : values()) {
            if (varType.name().equalsIgnoreCase(type)) return varType;
        }
        return TEXT;
    }

    public static ValueType getByMaterial(Material material) {
        for (ValueType varType : values()) {
            if (varType.itemMaterial == material) return varType;
        }
        return TEXT;
    }

    public static ValueType getByObject(Object object) {
        if (object instanceof Float || object instanceof Double || object instanceof Integer || object instanceof Byte) {
            return NUMBER;
        } else if (object instanceof String) {
            return TEXT;
        } else if (object instanceof VariableLink) {
            return VARIABLE;
        } else if (object instanceof EventValueLink) {
            return EVENT_VALUE;
        } else if (object instanceof Boolean) {
            return BOOLEAN;
        } else if (object instanceof List) {
            return LIST;
        } else if (object instanceof Map) {
            return MAP;
        } else if (object instanceof ItemStack) {
            return ITEM;
        } else if (object instanceof Location) {
            return LOCATION;
        }
        return null;
    }

    public String getLocaleName() {
        return getLocaleMessage("environment.values." + name().toLowerCase().replace("_","-"),false);
    }

}
