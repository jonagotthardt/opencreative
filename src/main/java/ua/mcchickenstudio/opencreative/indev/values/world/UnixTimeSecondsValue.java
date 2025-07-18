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

public class UnixTimeSecondsValue extends NumberEventValue {

    public UnixTimeSecondsValue() {
        super("unix_time_seconds", new ItemStack(Material.MINECART), MenusCategory.WORLD);
    }

    @Override
    public @Nullable Number getNumber(@NotNull ActionsHandler handler, @NotNull Action action) {
        SimpleDateFormat secondsFormat = new SimpleDateFormat("ss");
        Date date = new Date(System.currentTimeMillis());
        return Integer.parseInt(secondsFormat.format(date));
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns server time in seconds";
    }

}
