package ua.mcchickenstudio.opencreative.indev.values.world;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.indev.values.NumberEventValue;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UnixTimeHoursValue extends NumberEventValue {

    public UnixTimeHoursValue() {
        super("unix_time_hours", new ItemStack(Material.CHEST_MINECART), MenusCategory.WORLD);
    }

    @Override
    public @Nullable Number getNumber(@NotNull ActionsHandler handler, @NotNull Action action) {
        SimpleDateFormat hoursFormat = new SimpleDateFormat("HH");
        Date date = new Date(System.currentTimeMillis());
        return Integer.parseInt(hoursFormat.format(date));
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns server time in hours";
    }

}
