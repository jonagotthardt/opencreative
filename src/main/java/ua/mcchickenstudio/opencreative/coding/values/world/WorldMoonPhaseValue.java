package ua.mcchickenstudio.opencreative.coding.values.world;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.TextEventValue;

public final class WorldMoonPhaseValue extends TextEventValue {

    public WorldMoonPhaseValue() {
        super("world_moon_phase", new ItemStack(Material.SEA_LANTERN), MenusCategory.WORLD);
    }

    @Override
    public @Nullable String getText(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity) {
        return action.getExecutor().getPlanet().getWorld().getMoonPhase().name().toLowerCase();
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns world's moon phase";
    }

}
