package ua.mcchickenstudio.opencreative.indev.values.player;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.indev.values.LocationEventValue;
import ua.mcchickenstudio.opencreative.indev.values.NumberEventValue;

public class PlayerCompassTargetValue extends LocationEventValue {

    public PlayerCompassTargetValue() {
        super("compass_target", new ItemStack(Material.COMPASS), MenusCategory.PLAYER);
    }

    @Override
    public @Nullable Location getLocation(@NotNull ActionsHandler handler, @NotNull Action action) {
        return action.getEntity() instanceof Player player ? player.getCompassTarget() : null;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns player's compass target location";
    }

}
