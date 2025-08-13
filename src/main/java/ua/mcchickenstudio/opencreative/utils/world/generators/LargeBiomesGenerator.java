package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class LargeBiomesGenerator extends WorldGenerator {

    public LargeBiomesGenerator() {
        super("large_biomes", new ItemStack(Material.MYCELIUM));
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator) {
        creator.type(WorldType.LARGE_BIOMES);
    }

    @Override
    public void afterCreation(@NotNull World world) {
        int y = world.getHighestBlockYAt(0,0)+3;
        Location spawn = new Location(world, 0, y+1, 0, -90, -6);
        world.setSpawnLocation(spawn);
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates large biomes world";
    }
}
