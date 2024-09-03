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

package mcchickenstudio.creative.coding.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;

public enum MenusCategory {

    FIGHTING(Material.NETHERITE_SWORD),
    INTERACTION(Material.GRASS_BLOCK),
    INVENTORY(Material.CHEST),
    MOVEMENT(Material.CHAINMAIL_BOOTS),
    BLOCKS(Material.STRUCTURE_BLOCK),
    WORLD(Material.BEACON),
    ENTITY(Material.VILLAGER_SPAWN_EGG),
    COMMUNICATION(Material.OAK_SIGN),
    STATE(Material.NAME_TAG),
    LINES(Material.REPEATING_COMMAND_BLOCK),
    APPEARANCE(Material.ARMOR_STAND),
    EVENTS(Material.NETHER_BRICKS),
    PARAMS(Material.ITEM_FRAME),
    OTHER(Material.PUMPKIN_SEEDS),

    TEXT_OPERATIONS(Material.BOOK),
    NUMBER_OPERATIONS(Material.SLIME_BALL),
    LOCATION_OPERATIONS(Material.PAPER),
    ITEM_OPERATIONS(Material.GLOW_ITEM_FRAME),
    LIST_OPERATIONS(Material.BOOKSHELF),
    MAP_OPERATIONS(Material.CHEST_MINECART);


    private final Material material;

    MenusCategory(Material material) {
        this.material = material;
    }

    public ItemStack getItem(String blockCategory) {
        return createItem(material,1,"items.developer.categories." + blockCategory + "." + this.name().toLowerCase());
    }

    public static MenusCategory getByMaterial(Material material) {
        for (MenusCategory category : values()) {
            if (category.material == material) {
                return category;
            }
        }
        return null;
    }

}
