package ua.mcchickenstudio.opencreative.coding.values.world;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.TextEventValue;

public final class WorldNameValue extends TextEventValue {

    public WorldNameValue() {
        super("planet_name", new ItemStack(Material.NAME_TAG), MenusCategory.WORLD);
    }

    @Override
    public @Nullable String getText(@NotNull ActionsHandler handler, @NotNull Action action) {
        return action.getExecutor().getPlanet().getInformation().getDisplayName();
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns world's display name";
    }

}
