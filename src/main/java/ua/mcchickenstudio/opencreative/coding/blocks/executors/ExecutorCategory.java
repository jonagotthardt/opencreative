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

package ua.mcchickenstudio.opencreative.coding.blocks.executors;

import ua.mcchickenstudio.opencreative.utils.MessageUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

/**
 * <h1>ExecutorCategory</h1>
 * This enum defines different categories for coding blocks with executor type.
 * Every category has material of block that player will place. Members are:
 * EVENT_PLAYER, EVENT_WORLD, CYCLE, FUNCTION and etc.
 * @since 5.0
 * @version 5.0
 * @author McChicken Studio
 */
public enum ExecutorCategory {

    EVENT_PLAYER(Material.DIAMOND_BLOCK, Material.DIAMOND_ORE, NamedTextColor.AQUA),
    EVENT_ENTITY(Material.GOLD_BLOCK, Material.GOLD_ORE, NamedTextColor.YELLOW),
    EVENT_WORLD(Material.REDSTONE_BLOCK, Material.REDSTONE_ORE, NamedTextColor.RED),
    CYCLE(Material.OXIDIZED_COPPER, Material.WAXED_OXIDIZED_CUT_COPPER, NamedTextColor.DARK_AQUA),
    FUNCTION(Material.LAPIS_BLOCK, Material.DEEPSLATE_LAPIS_ORE, NamedTextColor.BLUE),
    METHOD(Material.EMERALD_BLOCK, Material.EMERALD_ORE, NamedTextColor.GREEN);

    private final Material block;
    private final Material additionalBlock;
    private final NamedTextColor color;

    ExecutorCategory(Material block, Material additionalBlock, NamedTextColor color) {
        this.block = block;
        this.additionalBlock = additionalBlock;
        this.color = color;
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

    public NamedTextColor getColor() {
        return color;
    }

    public final String getLocaleName() {
        return MessageUtils.getLocaleMessage("blocks." + this.name().toLowerCase(), false);
    }

    public Material getAdditionalBlock() {
        return additionalBlock;
    }
}
