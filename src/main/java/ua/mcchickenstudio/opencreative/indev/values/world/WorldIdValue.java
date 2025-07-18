package ua.mcchickenstudio.opencreative.indev.values.world;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.indev.values.NumberEventValue;
import ua.mcchickenstudio.opencreative.indev.values.TextEventValue;

public class WorldIdValue extends NumberEventValue {

    public WorldIdValue() {
        super("planet_id", new ItemStack(Material.SLIME_BALL), MenusCategory.WORLD);
    }

    @Override
    public @Nullable Number getNumber(@NotNull ActionsHandler handler, @NotNull Action action) {
        return action.getExecutor().getPlanet().getId();
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns world's ID";
    }

}
