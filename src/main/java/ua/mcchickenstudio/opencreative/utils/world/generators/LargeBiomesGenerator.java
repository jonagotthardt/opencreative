package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LargeBiomesGenerator extends WorldGenerator {

    public LargeBiomesGenerator() {
        super("large_biomes", new ItemStack(Material.MYCELIUM));
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator) {
        creator.type(WorldType.LARGE_BIOMES);
    }

    @Override
    public void afterCreation(@NotNull World world) {}

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates large biomes world";
    }
}
