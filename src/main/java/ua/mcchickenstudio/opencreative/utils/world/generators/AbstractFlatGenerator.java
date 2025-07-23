package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * <h1>AbstractFlatGenerator</h1>
 * This class represents a flat world generator, that
 * will generate layers of blocks. To add layers, just
 * put them in {@link AbstractFlatGenerator#blocks blocks map}, that contains
 * Y coordinates and materials of blocks.
 */
public abstract class AbstractFlatGenerator extends WorldGenerator {

    protected final Map<Integer, Material> blocks = new HashMap<>();

    public AbstractFlatGenerator(@NotNull String id, @NotNull ItemStack displayIcon) {
        super(id, displayIcon);
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator) {
        creator.type(WorldType.FLAT);
        creator.generator(this);
    }

    @Override
    public void afterCreation(@NotNull World world) {}

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int y = chunkData.getMinHeight(); y < chunkData.getMaxHeight(); y++) {
                    Material material = blocks.getOrDefault(y, Material.AIR);
                    if (!material.isBlock() || material == Material.AIR) {
                        continue;
                    }
                    chunkData.setBlock(i, y, j, material);
                }
            }
        }
    }

}
