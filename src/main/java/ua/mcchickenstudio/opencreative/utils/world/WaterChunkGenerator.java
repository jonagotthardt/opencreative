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

package ua.mcchickenstudio.opencreative.utils.world;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * <h1>WaterChunkGenerator</h1>
 * This class represents a ChunkGenerator, that
 * generates ocean world with water, sand and bedrock.
 */
public class WaterChunkGenerator extends ChunkGenerator {

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunkData.setBlock(i, 0, j, Material.BEDROCK);
                chunkData.setBlock(i, 1, j, Material.SAND);
                chunkData.setBlock(i, 2, j, Material.SAND);
                chunkData.setBlock(i, 3, j, Material.WATER);
                chunkData.setBlock(i, 4, j, Material.WATER);
                chunkData.setBlock(i, 5, j, Material.WATER);
                chunkData.setBlock(i, 6, j, Material.WATER);
                chunkData.setBlock(i, 7, j, Material.WATER);
            }
        }
        return chunkData;
    }

}
