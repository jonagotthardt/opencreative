package ua.mcchickenstudio.opencreative.coding.values.world;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.NumberEventValue;
import ua.mcchickenstudio.opencreative.planets.Planet;

public class WorldEntitiesAmountValue extends NumberEventValue {

    public WorldEntitiesAmountValue() {
        super("planet_entities_amount", new ItemStack(Material.CHICKEN_SPAWN_EGG), MenusCategory.WORLD);
    }

    @Override
    public @Nullable Number getNumber(@NotNull ActionsHandler handler, @NotNull Action action) {
        Planet planet = action.getExecutor().getPlanet();
        return planet.getTerritory().getWorld().getEntityCount() + ((planet.getDevPlanet() != null && planet.getDevPlanet().getWorld() != null) ? planet.getDevPlanet().getWorld().getEntityCount() : 0);
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns world's entities amount";
    }

}
