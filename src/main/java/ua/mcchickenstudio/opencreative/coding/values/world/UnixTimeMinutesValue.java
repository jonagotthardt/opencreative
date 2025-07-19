package ua.mcchickenstudio.opencreative.coding.values.world;

import org.bukkit.entity.Entity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.NumberEventValue;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class UnixTimeMinutesValue extends NumberEventValue {

    public UnixTimeMinutesValue() {
        super("unix_time_minutes", new ItemStack(Material.HOPPER_MINECART), MenusCategory.WORLD);
    }

    @Override
    public @Nullable Number getNumber(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity) {
        SimpleDateFormat minutesFormat = new SimpleDateFormat("mm");
        Date date = new Date(System.currentTimeMillis());
        return Integer.parseInt(minutesFormat.format(date));
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns server time in minutes";
    }

}
