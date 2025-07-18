package ua.mcchickenstudio.opencreative.indev.values.world;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.indev.values.TextEventValue;

public class WorldDescriptionValue extends TextEventValue {

    public WorldDescriptionValue() {
        super("planet_description", new ItemStack(Material.BOOK), MenusCategory.WORLD);
    }

    @Override
    public @Nullable String getText(@NotNull ActionsHandler handler, @NotNull Action action) {
        return action.getExecutor().getPlanet().getInformation().getDescription();
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns world's description";
    }

}
