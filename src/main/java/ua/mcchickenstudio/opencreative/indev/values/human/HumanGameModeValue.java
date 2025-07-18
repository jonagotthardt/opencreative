package ua.mcchickenstudio.opencreative.indev.values.human;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.indev.values.TextEventValue;

public class HumanGameModeValue extends TextEventValue {

    public HumanGameModeValue() {
        super("game_mode", new ItemStack(Material.CRAFTING_TABLE), MenusCategory.PLAYER);
    }

    @Override
    public @Nullable String getText(@NotNull ActionsHandler handler, @NotNull Action action) {
        return action.getEntity() instanceof HumanEntity humanEntity ? humanEntity.getGameMode().name().toLowerCase() : null;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns human entity's current game mode";
    }

}
