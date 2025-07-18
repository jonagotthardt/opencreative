package ua.mcchickenstudio.opencreative.coding.values.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.NumberEventValue;

public class PlayerExperienceLevelValue extends NumberEventValue {

    public PlayerExperienceLevelValue() {
        super("experience_level", new ItemStack(Material.SUGAR_CANE), MenusCategory.PLAYER);
    }

    @Override
    public @Nullable Number getNumber(@NotNull ActionsHandler handler, @NotNull Action action) {
        return action.getEntity() instanceof Player player ? player.getLevel() : null;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns player's experience level";
    }

}
