package ua.mcchickenstudio.opencreative.indev.values.human;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.indev.values.LocationEventValue;

public class HumanLastDeathLocationValue extends LocationEventValue {

    public HumanLastDeathLocationValue() {
        super("last_death_location", new ItemStack(Material.SKELETON_SKULL), MenusCategory.PLAYER);
    }

    @Override
    public @Nullable Location getLocation(@NotNull ActionsHandler handler, @NotNull Action action) {
        return action.getEntity() instanceof HumanEntity humanEntity ? humanEntity.getLastDeathLocation() : null;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns human entity's item in main hand";
    }

}
