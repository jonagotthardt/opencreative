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

package ua.mcchickenstudio.opencreative.planets;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;

/**
 * <h1>PlanetPlayers</h1>
 * This class represents a planet players with
 * data and statuses, like building or development
 * permissions.
 */
public class PlanetPlayers {

    private final Planet planet;

    private final Set<PlanetPlayer> planetPlayers = new HashSet<>();

    private final Set<String> buildersTrusted = new HashSet<>();
    private final Set<String> buildersNotTrusted = new HashSet<>();

    private final Set<String> developersTrusted = new HashSet<>();
    private final Set<String> developersNotTrusted = new HashSet<>();
    private final Set<String> developersGuests = new HashSet<>();

    private final Set<String> bannedPlayers = new HashSet<>();

    public PlanetPlayers(Planet planet) {
        this.planet = planet;
    }

    public void registerPlayer(Player player) {
        planetPlayers.add(new PlanetPlayer(planet,player));
    }

    public void unregisterPlayer(Player player) {
        planetPlayers.removeIf(planetPlayer -> planetPlayer.getPlayer().equals(player));
        planet.getDevPlanet().getLastLocations().remove(player);
    }

    public PlanetPlayer getPlanetPlayer(Player player) {
        for (PlanetPlayer planetPlayer : planetPlayers) {
            if (planetPlayer.getPlayer().equals(player)) {
                return planetPlayer;
            }
        }
        return null;
    }

    public void clear() {
        buildersTrusted.clear();
        developersTrusted.clear();
        buildersNotTrusted.clear();
        developersNotTrusted.clear();
        developersGuests.clear();
        bannedPlayers.clear();
    }

    public void loadPlayers() {
        clear();
        FileConfiguration config = getPlanetConfig(planet);

        buildersTrusted.addAll(config.getStringList("players.builders.trusted"));
        developersTrusted.addAll(config.getStringList("players.developers.trusted"));

        buildersNotTrusted.addAll(config.getStringList("players.builders.not-trusted"));
        developersNotTrusted.addAll(config.getStringList("players.developers.not-trusted"));

        developersGuests.addAll(config.getStringList("players.developers.guests"));
        bannedPlayers.addAll(config.getStringList("players.black-list"));
    }

    public Set<String> getAllBuilders() {
        Set<String> builders = new HashSet<>(getBuildersTrusted());
        builders.addAll(getBuildersNotTrusted());
        return builders;
    }

    public Set<String> getAllDevelopers() {
        Set<String> developers = new HashSet<>(getDevelopersTrusted());
        developers.addAll(getDevelopersNotTrusted());
        developers.addAll(getDevelopersGuests());
        return developers;
    }

    public boolean isTrustedDeveloper(Player player) {
        if (planet.isOwner(player)) {
            return true;
        }
        if (player.hasPermission("opencreative.world.dev.others")) {
            return true;
        }
        for (String nickname : getDevelopersTrusted()) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isNotTrustedDeveloper(Player player) {
        for (String nickname : getDevelopersNotTrusted()) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isNotTrustedBuilder(Player player) {
        for (String nickname : getBuildersNotTrusted()) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isTrustedBuilder(Player player) {
        if (planet.isOwner(player)) {
            return true;
        }
        if (player.hasPermission("opencreative.world.build.others")) {
            return true;
        }
        for (String nickname : getBuildersTrusted()) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isDeveloperGuest(Player player) {
        for (String nickname : getDevelopersGuests()) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean canDevelop(Player player) {
        if (planet.isOwner(player)) {
            return true;
        }
        if (player.hasPermission("opencreative.world.dev.others")) {
            return true;
        }
        for (String nickname : getDevelopersTrusted()) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        Player owner = Bukkit.getPlayer(planet.getOwner());
        if (owner == null) {
            return false;
        }
        if (!planet.equals(PlanetManager.getInstance().getPlanetByPlayer(owner))) {
            return false;
        }
        for (String nickname : getDevelopersNotTrusted()) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean canBuild(Player player) {
        if (planet.isOwner(player)) {
            return true;
        }
        if (player.hasPermission("opencreative.world.build.others")) {
            return true;
        }
        for (String nickname : getBuildersTrusted()) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        Player owner = Bukkit.getPlayer(planet.getOwner());
        if (owner == null) {
            return false;
        }
        if (!planet.equals(PlanetManager.getInstance().getPlanetByPlayer(owner))) {
            return false;
        }
        for (String nickname : getBuildersNotTrusted()) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public void removeBuilder(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Planet playerPlanet = PlanetManager.getInstance().getPlanetByPlayer(player);
            if (planet.equals(playerPlanet)) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }
        }
        if (!planet.isLoaded()) loadPlayers();
        buildersNotTrusted.removeIf(builder -> builder.equalsIgnoreCase(nickname));
        buildersTrusted.removeIf(builder -> builder.equalsIgnoreCase(nickname));
        setPlanetConfigParameter(planet,"players.builders.not-trusted",buildersNotTrusted);
        setPlanetConfigParameter(planet,"players.builders.trusted",buildersTrusted);
    }

    public void removeDeveloper(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Planet playerPlanet = PlanetManager.getInstance().getPlanetByPlayer(player);
            if (planet.equals(playerPlanet)) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.setGameMode(GameMode.ADVENTURE);
                }
                if (isEntityInDevPlanet(player)) {
                    clearPlayer(player);
                    player.teleport(planet.getTerritory().getWorld().getSpawnLocation());
                }
            }
        }
        if (!planet.isLoaded()) loadPlayers();
        developersNotTrusted.removeIf(developer -> developer.equalsIgnoreCase(nickname));
        developersTrusted.removeIf(developer -> developer.equalsIgnoreCase(nickname));
        setPlanetConfigParameter(planet,"players.developers.not-trusted",developersNotTrusted);
        setPlanetConfigParameter(planet,"players.developers.trusted",developersTrusted);
    }

    public void addDeveloperGuest(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Planet playerPlanet = PlanetManager.getInstance().getPlanetByPlayer(player);
            if (planet.equals(playerPlanet)) {
                player.sendMessage(getLocaleMessage("world.players.developers.player-guest").replace("%player%",player.getName()));
                Sounds.WORLD_NOW_DEVELOPER_GUEST.play(player);
            }
        }
        if (!planet.isLoaded()) loadPlayers();
        developersGuests.add(nickname);
        developersNotTrusted.removeIf(developer -> developer.equalsIgnoreCase(nickname));
        developersTrusted.removeIf(developer -> developer.equalsIgnoreCase(nickname));
        setPlanetConfigParameter(planet,"players.developers.guests",developersGuests);
        setPlanetConfigParameter(planet,"players.developers.not-trusted",developersNotTrusted);
        setPlanetConfigParameter(planet,"players.developers.trusted",developersTrusted);    }

    public void addDeveloper(String nickname, boolean trusted) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Planet playerPlanet = PlanetManager.getInstance().getPlanetByPlayer(player);
            if (planet.equals(playerPlanet)) {
                if (!trusted) {
                    player.sendMessage(getLocaleMessage("world.players.developers.player").replace("%player%",player.getName()));
                    Sounds.WORLD_NOW_DEVELOPER.play(player);
                    if (PlanetManager.getInstance().getDevPlanet(player) != null) {
                        player.setGameMode(GameMode.CREATIVE);
                    }
                }
            }
        }
        if (!planet.isLoaded()) loadPlayers();
        if (trusted) {
            developersNotTrusted.removeIf(developer -> developer.equalsIgnoreCase(nickname));
            developersTrusted.add(nickname);
        } else {
            developersTrusted.removeIf(developer -> developer.equalsIgnoreCase(nickname));
            developersNotTrusted.add(nickname);
        }
        developersGuests.removeIf(developer -> developer.equalsIgnoreCase(nickname));
        setPlanetConfigParameter(planet,"players.developers.guests",developersGuests);
        setPlanetConfigParameter(planet,"players.developers.not-trusted",developersNotTrusted);
        setPlanetConfigParameter(planet,"players.developers.trusted",developersTrusted);
    }


    public void addBuilder(String nickname, boolean trusted) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Planet playerPlanet = PlanetManager.getInstance().getPlanetByPlayer(player);
            if (planet.equals(playerPlanet)) {
                if (!trusted) {
                    player.sendMessage(getLocaleMessage("world.players.builders.player").replace("%player%",player.getName()));
                    Sounds.WORLD_NOW_BUILDER.play(player);
                    if (PlanetManager.getInstance().getDevPlanet(player) == null) {
                        player.setGameMode(GameMode.CREATIVE);
                    }
                }
            }
        }
        if (!planet.isLoaded()) loadPlayers();
        if (trusted) {
            buildersNotTrusted.removeIf(builder -> builder.equalsIgnoreCase(nickname));
            buildersTrusted.add(nickname);
        } else {
            buildersTrusted.removeIf(builder -> builder.equalsIgnoreCase(nickname));
            buildersNotTrusted.add(nickname);
        }
        setPlanetConfigParameter(planet,"players.builders.not-trusted",buildersNotTrusted);
        setPlanetConfigParameter(planet,"players.builders.trusted",buildersTrusted);
        if (!planet.isLoaded()) clear();
    }

    public void unbanPlayer(String nickname) {
        if (!planet.isLoaded()) loadPlayers();
        this.bannedPlayers.removeIf(ban -> ban.equalsIgnoreCase(nickname));
        setPlanetConfigParameter(planet,"players.blacklist",bannedPlayers);
        if (!planet.isLoaded()) clear();
    }

    public void banPlayer(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Planet playerPlanet = PlanetManager.getInstance().getPlanetByPlayer(player);
            if (planet.equals(playerPlanet)) {
                teleportToLobby(player);
                player.sendMessage(getLocaleMessage("world.players.black-list.player").replace("%player%",player.getName()));
                Sounds.WORLD_BANNED.play(player);
                bannedPlayers.add(player.getName());
            }
        }
        if (!planet.isLoaded()) loadPlayers();
        setPlanetConfigParameter(planet,"players.blacklist",bannedPlayers);
        if (!planet.isLoaded()) clear();
    }

    public void kickPlayer(Player player) {
        Planet playerPlanet = PlanetManager.getInstance().getPlanetByPlayer(player);
        if (planet.equals(playerPlanet)) {
            teleportToLobby(player);
            player.sendMessage(getLocaleMessage("world.players.kick.player").replace("%player%",player.getName()));
            Sounds.WORLD_KICKED.play(player);
        }
    }

    public Set<String> getAllPlayersFromConfig() {
        Set<String> allPlayers = new HashSet<>();
        planet.getPlayers().forEach(player -> allPlayers.add(player.getName()));
        allPlayers.addAll(getBuildersTrusted());
        allPlayers.addAll(getBuildersNotTrusted());
        allPlayers.addAll(getDevelopersTrusted());
        allPlayers.addAll(getDevelopersNotTrusted());
        allPlayers.addAll(getDevelopersGuests());
        allPlayers.addAll(getBannedPlayers());
        allPlayers.remove(planet.getOwner());
        return allPlayers;
    }

    public Set<String> getBuildersTrusted() {
        if (!planet.isLoaded()) {
            return new HashSet<>(getPlanetConfig(planet).getStringList("players.builders.trusted"));
        }
        return new HashSet<>(buildersTrusted);
    }

    public Set<String> getBuildersNotTrusted() {
        if (!planet.isLoaded()) {
            return new HashSet<>(getPlanetConfig(planet).getStringList("players.builders.not-trusted"));
        }
        return new HashSet<>(buildersNotTrusted);
    }

    public Set<String> getDevelopersGuests() {
        if (!planet.isLoaded()) {
            return new HashSet<>(getPlanetConfig(planet).getStringList("players.developers.guests"));
        }
        return new HashSet<>(developersGuests);
    }

    public Set<String> getDevelopersTrusted() {
        if (!planet.isLoaded()) {
            return new HashSet<>(getPlanetConfig(planet).getStringList("players.developers.trusted"));
        }
        return new HashSet<>(developersTrusted);
    }

    public Set<String> getDevelopersNotTrusted() {
        if (!planet.isLoaded()) {
            return new HashSet<>(getPlanetConfig(planet).getStringList("players.developers.not-trusted"));
        }
        return new HashSet<>(developersNotTrusted);
    }

    public String getBuilders() {
        return String.join(", ", planet.getWorldPlayers().getAllBuilders());
    }

    public String getDevelopers() {
        return String.join(", ", planet.getWorldPlayers().getAllDevelopers());
    }

    public boolean isBanned(String nickname) {
        for (String banned : getBannedPlayers()) {
            if (banned.equalsIgnoreCase(nickname)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getBannedPlayers() {
        if (!planet.isLoaded()) {
            return new HashSet<>(getPlanetConfig(planet).getStringList("players.blacklist"));
        }
        return bannedPlayers;
    }

    public void purgeData() {
        List<String> empty = new ArrayList<>();
        clear();
        setPlanetConfigParameter(planet,"players.unique",empty);
        setPlanetConfigParameter(planet,"players.liked",empty);
        setPlanetConfigParameter(planet,"players.disliked",empty);
        setPlanetConfigParameter(planet,"players.blacklist",empty);
        setPlanetConfigParameter(planet,"players.whitelist",empty);
        setPlanetConfigParameter(planet,"players.developers.trusted",empty);
        setPlanetConfigParameter(planet,"players.developers.not-trusted",empty);
        setPlanetConfigParameter(planet,"players.developers.guests",empty);
        setPlanetConfigParameter(planet,"players.builders.trusted",empty);
        setPlanetConfigParameter(planet,"players.builders.not-trusted",empty);
    }
}
