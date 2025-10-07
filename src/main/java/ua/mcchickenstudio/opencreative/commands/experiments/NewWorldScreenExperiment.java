/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2025, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.mcchickenstudio.opencreative.commands.experiments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class NewWorldScreenExperiment extends Experiment {

    private static ScreenType type = ScreenType.NORMAL;

    @Override
    public @NotNull String getId() {
        return "new_world_screen";
    }

    @Override
    public @NotNull String getName() {
        return "New World Loading Screen";
    }

    @Override
    public @NotNull String getDescription() {
        return "Displays screen for player when world loads";
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // normal, nether, the_end, percents
        if (args.length == 0) {
            sender.sendMessage(getLocaleMessage("too-few-args"));
            return;
        }
        String typeString = args[0].toLowerCase();
        switch (typeString) {
            case "normal", "nether", "the_end", "percents" -> {
                type = ScreenType.valueOf(typeString.toUpperCase());
                System.out.println(":) Changed to: " + typeString);
            }
            default -> sender.sendMessage("Unknown screen. Available: normal, nether, the_end, percents");
        }
    }

    @Override
    public void onDisable() {
        type = ScreenType.NORMAL;
    }

    @Override
    public @Nullable List<String> tabCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            return List.of("normal", "nether", "the_end", "percents");
        }
        return null;
    }

    public static ScreenType getType() {
        return type;
    }

    public enum ScreenType {
        NORMAL,
        NETHER,
        THE_END,
        PERCENTS
    }

}
