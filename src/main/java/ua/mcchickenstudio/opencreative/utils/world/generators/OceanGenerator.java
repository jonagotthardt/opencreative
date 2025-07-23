package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class OceanGenerator extends WorldGenerator {

    public OceanGenerator() {
        super("water", new ItemStack(Material.WATER_BUCKET));
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator) {
        creator.type(WorldType.FLAT);
        creator.generator(this);
    }

    @Override
    public void afterCreation(@NotNull World world) {
        world.setSpawnLocation(0, 8, 0);
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates ocean world";
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
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
    }

}
