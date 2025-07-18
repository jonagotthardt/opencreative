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

package ua.mcchickenstudio.opencreative.coding.blocks.executors;

import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;

/**
 * <h1>ExecutorCategory</h1>
 * This enum defines different categories for coding blocks with executor id.
 * Every category has material of block that player will place. Members are:
 * EVENT_PLAYER, EVENT_WORLD, CYCLE, FUNCTION and etc.
 * @since 5.0
 * @version 5.0
 * @author McChicken Studio
 */
public enum ExecutorCategory {

    EVENT_PLAYER(Material.DIAMOND_BLOCK, Material.DIAMOND_ORE, NamedTextColor.AQUA, Material.LIGHT_BLUE_STAINED_GLASS_PANE, MenusCategory.WORLD),
    EVENT_ENTITY(Material.GOLD_BLOCK, Material.GOLD_ORE, NamedTextColor.YELLOW, Material.YELLOW_STAINED_GLASS_PANE, MenusCategory.ENTITY_INTERACTION),
    EVENT_WORLD(Material.REDSTONE_BLOCK, Material.REDSTONE_ORE, NamedTextColor.RED, Material.RED_STAINED_GLASS_PANE, MenusCategory.WORLD_OTHER),
    CYCLE(Material.OXIDIZED_COPPER, Material.WAXED_OXIDIZED_CUT_COPPER, NamedTextColor.DARK_AQUA, Material.LIGHT_BLUE_STAINED_GLASS_PANE),
    FUNCTION(Material.LAPIS_BLOCK, Material.DEEPSLATE_LAPIS_ORE, NamedTextColor.BLUE, Material.BLUE_STAINED_GLASS_PANE),
    METHOD(Material.EMERALD_BLOCK, Material.DEEPSLATE_EMERALD_ORE, NamedTextColor.GREEN, Material.LIME_STAINED_GLASS_PANE);

    private final Material block;
    private final Material additionalBlock;
    private final NamedTextColor color;
    private final Material stainedPane;
    private final MenusCategory defaultCategory;

    ExecutorCategory(Material block, Material additionalBlock, NamedTextColor color, Material stainedPane) {
        this(block, additionalBlock, color, stainedPane, MenusCategory.OTHER);
    }

    ExecutorCategory(Material block, Material additionalBlock, NamedTextColor color, Material stainedPane, MenusCategory defaultCategory) {
        this.block = block;
        this.additionalBlock = additionalBlock;
        this.color = color;
        this.stainedPane = stainedPane;
        this.defaultCategory = defaultCategory;
    }

    public MenusCategory getDefaultCategory() {
        return defaultCategory;
    }

    public Material getBlock() {
        return block;
    }

    public static ExecutorCategory getByMaterial(Material material) {
        for (ExecutorCategory category : values()) {
            if (category.block == material) return category;
        }
        return null;
    }

    public Material getStainedPane() {
        return stainedPane;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public final String getLocaleName() {
        return MessageUtils.getLocaleMessage("blocks." + this.name().toLowerCase(), false);
    }

    public Material getAdditionalBlock() {
        return additionalBlock;
    }

    public ItemStack getItem() {
        return createItem(block,1,"items.developer." + name().toLowerCase().replace("_","-"),name().toLowerCase());
    }
}
