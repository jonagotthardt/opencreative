/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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

package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>WorldTemplate</h1>
 * This class represents a world template, that can be
 * used for creating a new worlds. Template folders are
 * located in /plugins/OpenCreative/templates/ directory.
 * <p>
 * Template folder must be Minecraft world folder.
 */
public final class WorldTemplate extends WorldGenerator {

    private final String folderName;

    /**
     * Constructor of world template.
     *
     * @param id          short id of world generator that will be used in world generation menu.
     *                    <p>
     *                    It must be lower-snake-cased, for example: "flat", "nostalgia_world".
     *                    If some of registered generators has same ID as new, it will be not added.
     * @param displayIcon icon of world generator that will be displayed in world generation menu.
     * @param folderName  name of folder from /plugins/OpenCreative/templates/.
     */
    public WorldTemplate(@NotNull String id, @NotNull ItemStack displayIcon, @NotNull String folderName) {
        super(id, displayIcon);
        this.folderName = folderName;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Copied " + folderName + " world from templates directory";
    }

    /**
     * Returns name of world folder, located in /plugins/OpenCreative/templates directory.
     *
     * @return name of world folder for copying and pasting.
     */
    public @NotNull String getFolderName() {
        return folderName;
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator, @NotNull String biome) {
    }

    @Override
    public void afterCreation(@NotNull World world) {
    }
}
