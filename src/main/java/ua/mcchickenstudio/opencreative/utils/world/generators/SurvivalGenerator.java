package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class SurvivalGenerator extends WorldGenerator implements EnvironmentCapable, StructuresCapable {

    public SurvivalGenerator() {
        super("survival", new ItemStack(Material.OAK_SAPLING));
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator) {
        creator.type(WorldType.NORMAL);
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
        return "Creates normal world";
    }

}
