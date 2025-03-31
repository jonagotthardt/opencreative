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

package ua.mcchickenstudio.opencreative.coding.blocks.actions;

import ua.mcchickenstudio.opencreative.utils.MessageUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public enum ActionCategory {

    PLAYER_ACTION(Material.COBBLESTONE, Material.STONE, NamedTextColor.GRAY, Material.GRAY_STAINED_GLASS_PANE),
    ENTITY_ACTION(Material.MOSSY_COBBLESTONE, Material.STONE, NamedTextColor.GREEN, Material.GREEN_STAINED_GLASS_PANE),
    WORLD_ACTION(Material.NETHER_BRICKS, Material.NETHERRACK, NamedTextColor.RED, Material.RED_STAINED_GLASS_PANE),
    VARIABLE_ACTION(Material.IRON_BLOCK, Material.IRON_ORE, NamedTextColor.WHITE, Material.WHITE_STAINED_GLASS_PANE),
    SELECTION_ACTION(Material.PURPUR_BLOCK, Material.PURPUR_PILLAR, NamedTextColor.LIGHT_PURPLE, Material.PINK_STAINED_GLASS_PANE),
    LAUNCH_FUNCTION_ACTION(Material.LAPIS_ORE, Material.STONE, NamedTextColor.AQUA, Material.BLUE_STAINED_GLASS_PANE),
    LAUNCH_METHOD_ACTION(Material.EMERALD_ORE, Material.STONE, NamedTextColor.GREEN, Material.LIME_STAINED_GLASS_PANE),
    CONTROL_ACTION(Material.COAL_BLOCK, Material.COAL_ORE, NamedTextColor.DARK_GRAY, Material.GRAY_STAINED_GLASS_PANE),
    HANDLER_ACTION(Material.DARK_PRISMARINE, Material.PISTON, NamedTextColor.GREEN, Material.BLUE_STAINED_GLASS_PANE),
    REPEAT_ACTION(Material.PRISMARINE, Material.PISTON, NamedTextColor.AQUA, Material.LIGHT_BLUE_STAINED_GLASS_PANE),

    PLAYER_CONDITION(Material.OAK_PLANKS, Material.PISTON, NamedTextColor.GOLD, Material.ORANGE_STAINED_GLASS_PANE),
    VARIABLE_CONDITION(Material.OBSIDIAN, Material.PISTON, NamedTextColor.BLUE, Material.BLUE_STAINED_GLASS_PANE),
    WORLD_CONDITION(Material.RED_NETHER_BRICKS, Material.PISTON, NamedTextColor.RED, Material.RED_STAINED_GLASS_PANE),
    ENTITY_CONDITION(Material.BRICKS, Material.PISTON, NamedTextColor.RED, Material.RED_STAINED_GLASS_PANE),
    ELSE_CONDITION(Material.END_STONE, Material.PISTON, NamedTextColor.YELLOW, Material.YELLOW_STAINED_GLASS_PANE);

    private final Material block;
    private final Material additionalBlock;
    private final NamedTextColor color;
    private final Material stainedPane;

    ActionCategory(Material block, Material additionalBlock, NamedTextColor color, Material stainedPane) {
        this.block = block;
        this.additionalBlock = additionalBlock;
        this.color = color;
        this.stainedPane = stainedPane;
    }

    public static ActionCategory getByMaterial(Material material) {
        for (ActionCategory category : values()) {
            if (category.block == material) return category;
        }
        return null;
    }

    public boolean isMultiAction() {
        return this.additionalBlock == Material.PISTON;
    }

    public boolean isCondition() {
        // FIXME: maybe we should use class extends Condition?
        return this == PLAYER_CONDITION || this == VARIABLE_CONDITION || this == WORLD_CONDITION || this == ENTITY_CONDITION || this == ELSE_CONDITION;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public final String getLocaleName() {
        return MessageUtils.getLocaleMessage("blocks." + this.name().toLowerCase(), false);
    }

    public Material getStainedPane() {
        return stainedPane;
    }

    public Material getAdditionalBlock() {
        return additionalBlock;
    }

    public Material getBlock() {
        return block;
    }
}
