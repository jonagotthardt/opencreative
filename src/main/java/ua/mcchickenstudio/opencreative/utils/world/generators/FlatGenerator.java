package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class FlatGenerator extends WorldGenerator implements EnvironmentCapable, StructuresCapable {

    public FlatGenerator() {
        super("flat", new ItemStack(Material.MOSS_BLOCK));
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator) {
        creator.type(WorldType.FLAT);
    }

    @Override
    public void afterCreation(@NotNull World world) {}

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates flat world";
    }

}
