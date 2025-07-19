package ua.mcchickenstudio.opencreative.coding.values.player;

import org.bukkit.entity.Entity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.TextEventValue;

public final class LocaleDisplayLanguageValue extends TextEventValue {

    public LocaleDisplayLanguageValue() {
        super("locale_display_language", new ItemStack(Material.BLUE_STAINED_GLASS), MenusCategory.PLAYER);
    }

    @Override
    public @Nullable String getText(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity) {
        return entity instanceof Player player ? player.locale().getDisplayLanguage() : null;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns player's locale display language";
    }

}
