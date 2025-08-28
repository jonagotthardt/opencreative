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

package ua.mcchickenstudio.opencreative.utils.hooks;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PAPIUtils {

    public static String parsePlaceholdersAPI(OfflinePlayer offlinePlayer, String string) {
        return PlaceholderAPI.setPlaceholders(offlinePlayer,string);
    }

    public static void registerPlaceholder() {
        new Placeholder().register();
    }


}

class Placeholder extends PlaceholderExpansion {

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        switch (identifier) {
            case "planet_id" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet != null) return String.valueOf(planet.getId());
            }
            case "planet_custom_id" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet != null) return planet.getInformation().getCustomID();
            }
            case "planet_online" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet != null) return String.valueOf(planet.getOnline());
            }
            case "planet_uniques" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet != null) return String.valueOf(planet.getInformation().getUniques());
            }
            case "planet_reputation" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet != null) return String.valueOf(planet.getInformation().getReputation());
            }
            case "is_in_planet" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                return String.valueOf((planet != null));
            }
            case "planet_name" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet != null) return planet.getInformation().getDisplayName();
            }
            case "planet_description" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet != null) return planet.getInformation().getDescription();
            }
            case "planet_owner" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet != null) return planet.getOwner();
            }
            case "planet_category" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet != null) return String.valueOf(planet.getInformation().getCategory());
            }
            case "planet_sharing" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet != null) return String.valueOf(planet.getSharing());
            }
            case "planet_mode" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet != null) return String.valueOf(planet.getMode());
            }
            case "planet_is_dev_planet_loaded" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet != null) return String.valueOf(planet.getDevPlanet() != null && planet.getDevPlanet().isLoaded());
            }
        }
        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "opencreative";
    }

    @Override
    public @NotNull String getAuthor() {
        return "McChicken Studio";
    }

    @Override
    public @NotNull String getVersion() {
        return "5.7.1";
    }

    @Override
    public boolean canRegister() {
        return (OpenCreative.getPlugin() != null);
    }
}
