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

package ua.mcchickenstudio.opencreative.indev;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;

/**
 * <h1>Items</h1>
 * This enum represents main items, that will be given to players
 * by doing something: joining the lobby, connecting to world
 * or entering to the coding world.
 * <p>
 * To get item, use {@link #get()} method.
 */
public enum Items {

    /**
     * For opening Worlds Browser menu in lobby.
     */
    WORLDS(Material.COMPASS, "lobby"),
    /**
     * For opening Own Worlds Browser menu in lobby.
     */
    OWN_WORLDS(Material.NETHER_STAR, "lobby"),
    /**
     * For viewing Changelogs in lobby.
     */
    CHANGELOGS(Material.WRITTEN_BOOK, "lobby"),

    /**
     * For opening World Settings menu in world, as its owner.
     */
    WORLD_SETTINGS(Material.COMPASS, "developer"),
    /**
     * For changing speed of flight in coding world.
     */
    FLY_SPEED_CHANGER(Material.FEATHER, "developer"),
    /**
     * For moving lines and marking them in coding world.
     */
    LINES_CONTROLLER(Material.COMPARATOR, "developer"),
    /**
     * For viewing coding tutorial in coding world.
     */
    CODING_BOOK(Material.WRITTEN_BOOK, "developer"),
    /**
     * For viewing values list in coding world.
     */
    VARIABLES(Material.IRON_INGOT, "developer"),

    EVENT_PLAYER(ExecutorCategory.EVENT_PLAYER),
    EVENT_WORLD(ExecutorCategory.EVENT_WORLD),
    EVENT_ENTITY(ExecutorCategory.EVENT_ENTITY),
    CYCLE(ExecutorCategory.CYCLE),
    METHOD(ExecutorCategory.METHOD),
    FUNCTION(ExecutorCategory.FUNCTION),

    PLAYER_ACTION(ActionCategory.PLAYER_ACTION),
    WORLD_ACTION(ActionCategory.WORLD_ACTION),
    ENTITY_ACTION(ActionCategory.ENTITY_ACTION),
    VARIABLE_ACTION(ActionCategory.VARIABLE_ACTION),
    REPEAT_ACTION(ActionCategory.REPEAT_ACTION),
    CONTROLLER_ACTION(ActionCategory.CONTROLLER_ACTION),
    CONTROL_ACTION(ActionCategory.CONTROL_ACTION),
    SELECTION_ACTION(ActionCategory.SELECTION_ACTION),

    LAUNCH_FUNCTION_ACTION(ActionCategory.LAUNCH_FUNCTION_ACTION),
    LAUNCH_METHOD_ACTION(ActionCategory.LAUNCH_METHOD_ACTION),

    PLAYER_CONDITION(ActionCategory.PLAYER_CONDITION),
    WORLD_CONDITION(ActionCategory.WORLD_CONDITION),
    ENTITY_CONDITION(ActionCategory.ENTITY_CONDITION),
    ELSE_CONDITION(ActionCategory.ELSE_CONDITION),
    VARIABLE_CONDITION(ActionCategory.VARIABLE_CONDITION);

    private final Material material;
    private final String group;

    /**
     * Creates main item with material and prefix group in localization file.
     * @param material material of item.
     * @param group group in localization file (lobby, developer).
     */
    Items(@NotNull Material material, @NotNull String group) {
        this.material = material;
        this.group = group;
    }

    /**
     * Creates main item by action block.
     * @param group group of action.
     */
    Items(@NotNull ActionCategory group) {
        this.material = group.getBlock();
        this.group = "developer";
    }

    /**
     * Creates main item by executor block.
     * @param group group of executor.
     */
    Items(@NotNull ExecutorCategory group) {
        this.material = group.getBlock();
        this.group = "developer";
    }

    /**
     * Returns material of main item.
     * @return material.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Creates and returns item, that can be given to player.
     * @return item to give to player.
     */
    public ItemStack get() {
        return createItem(material,1, "items." + group + "." + name()
                .toLowerCase().replace("_","-"));
    }

    public static @Nullable Items getById(@NotNull String id) {
        for (Items type : Items.values()) {
            if (type.name().equals(id)) {
                return type;
            }
        }
        return null;
    }
}
