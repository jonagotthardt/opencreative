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

package mcchickenstudio.creative.coding.blocks.actions;

import mcchickenstudio.creative.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

public enum ActionCategory {

    PLAYER_ACTION(Material.COBBLESTONE, Material.STONE, ChatColor.GRAY),
    WORLD_ACTION(Material.NETHER_BRICKS, Material.NETHERRACK, ChatColor.RED),
    VARIABLE_ACTION(Material.IRON_BLOCK, Material.STONE, ChatColor.WHITE),
    LAUNCH_FUNCTION_ACTION(Material.LAPIS_ORE, Material.STONE, ChatColor.AQUA),
    CONTROL_ACTION(Material.COAL_BLOCK, Material.COAL_ORE, ChatColor.DARK_GRAY),
    PLAYER_CONDITION(Material.OAK_PLANKS, Material.PISTON, ChatColor.GOLD),
    VARIABLE_CONDITION(Material.OBSIDIAN, Material.PISTON, ChatColor.BLUE);
    //HANDLER_ACTION(Material.DARK_PRISMARINE, Material.PISTON, ChatColor.GREEN),
    //REPEAT_ACTION(Material.PRISMARINE, Material.PISTON, ChatColor.AQUA);


    //ELSE_CONDITION(Material.END_STONE, ChatColor.YELLOW);

    private final Material block;
    private final Material additionalBlock;
    private final ChatColor color;

    ActionCategory(Material block, Material additionalBlock, ChatColor color) {
        this.block = block;
        this.additionalBlock = additionalBlock;
        this.color = color;
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
        return this == PLAYER_CONDITION || this == VARIABLE_CONDITION;
    }

    public ChatColor getColor() {
        return color;
    }

    public final String getLocaleName() {
        return MessageUtils.getLocaleMessage("blocks." + this.name().toLowerCase(), false);
    }

    public Material getAdditionalBlock() {
        return additionalBlock;
    }

    public Material getBlock() {
        return block;
    }
}
