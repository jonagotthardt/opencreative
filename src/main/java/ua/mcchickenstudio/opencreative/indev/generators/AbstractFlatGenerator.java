package ua.mcchickenstudio.opencreative.indev.generators;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;

public abstract class AbstractFlatGenerator extends WorldGenerator {

    private final Map<Integer, Material> blocks;

    /**
     * Constructor of world generator.
     *
     * @param id          short id of world generator that will be used in world generation menu.
     *                    <p>
     *                    It must be lower-snake-cased, for example: "flat", "nostalgia_world".
     *                    If some of registered generators has same ID as new, it will be not added.
     * @param displayIcon icon of world generator that will be displayed in world generation menu.
     */
    public AbstractFlatGenerator(@NotNull String id, @NotNull ItemStack displayIcon, @NotNull Map<Integer, Material> blocks) {
        super(id, displayIcon);
        this.blocks = blocks;
    }

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int y = chunkData.getMinHeight(); y < chunkData.getMaxHeight(); y++) {
                    Material material = blocks.getOrDefault(y, Material.AIR);
                    if (!material.isBlock()) material = Material.AIR;
                    chunkData.setBlock(i, y, j, material);
                }
            }
        }
        return chunkData;
    }

}
