package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class EmptyGenerator extends WorldGenerator {

    public EmptyGenerator() {
        super("empty", new ItemStack(Material.GLASS));
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator) {
        creator.type(WorldType.FLAT);
        creator.generator(this);
    }

    @Override
    public void afterCreation(@NotNull World world) {
        world.setSpawnLocation(0, 5, 0);
        for (int x = 5; x >= -5; x--) {
            for (int z = 5; z >= -5; z--) {
                world.getBlockAt(x, 4, z).setType(Material.BEDROCK);
            }
        }
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates empty world";
    }

}
