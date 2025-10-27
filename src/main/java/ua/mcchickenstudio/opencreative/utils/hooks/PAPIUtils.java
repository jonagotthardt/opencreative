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
import ua.mcchickenstudio.opencreative.settings.groups.LimitType;

import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;

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
        if (identifier.startsWith("planet:")) {
            // For other planets: %opencreative_planet:123_online% (planet:123_online)
            String identifierNoPlanet = identifier.replace("planet:", ""); // 123_online

            int underscoreIndex = identifierNoPlanet.indexOf('_');
            if (underscoreIndex == -1) return null;
            String planetId = identifierNoPlanet.substring(0, underscoreIndex);

            Planet planet = OpenCreative.getPlanetsManager().getPlanetByAnyID(planetId);
            if (planet == null) return null;
            identifier = identifierNoPlanet.substring(underscoreIndex + 1); // online
            return parsePlanet(planet, identifier);
        }
        // For player's current planet: %opencreative_planet_online% (planet_online)
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        return parsePlayer(player, identifier, planet);
    }

    private String parsePlayer(Player player, @NotNull String identifier, Planet currentPlanet) {
        if (currentPlanet == null) {
            if (identifier.equals("is_in_planet")) {
                return "false";
            }
            return null;
        }
        switch (identifier) {
            case "is_in_planet" -> {
                return "true";
            }
            case "is_in_build_planet" -> {
                return String.valueOf(!isDevPlanet(player.getWorld()));
            }
            case "is_in_dev_planet" -> {
                return String.valueOf(isDevPlanet(player.getWorld()));
            }
            case "can_develop" -> {
                return String.valueOf(currentPlanet.getWorldPlayers().canDevelop(player));
            }
            case "can_build" -> {
                return String.valueOf(currentPlanet.getWorldPlayers().canBuild(player));
            }
            case "is_whitelisted" -> {
                return String.valueOf(currentPlanet.getWorldPlayers().isWhitelisted(player.getName()));
            }
            case "is_banned" -> {
                return String.valueOf(currentPlanet.getWorldPlayers().isBanned(player.getName()));
            }
            case "is_owner" -> {
                return String.valueOf(currentPlanet.isOwner(player));
            }
        }
        return parsePlanet(currentPlanet, identifier);
    }
    
    private String parsePlanet(@NotNull Planet planet, @NotNull String identifier) {
        switch (identifier) {
            case "id" -> {
                return String.valueOf(planet.getId());
            }
            case "custom_id" -> {
                return planet.getInformation().getCustomID();
            }
            case "online" -> {
                return String.valueOf(planet.getOnline());
            }
            case "uniques" -> {
                return String.valueOf(planet.getInformation().getUniques());
            }
            case "reputation" -> {
                return String.valueOf(planet.getInformation().getReputation());
            }
            case "name" -> {
                return planet.getInformation().getDisplayName();
            }
            case "description" -> {
                return planet.getInformation().getDescription();
            }
            case "icon_material" -> {
                return planet.getInformation().getIcon().getType().name();
            }
            case "owner" -> {
                return planet.getOwner();
            }
            case "category" -> {
                return String.valueOf(planet.getInformation().getCategory());
            }
            case "sharing" -> {
                return String.valueOf(planet.getSharing());
            }
            case "mode" -> {
                return String.valueOf(planet.getMode());
            }
            case "is_loaded" -> {
                return String.valueOf(planet.isLoaded());
            }
            case "variables_amount" -> {
                return String.valueOf(planet.getVariables().getTotalVariablesAmount());
            }
            case "redstone_operations_amount" -> {
                return String.valueOf(planet.getLimits().getLastRedstoneOperationsAmount());
            }
            case "modified_blocks_amount" -> {
                return String.valueOf(planet.getLimits().getLastModifiedBlocksAmount());
            }
            case "elements_changes_amount" -> {
                return String.valueOf(planet.getLimits().getLastVariableElementsChangesAmount());
            }
            case "whitelisted_players" -> {
                return String.join(", ", planet.getWorldPlayers().getWhitelistedPlayers());
            }
            case "blacklisted_players", "banned_players" -> {
                return String.join(", ", planet.getWorldPlayers().getBannedPlayers());
            }
            case "builders" -> {
                return String.join(", ", planet.getWorldPlayers().getAllBuilders());
            }
            case "developers" -> {
                return String.join(", ", planet.getWorldPlayers().getAllDevelopers());
            }
            case "trusted_developers" -> {
                return String.join(", ", planet.getWorldPlayers().getDevelopersTrusted());
            }
            case "not_trusted_developers" -> {
                return String.join(", ", planet.getWorldPlayers().getDevelopersNotTrusted());
            }
            case "guest_developers" -> {
                return String.join(", ", planet.getWorldPlayers().getDevelopersGuests());
            }
            case "trusted_builders" -> {
                return String.join(", ", planet.getWorldPlayers().getBuildersTrusted());
            }
            case "not_trusted_builders" -> {
                return String.join(", ", planet.getWorldPlayers().getBuildersNotTrusted());
            }
            case "entities_amount" -> {
                int entities = planet.getTerritory().getWorld().getEntityCount()
                        + (planet.getDevPlanet().isLoaded() ? planet.getDevPlanet().getWorld().getEntityCount() : 0);
                return String.valueOf(entities);
            }
            case "executors_amount" -> {
                return String.valueOf(planet.getTerritory().getScript().getExecutors().getExecutorsList().size());
            }
            case "is_dev_planet_loaded" -> {
                return String.valueOf(planet.getDevPlanet() != null && planet.getDevPlanet().isLoaded());
            }
        }
        if (identifier.startsWith("limit_")) {
            String limitType = identifier.replace("limit_", "");
            if (limitType.isEmpty()) return null;
            try {
                LimitType type = LimitType.valueOf(limitType.toUpperCase().replace("-", "_"));
                return String.valueOf(planet.getGroup().getLimit(type));
            } catch (Exception ignored) {
                return null;
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
        return "5.8.0";
    }

    @Override
    public boolean canRegister() {
        return (OpenCreative.getPlugin() != null);
    }
}
