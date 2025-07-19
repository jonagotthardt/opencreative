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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;

/**
 * <h1>Target</h1>
 * This enum represents targets, that can be specified
 * for executing actions and conditions. Target is a
 * list of entities in world.
 * @since 5.0
 * @version 5.0
 * @author McChicken Studio
 */
public enum Target {

    DEFAULT(Material.LIGHT_BLUE_STAINED_GLASS),
    SELECTED(Material.PURPUR_BLOCK),
    ALL_PLAYERS(Material.BEACON),
    ALL_ENTITIES(Material.PUFFERFISH),
    RANDOM_PLAYER(Material.PLAYER_HEAD),
    RANDOM_TARGET(Material.PURPUR_STAIRS),
    KILLER(Material.NETHERITE_SWORD),
    VICTIM(Material.SKELETON_SKULL);

    private final Material icon;

    Target(Material icon) {
        this.icon = icon;
    }

    public Material getIcon() {
        return icon;
    }

    public static Target getByMaterial(Material material) {
        for (Target target : values()) {
            if (target.getIcon() == material) {
                return target;
            }
        }
        return DEFAULT;
    }

    public static @NotNull Target getByText(@NotNull String text) {
        for (Target target : values()) {
            if (target.name().equals(text)) {
                return target;
            }
        }
        return SELECTED;
    }

    public static Target getBySign(Location location) {
        Block signBlock = location.getBlock().getRelative(BlockFace.SOUTH);
        String string = getSignLine(signBlock.getLocation(), (byte) 4);
        if (string != null && !string.isEmpty()) {
            for (Target selection : values()) {
                if (selection.name().equalsIgnoreCase(string)) {
                    return selection;
                }
            }
        }
        return DEFAULT;
    }
}
