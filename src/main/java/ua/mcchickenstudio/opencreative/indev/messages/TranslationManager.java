package ua.mcchickenstudio.opencreative.indev.messages;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.managers.Manager;

public interface TranslationManager extends Manager {

    @NotNull Component getMessage(@NotNull String path);

    @NotNull Component getMessage(@NotNull String path, @NotNull String language);

    @NotNull Component getMessage(@NotNull String path, @NotNull CommandSender sender);

    @NotNull Component getMessage(@NotNull String path, @NotNull OfflinePlayer player);

}

