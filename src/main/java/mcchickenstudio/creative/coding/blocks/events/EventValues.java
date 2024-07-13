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

package mcchickenstudio.creative.coding.blocks.events;

import mcchickenstudio.creative.coding.menus.MenusCategory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

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

        PLOT_NAME                       (Material.NAME_TAG,         MenusCategory.WORLD, String.class),
        PLOT_DESCRIPTION                (Material.BOOK,             MenusCategory.WORLD, String.class),
        PLOT_ONLINE                     (Material.PUFFERFISH,       MenusCategory.WORLD, Integer.class),
        PLOT_CUSTOM_ID                  (Material.LEAD,             MenusCategory.WORLD, String.class),
        PLOT_ID                         (Material.SLIME_BALL,       MenusCategory.WORLD, String.class),
        PLOT_ICON                       (Material.DIAMOND,          MenusCategory.WORLD, ItemStack.class),
        PLOT_REPUTATION                 (Material.TURTLE_HELMET,    MenusCategory.WORLD, ItemStack.class),
        PLOT_ENTITIES_AMOUNT            (Material.CHICKEN_SPAWN_EGG,  MenusCategory.WORLD, Integer.class),
        PLOT_ENTITIES_AMOUNT_LIMIT      (Material.SPAWNER,          MenusCategory.WORLD, Integer.class),
        PLOT_LAST_REDSTONE_OPERATIONS   (Material.REDSTONE,         MenusCategory.WORLD, String.class),
        PLOT_REDSTONE_OPERATIONS_LIMIT  (Material.REDSTONE_ORE,     MenusCategory.WORLD, String.class),
        PLOT_VARIABLES_AMOUNT           (Material.MAGMA_CREAM,      MenusCategory.WORLD, String.class),
        PLOT_VARIABLES_AMOUNT_LIMIT     (Material.MAGMA_BLOCK,      MenusCategory.WORLD, String.class),

        WORLD_TIME              (Material.GRASS_BLOCK,              MenusCategory.WORLD,  Long.class),
        CLEAR_WEATHER_DURATION  (Material.SUNFLOWER,                MenusCategory.WORLD,  Integer.class),
        THUNDER_WEATHER_DURATION(Material.WATER_BUCKET,             MenusCategory.WORLD,  Integer.class),
        UNIX_TIME               (Material.CLOCK,                    MenusCategory.WORLD, Long.class),
        UNIX_TIME_HOURS         (Material.CHEST_MINECART,           MenusCategory.WORLD,  Integer.class),
        UNIX_TIME_MINUTES       (Material.HOPPER_MINECART,          MenusCategory.WORLD,  Integer.class),
        UNIX_TIME_SECONDS       (Material.MINECART,                 MenusCategory.WORLD,  Integer.class),

        ITEM                    (Material.ITEM_FRAME,               MenusCategory.EVENTS, ItemStack.class),
        //CLICKED_ITEM            (Material.GLOW_ITEM_FRAME,          MenusCategory.EVENTS, ItemStack.class),
        MESSAGE                 (Material.BOOK,                     MenusCategory.EVENTS, String.class),
        BLOCK                   (Material.GRASS_BLOCK,              MenusCategory.EVENTS, Block.class),
        PURCHASE_ID(Material.GOLDEN_APPLE,             MenusCategory.EVENTS, String.class),
        PURCHASE_NAME(Material.GOLD_BLOCK,               MenusCategory.EVENTS, String.class),
        PURCHASE_PRICE(Material.GOLD_INGOT,               MenusCategory.EVENTS, Integer.class),
        PURCHASE_SAVE           (Material.YELLOW_SHULKER_BOX,       MenusCategory.EVENTS, Boolean.class),

        NICKNAME                (Material.NAME_TAG,                 MenusCategory.ENTITY, String.class),
        UUID                    (Material.KNOWLEDGE_BOOK,           MenusCategory.ENTITY, String.class),
        PING                    (Material.SCULK_SENSOR,             MenusCategory.ENTITY, Integer.class),
        LOCATION                (Material.PAPER,                    MenusCategory.ENTITY, Location.class),
        ITEM_IN_MAIN_HAND       (Material.WOODEN_SWORD,             MenusCategory.ENTITY, ItemStack.class),
        ITEM_IN_OFF_HAND        (Material.SHIELD,                   MenusCategory.ENTITY, ItemStack.class),
        HELMET                  (Material.NETHERITE_HELMET,         MenusCategory.ENTITY, ItemStack.class),
        CHESTPLATE              (Material.NETHERITE_CHESTPLATE,     MenusCategory.ENTITY, ItemStack.class),
        LEGGINGS                (Material.NETHERITE_LEGGINGS,       MenusCategory.ENTITY, ItemStack.class),
        BOOTS                   (Material.NETHERITE_BOOTS,          MenusCategory.ENTITY, ItemStack.class),
        LAST_DEATH_LOCATION     (Material.SKELETON_SKULL,           MenusCategory.ENTITY, Location.class),
        HEALTH                  (Material.APPLE,                    MenusCategory.ENTITY, Double.class),
        HUNGER                  (Material.COOKED_CHICKEN,           MenusCategory.ENTITY, Double.class),
        EYE_LOCATION            (Material.ENDER_EYE,                MenusCategory.ENTITY, Location.class),
        WALK_SPEED              (Material.CHAINMAIL_BOOTS,          MenusCategory.ENTITY, Float.class),
        FLY_SPEED               (Material.FEATHER,                  MenusCategory.ENTITY, Float.class),
        COMPASS_TARGET          (Material.COMPASS,                  MenusCategory.ENTITY, Location.class),
        LAST_DAMAGE             (Material.REDSTONE,                 MenusCategory.ENTITY, Double.class),
        LAST_DAMAGE_CAUSE       (Material.REDSTONE_BLOCK,           MenusCategory.ENTITY, String.class),
        MAX_HEALTH              (Material.GOLDEN_APPLE,             MenusCategory.ENTITY, Double.class),
        FALL_DISTANCE           (Material.RABBIT_FOOT,              MenusCategory.ENTITY, Float.class),
        NO_DAMAGE_TICKS         (Material.TOTEM_OF_UNDYING,         MenusCategory.ENTITY, Integer.class),
        FIRE_TICKS              (Material.CAMPFIRE,                 MenusCategory.ENTITY, Integer.class),
        FREEZE_TICKS            (Material.ICE,                      MenusCategory.ENTITY, Integer.class),
        EXPERIENCE              (Material.SLIME_SPAWN_EGG,          MenusCategory.ENTITY, Float.class),
        EXPERIENCE_LEVEL        (Material.SUGAR_CANE,               MenusCategory.ENTITY, Integer.class),
        TOTAL_EXPERIENCE        (Material.EXPERIENCE_BOTTLE,        MenusCategory.ENTITY, Integer.class),
        GAME_MODE               (Material.CRAFTING_TABLE,           MenusCategory.ENTITY, String.class),
        MAX_NO_DAMAGE_TICKS     (Material.ENCHANTED_GOLDEN_APPLE,   MenusCategory.ENTITY, Integer.class),
        CAN_PICKUP_ITEM         (Material.GLOWSTONE_DUST,           MenusCategory.ENTITY, Boolean.class),
        ARROWS_IN_BODY          (Material.ARROW,                    MenusCategory.ENTITY, Integer.class),
        SHIELD_BLOCKING_DELAY   (Material.SHIELD,                   MenusCategory.ENTITY, Integer.class),
        BEE_STINGER_COOLDOWN    (Material.BEE_NEST,                 MenusCategory.ENTITY, Integer.class);

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
            return getLocaleMessage("menus.developer.event-values.items." + this.name().toLowerCase() + ".name" ,false);
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
