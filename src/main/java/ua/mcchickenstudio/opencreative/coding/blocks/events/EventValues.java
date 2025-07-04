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

import org.bukkit.util.Vector;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class EventValues {

    private final Map<Variable,Object> variables = new HashMap<>();

    public void setVariable(Variable var, Object value) {
        variables.put(var,value);
    }

    public Object getVarValue(Variable var) {
        if (variables.containsKey(var)) {
            return variables.get(var);
        }
        return null;
    }

    public void clear() {
        variables.clear();
    }

    public Map<Variable, Object> getMap() {
        return variables;
    }

    public enum Variable {

        PLANET_NAME                       (Material.NAME_TAG,         MenusCategory.WORLD, String.class),
        PLANET_DESCRIPTION                (Material.BOOK,             MenusCategory.WORLD, String.class),
        PLANET_ONLINE                     (Material.PUFFERFISH,       MenusCategory.WORLD, Integer.class),
        PLANET_CUSTOM_ID                  (Material.LEAD,             MenusCategory.WORLD, String.class),
        WORLD_SPAWN                       (Material.SPAWNER,          MenusCategory.WORLD, Location.class),
        PLANET_ID                         (Material.SLIME_BALL,       MenusCategory.WORLD, String.class),
        PLANET_ICON                       (Material.DIAMOND,          MenusCategory.WORLD, ItemStack.class),
        PLANET_REPUTATION                 (Material.TURTLE_HELMET,    MenusCategory.WORLD, ItemStack.class),
        PLANET_ENTITIES_AMOUNT            (Material.CHICKEN_SPAWN_EGG,  MenusCategory.WORLD, Integer.class),
        PLANET_ENTITIES_AMOUNT_LIMIT      (Material.STRIDER_SPAWN_EGG,          MenusCategory.WORLD, Integer.class),
        PLANET_LAST_REDSTONE_OPERATIONS   (Material.REDSTONE,         MenusCategory.WORLD, String.class),
        PLANET_REDSTONE_OPERATIONS_LIMIT  (Material.REDSTONE_ORE,     MenusCategory.WORLD, String.class),
        PLANET_VARIABLES_AMOUNT           (Material.MAGMA_CREAM,      MenusCategory.WORLD, String.class),
        PLANET_VARIABLES_AMOUNT_LIMIT     (Material.MAGMA_BLOCK,      MenusCategory.WORLD, String.class),
        WORLD_TIME                        (Material.GRASS_BLOCK,              MenusCategory.WORLD,  Long.class),
        CLEAR_WEATHER_DURATION            (Material.SUNFLOWER,                MenusCategory.WORLD,  Integer.class),
        THUNDER_WEATHER_DURATION          (Material.WATER_BUCKET,             MenusCategory.WORLD,  Integer.class),
        UNIX_TIME                         (Material.CLOCK,                    MenusCategory.WORLD, Long.class),
        UNIX_TIME_HOURS                   (Material.CHEST_MINECART,           MenusCategory.WORLD,  Integer.class),
        UNIX_TIME_MINUTES                 (Material.HOPPER_MINECART,          MenusCategory.WORLD,  Integer.class),
        UNIX_TIME_SECONDS                 (Material.MINECART,                 MenusCategory.WORLD,  Integer.class),

        BED_ENTER_RESULT        (Material.WHITE_BED,               MenusCategory.EVENTS, String.class),
        BED                     (Material.RED_BED,                 MenusCategory.EVENTS, Block.class),
        ITEM                    (Material.ITEM_FRAME,               MenusCategory.EVENTS, ItemStack.class),
        NEW_ITEM                (Material.GLOW_ITEM_FRAME,               MenusCategory.EVENTS, ItemStack.class),
        BLOCK_INTERACTION_TYPE  (Material.GOLDEN_HOE,                     MenusCategory.EVENTS, String.class),
        CURSOR_ITEM             (Material.TRIPWIRE_HOOK,                     MenusCategory.EVENTS, ItemStack.class),
        CLICK_TYPE              (Material.LEVER,                     MenusCategory.EVENTS, String.class),
        CLICKED_SLOT            (Material.SLIME_BALL,                     MenusCategory.EVENTS, Integer.class),
        EQUIPMENT_SLOT          (Material.NETHERITE_SWORD,                     MenusCategory.EVENTS, String.class),
        OLD_SLOT                (Material.SLIME_BALL,                     MenusCategory.EVENTS, Integer.class),
        NEW_SLOT                (Material.SLIME_BALL,                     MenusCategory.EVENTS, Integer.class),

        MESSAGE                 (Material.BOOK,                     MenusCategory.EVENTS, String.class),
        BLOCK                   (Material.GRASS_BLOCK,              MenusCategory.EVENTS, Block.class),
        BLOCK_MATERIAL          (Material.GLOBE_BANNER_PATTERN,              MenusCategory.EVENTS, Block.class),
        BLOCK_LOCATION          (Material.PAPER,              MenusCategory.EVENTS, Block.class),
        IS_CHUNK_NEW            (Material.GLASS,               MenusCategory.EVENTS, Boolean.class),
        TRANSFER_KEY            (Material.CALIBRATED_SCULK_SENSOR,               MenusCategory.EVENTS, String.class),
        TRANSFER_VALUE            (Material.SCULK_SENSOR,               MenusCategory.EVENTS, Object.class),
        URL                     (Material.NAME_TAG,               MenusCategory.EVENTS, String.class),
        URL_RESPONSE_CODE       (Material.SLIME_BALL,               MenusCategory.EVENTS, Integer.class),
        URL_RESPONSE                     (Material.KNOWLEDGE_BOOK,               MenusCategory.EVENTS, String.class),

        PURCHASE_ID(Material.GOLDEN_APPLE,             MenusCategory.EVENTS, String.class),
        PURCHASE_NAME(Material.GOLD_BLOCK,               MenusCategory.EVENTS, String.class),
        PURCHASE_PRICE(Material.GOLD_INGOT,               MenusCategory.EVENTS, Integer.class),
        PURCHASE_SAVE           (Material.YELLOW_SHULKER_BOX,       MenusCategory.EVENTS, Boolean.class),
        FOOD_LEVEL              (Material.COOKED_CHICKEN, MenusCategory.EVENTS, Integer.class),
        DAMAGE_CAUSE            (Material.REDSTONE_TORCH,           MenusCategory.EVENTS, String.class),
        DAMAGE                  (Material.SOUL_TORCH,           MenusCategory.EVENTS, String.class),
        EVENT_EXPERIENCE        (Material.SLIME_SPAWN_EGG,          MenusCategory.EVENTS, Float.class),


        CLIENT_BRAND            (Material.GRASS_BLOCK,              MenusCategory.PLAYER, String.class),
        HUNGER                  (Material.COOKED_CHICKEN,           MenusCategory.PLAYER, Double.class),
        LAST_DEATH_LOCATION     (Material.SKELETON_SKULL,           MenusCategory.PLAYER, Location.class),
        PING                    (Material.SCULK_SENSOR,             MenusCategory.PLAYER, Integer.class),
        EXPERIENCE              (Material.SLIME_SPAWN_EGG,          MenusCategory.PLAYER, Float.class),
        EXPERIENCE_LEVEL        (Material.SUGAR_CANE,               MenusCategory.PLAYER, Integer.class),
        TOTAL_EXPERIENCE        (Material.EXPERIENCE_BOTTLE,        MenusCategory.PLAYER, Integer.class),
        GAME_MODE               (Material.CRAFTING_TABLE,           MenusCategory.PLAYER, String.class),
        LOCALE_COUNTRY          (Material.HEART_OF_THE_SEA,         MenusCategory.PLAYER, String.class),
        LOCALE_DISPLAY_COUNTRY  (Material.HEART_POTTERY_SHERD,      MenusCategory.PLAYER, String.class),
        LOCALE_LANGUAGE         (Material.HEARTBREAK_POTTERY_SHERD, MenusCategory.PLAYER, String.class),
        LOCALE_DISPLAY_LANGUAGE (Material.BLUE_STAINED_GLASS,       MenusCategory.PLAYER, String.class),
        COMPASS_TARGET          (Material.COMPASS,                  MenusCategory.PLAYER, Location.class),

        NICKNAME                (Material.NAME_TAG,                 MenusCategory.ENTITY, String.class),
        TYPE                    (Material.SPAWNER,                  MenusCategory.ENTITY, String.class),
        UUID                    (Material.KNOWLEDGE_BOOK,           MenusCategory.ENTITY, String.class),
        LOCATION                (Material.PAPER,                    MenusCategory.ENTITY, Location.class),
        VELOCITY                (Material.PRISMARINE_SHARD,         MenusCategory.ENTITY, Vector.class),
        ITEM_IN_MAIN_HAND       (Material.WOODEN_SWORD,             MenusCategory.ENTITY, ItemStack.class),
        ITEM_IN_OFF_HAND        (Material.SHIELD,                   MenusCategory.ENTITY, ItemStack.class),
        HELMET                  (Material.NETHERITE_HELMET,         MenusCategory.ENTITY, ItemStack.class),
        CHESTPLATE              (Material.NETHERITE_CHESTPLATE,     MenusCategory.ENTITY, ItemStack.class),
        LEGGINGS                (Material.NETHERITE_LEGGINGS,       MenusCategory.ENTITY, ItemStack.class),
        BOOTS                   (Material.NETHERITE_BOOTS,          MenusCategory.ENTITY, ItemStack.class),
        HEALTH                  (Material.APPLE,                    MenusCategory.ENTITY, Double.class),
        EYE_LOCATION            (Material.ENDER_EYE,                MenusCategory.ENTITY, Location.class),
        TARGET_ENTITY           (Material.ENDER_PEARL,              MenusCategory.ENTITY, String.class),
        TARGET_BLOCK            (Material.END_PORTAL_FRAME,         MenusCategory.ENTITY, Location.class),

        WALK_SPEED              (Material.CHAINMAIL_BOOTS,          MenusCategory.ENTITY, Float.class),
        FLY_SPEED               (Material.FEATHER,                  MenusCategory.ENTITY, Float.class),
        LAST_DAMAGE             (Material.REDSTONE,                 MenusCategory.ENTITY, Double.class),
        LAST_DAMAGE_CAUSE       (Material.REDSTONE_BLOCK,           MenusCategory.ENTITY, String.class),
        MAX_HEALTH              (Material.GOLDEN_APPLE,             MenusCategory.ENTITY, Double.class),
        FALL_DISTANCE           (Material.RABBIT_FOOT,              MenusCategory.ENTITY, Float.class),
        NO_DAMAGE_TICKS         (Material.TOTEM_OF_UNDYING,         MenusCategory.ENTITY, Integer.class),
        FIRE_TICKS              (Material.CAMPFIRE,                 MenusCategory.ENTITY, Integer.class),
        FREEZE_TICKS            (Material.ICE,                      MenusCategory.ENTITY, Integer.class),
        MAX_NO_DAMAGE_TICKS     (Material.ENCHANTED_GOLDEN_APPLE,   MenusCategory.ENTITY, Integer.class),
        CAN_PICKUP_ITEM         (Material.GLOWSTONE_DUST,           MenusCategory.ENTITY, Boolean.class),
        ARROWS_IN_BODY          (Material.ARROW,                    MenusCategory.ENTITY, Integer.class),
        SHIELD_BLOCKING_DELAY   (Material.SHIELD,                   MenusCategory.ENTITY, Integer.class),
        BEE_STINGER_COOLDOWN    (Material.BEE_NEST,                 MenusCategory.ENTITY, Integer.class),
        REMAINING_AIR           (Material.SPONGE,                   MenusCategory.ENTITY, Integer.class),
        MAXIMUM_AIR           (Material.POTION,                   MenusCategory.ENTITY, Integer.class),

        ;


        final Class<?> valueClass;
        final MenusCategory category;
        final Material icon;

        Variable(Material icon, MenusCategory category, Class<?> valueClass) {
            this.valueClass = valueClass;
            this.category = category;
            this.icon = icon;
        }

        public Class<?> getValueClass() {
            return valueClass;
        }

        public MenusCategory getCategory() {
            return category;
        }

        public Material getIcon() {
            return icon;
        }

        public String getLocaleName() {
            return getLocaleMessage("menus.developer.event-values.items." + this.name().toLowerCase().replace("_","-") + ".name" ,false);
        }

        public static List<Variable> getByCategories(MenusCategory menusCategory) {
            List<Variable> list = new ArrayList<>();
            for (Variable type : values()) {
                if (type.category == menusCategory) {
                    list.add(type);
                }
            }
            return list;
        }
    }


}
