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

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class FlatGenerator extends WorldGenerator implements EnvironmentCapable, StructuresCapable {

    public FlatGenerator() {
        super("flat", new ItemStack(Material.MOSS_BLOCK));
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator) {
        creator.type(WorldType.FLAT);
    }

    @Override
    public void afterCreation(@NotNull World world) {
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        Material customFlatTerrainBlock = null;
        if (worldInfo.getEnvironment() == World.Environment.NETHER) customFlatTerrainBlock = Material.NETHERRACK;
        if (worldInfo.getEnvironment() == World.Environment.THE_END) customFlatTerrainBlock = Material.END_STONE;
        if (customFlatTerrainBlock == null) {
            super.generateSurface(worldInfo, random, chunkX, chunkZ, chunkData);
            return;
        }
        if (worldInfo.getEnvironment() == World.Environment.NETHER) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    chunkData.setBlock(i, -64, j, Material.BEDROCK);
                    chunkData.setBlock(i, -63, j, customFlatTerrainBlock);
                    chunkData.setBlock(i, -62, j, customFlatTerrainBlock);
                    chunkData.setBlock(i, -61, j, customFlatTerrainBlock);
                }
            }
        }
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates flat world";
    }

}
