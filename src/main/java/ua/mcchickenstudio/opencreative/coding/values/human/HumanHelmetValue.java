package ua.mcchickenstudio.opencreative.coding.values.human;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.ItemEventValue;

public final class HumanHelmetValue extends ItemEventValue {

    public HumanHelmetValue() {
        super("helmet", new ItemStack(Material.NETHERITE_HELMET), MenusCategory.PLAYER);
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity) {
        return entity instanceof HumanEntity humanEntity ? humanEntity.getInventory().getHelmet() : null;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns human entity's helmet item";
    }

}
