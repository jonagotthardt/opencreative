package ua.mcchickenstudio.opencreative.coding.values.world;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.ItemEventValue;

public class WorldIconValue extends ItemEventValue {

    public WorldIconValue() {
        super("planet_icon", new ItemStack(Material.DIAMOND), MenusCategory.WORLD);
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull ActionsHandler handler, @NotNull Action action) {
        return new ItemStack(action.getExecutor().getPlanet().getInformation().getIcon().getType(),1);
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns world's item icon";
    }

}
