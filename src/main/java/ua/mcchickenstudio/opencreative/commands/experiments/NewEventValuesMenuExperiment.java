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
import ua.mcchickenstudio.opencreative.coding.menus.variables.EventValuesCategorySelectionMenu;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class NewEventValuesMenuExperiment extends Experiment {

    @Override
    public @NotNull String getId() {
        return "new_event_values_menu";
    }

    @Override
    public @NotNull String getName() {
        return "New Event Values Menu";
    }

    @Override
    public @NotNull String getDescription() {
        return "Opens new event values menu";
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (sender instanceof Player player) {
            new EventValuesCategorySelectionMenu(player, player.getInventory().getItemInMainHand())
                    .open(player);
        } else {
            sender.sendMessage(getLocaleMessage("only-players"));
        }
    }

    @Override
    public @NotNull List<String> tabCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        return List.of();
    }

}
