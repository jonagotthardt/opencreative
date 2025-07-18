package ua.mcchickenstudio.opencreative.indev.values.human;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.indev.values.ItemEventValue;

public class HumanItemInMainHandValue extends ItemEventValue {

    public HumanItemInMainHandValue() {
        super("item_in_main_hand", new ItemStack(Material.WOODEN_SWORD), MenusCategory.PLAYER);
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull ActionsHandler handler, @NotNull Action action) {
        return action.getEntity() instanceof HumanEntity humanEntity ? humanEntity.getInventory().getItemInMainHand() : null;
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
