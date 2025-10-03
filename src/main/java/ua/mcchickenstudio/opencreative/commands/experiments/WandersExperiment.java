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
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.indev.Wander;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class WandersExperiment extends Experiment {

    @Override
    public @NotNull String getId() {
        return "wanders";
    }

    @Override
    public @NotNull String getName() {
        return "Wanders Subsystem";
    }

    @Override
    public @NotNull String getDescription() {
        return "Adds favorite, unfavorite worlds, saving players last world";
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return;
        }
        if (args.length == 0) {
            player.sendMessage(getLocaleMessage("too-few-args"));
            return;
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) {
            player.sendMessage(getLocaleMessage("only-in-world"));
            return;
        }
        switch (args[0].toLowerCase()) {
            case "favorite" -> {
                Wander wander = OpenCreative.getWander(player);
                if (wander.addFavoriteWorld(planet.getId())) {
                    wander.getPlayer().sendMessage(":) Favorited " + planet.getId());
                } else {
                    wander.getPlayer().sendMessage("Already in favorites!");
                }
                List<Integer> favoriteWorlds = wander.getFavoriteWorlds();
                if (favoriteWorlds.isEmpty()) {
                    wander.getPlayer().sendMessage("No favorite worlds");
                }
                wander.getPlayer().sendMessage("Favorite worlds (" + favoriteWorlds.size() + "): ");
                for (int world : favoriteWorlds) {
                    wander.getPlayer().sendMessage(" - " + world);
                }
            }
            case "unfavorite" -> {
                Wander wander = OpenCreative.getWander(player);
                if (wander.removeFavoriteWorld(planet.getId())) {
                    wander.getPlayer().sendMessage(":) Unfavorited " + planet.getId());
                } else {
                    wander.getPlayer().sendMessage("Not in favorites!");
                }
                List<Integer> favoriteWorlds = wander.getFavoriteWorlds();
                if (favoriteWorlds.isEmpty()) {
                    wander.getPlayer().sendMessage("No favorite worlds");
                }
                wander.getPlayer().sendMessage("Favorite worlds (" + favoriteWorlds.size() + "): ");
                for (int world : favoriteWorlds) {
                    wander.getPlayer().sendMessage(" - " + world);
                }
            }
        }
    }

    @Override
    public @Nullable List<String> tabCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            return List.of("favorite", "unfavorite");
        }
        return null;
    }

}
