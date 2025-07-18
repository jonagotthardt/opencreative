package ua.mcchickenstudio.opencreative.indev.values.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.indev.values.LocationEventValue;

public class WorldSpawnValue extends LocationEventValue {

    public WorldSpawnValue() {
        super("world_spawn", new ItemStack(Material.SPAWNER), MenusCategory.WORLD);
    }

    @Override
    public @Nullable Location getLocation(@NotNull ActionsHandler handler, @NotNull Action action) {
        return action.getExecutor().getPlanet().getTerritory().getWorld().getSpawnLocation();
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns world's spawn location";
    }

}
