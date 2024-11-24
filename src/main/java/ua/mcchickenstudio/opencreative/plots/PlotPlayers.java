/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com
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

package ua.mcchickenstudio.opencreative.plots;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;

public class PlotPlayers {

    private final Plot plot;

    private final Set<WorldPlayer> worldPlayers = new HashSet<>();

    private final Set<String> buildersTrusted = new HashSet<>();
    private final Set<String> buildersNotTrusted = new HashSet<>();

    private final Set<String> developersTrusted = new HashSet<>();
    private final Set<String> developersNotTrusted = new HashSet<>();
    private final Set<String> developersGuests = new HashSet<>();

    private final Set<String> bannedPlayers = new HashSet<>();

    public PlotPlayers(Plot plot) {
        this.plot = plot;
        loadPlayers();
    }

    public void registerPlayer(Player player) {
        worldPlayers.add(new WorldPlayer(plot,player));
    }

    public void unregisterPlayer(Player player) {
        worldPlayers.removeIf(plotPlayer -> plotPlayer.getPlayer().equals(player));
        plot.getDevPlot().getLastLocations().remove(player);
    }

    public WorldPlayer getPlotPlayer(Player player) {
        for (WorldPlayer worldPlayer : worldPlayers) {
            if (worldPlayer.getPlayer().equals(player)) {
                return worldPlayer;
            }
        }
        return null;
    }

    private void loadPlayers() {
        FileConfiguration config = getPlotConfig(plot);
        buildersTrusted.addAll(config.getStringList("players.builders.trusted"));
        developersTrusted.addAll(config.getStringList("players.developers.trusted"));

        buildersNotTrusted.addAll(config.getStringList("players.builders.not-trusted"));
        developersNotTrusted.addAll(config.getStringList("players.developers.not-trusted"));

        developersGuests.addAll(config.getStringList("players.developers.guests"));
        bannedPlayers.addAll(config.getStringList("players.black-list"));
    }

    public Set<String> getAllBuilders() {
        Set<String> builders = new HashSet<>(buildersTrusted);
        builders.addAll(buildersNotTrusted);
        return builders;
    }

    public Set<String> getAllDevelopers() {
        Set<String> developers = new HashSet<>(developersTrusted);
        developers.addAll(developersNotTrusted);
        developers.addAll(developersGuests);
        return developers;
    }

    public boolean isTrustedDeveloper(Player player) {
        if (plot.isOwner(player)) {
            return true;
        }
        if (player.hasPermission("opencreative.world.dev.others")) {
            return true;
        }
        for (String nickname : developersTrusted) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isNotTrustedDeveloper(Player player) {
        for (String nickname : developersNotTrusted) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isNotTrustedBuilder(Player player) {
        for (String nickname : buildersNotTrusted) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isTrustedBuilder(Player player) {
        if (plot.isOwner(player)) {
            return true;
        }
        if (player.hasPermission("opencreative.world.build.others")) {
            return true;
        }
        for (String nickname : buildersTrusted) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isDeveloperGuest(Player player) {
        for (String nickname : developersGuests) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean canDevelop(Player player) {
        if (plot.isOwner(player)) {
            return true;
        }
        if (player.hasPermission("opencreative.world.dev.others")) {
            return true;
        }
        for (String nickname : developersTrusted) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        Player owner = Bukkit.getPlayer(plot.getOwner());
        if (owner == null) {
            return false;
        }
        if (!plot.equals(PlotManager.getInstance().getPlotByPlayer(owner))) {
            return false;
        }
        for (String nickname : developersNotTrusted) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean canBuild(Player player) {
        if (plot.isOwner(player)) {
            return true;
        }
        if (player.hasPermission("opencreative.world.build.others")) {
            return true;
        }
        for (String nickname : buildersTrusted) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        Player owner = Bukkit.getPlayer(plot.getOwner());
        if (owner == null) {
            return false;
        }
        if (!plot.equals(PlotManager.getInstance().getPlotByPlayer(owner))) {
            return false;
        }
        for (String nickname : buildersNotTrusted) {
            if (nickname.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public void removeBuilder(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot playerPlot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot.equals(playerPlot)) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }
        }
        buildersNotTrusted.removeIf(builder -> builder.equalsIgnoreCase(nickname));
        buildersTrusted.removeIf(builder -> builder.equalsIgnoreCase(nickname));
        setPlotConfigParameter(plot,"players.builders.not-trusted",buildersNotTrusted);
        setPlotConfigParameter(plot,"players.builders.trusted",buildersTrusted);
    }

    public void removeDeveloper(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot playerPlot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot.equals(playerPlot)) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.setGameMode(GameMode.ADVENTURE);
                }
                if (isEntityInDevPlot(player)) {
                    clearPlayer(player);
                    player.teleport(plot.getTerritory().getWorld().getSpawnLocation());
                }
            }
        }
        developersNotTrusted.removeIf(developer -> developer.equalsIgnoreCase(nickname));
        developersTrusted.removeIf(developer -> developer.equalsIgnoreCase(nickname));
        setPlotConfigParameter(plot,"players.developers.not-trusted",developersNotTrusted);
        setPlotConfigParameter(plot,"players.developers.trusted",developersTrusted);
    }

    public void addDeveloperGuest(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot playerPlot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot.equals(playerPlot)) {
                player.sendMessage(getLocaleMessage("world.players.developers.player-guest").replace("%player%",player.getName()));
                player.playSound(player.getLocation(),Sound.ENTITY_CAT_AMBIENT,100,1);
            }
        }
        developersGuests.add(nickname);
        developersNotTrusted.removeIf(developer -> developer.equalsIgnoreCase(nickname));
        developersTrusted.removeIf(developer -> developer.equalsIgnoreCase(nickname));
        setPlotConfigParameter(plot,"players.developers.guests",developersGuests);
        setPlotConfigParameter(plot,"players.developers.not-trusted",developersNotTrusted);
        setPlotConfigParameter(plot,"players.developers.trusted",developersTrusted);    }

    public void addDeveloper(String nickname, boolean trusted) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot playerPlot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot.equals(playerPlot)) {
                if (!trusted) {
                    player.sendMessage(getLocaleMessage("world.players.developers.player").replace("%player%",player.getName()));
                    player.playSound(player.getLocation(),Sound.ENTITY_CAT_AMBIENT,100,1);
                    if (PlotManager.getInstance().getDevPlot(player) != null) {
                        player.setGameMode(GameMode.CREATIVE);
                    }
                }
            }
        }
        if (trusted) {
            developersNotTrusted.removeIf(developer -> developer.equalsIgnoreCase(nickname));
            developersTrusted.add(nickname);
        } else {
            developersTrusted.removeIf(developer -> developer.equalsIgnoreCase(nickname));
            developersNotTrusted.add(nickname);
        }
        developersGuests.removeIf(developer -> developer.equalsIgnoreCase(nickname));
        setPlotConfigParameter(plot,"players.developers.guests",developersGuests);
        setPlotConfigParameter(plot,"players.developers.not-trusted",developersNotTrusted);
        setPlotConfigParameter(plot,"players.developers.trusted",developersTrusted);
    }


    public void addBuilder(String nickname, boolean trusted) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot playerPlot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot.equals(playerPlot)) {
                if (!trusted) {
                    player.sendMessage(getLocaleMessage("world.players.builders.player").replace("%player%",player.getName()));
                    player.playSound(player.getLocation(),Sound.ENTITY_CAT_AMBIENT,100,1);
                    if (PlotManager.getInstance().getDevPlot(player) == null) {
                        player.setGameMode(GameMode.CREATIVE);
                    }
                }
            }
        }
        if (trusted) {
            buildersNotTrusted.removeIf(builder -> builder.equalsIgnoreCase(nickname));
            buildersTrusted.add(nickname);
        } else {
            buildersTrusted.removeIf(builder -> builder.equalsIgnoreCase(nickname));
            buildersNotTrusted.add(nickname);
        }
        setPlotConfigParameter(plot,"players.builders.not-trusted",buildersNotTrusted);
        setPlotConfigParameter(plot,"players.builders.trusted",buildersTrusted);
    }

    public void unbanPlayer(String nickname) {
        this.bannedPlayers.removeIf(ban -> ban.equalsIgnoreCase(nickname));
        setPlotConfigParameter(plot,"players.blacklist",bannedPlayers);
    }

    public void banPlayer(String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player != null) {
            Plot playerPlot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot.equals(playerPlot)) {
                teleportToLobby(player);
                player.sendMessage(getLocaleMessage("world.players.black-list.player").replace("%player%",player.getName()));
                player.playSound(player.getLocation(), Sound.ENTITY_CAT_HURT,100,1);
                bannedPlayers.add(player.getName());
            }
        }
        setPlotConfigParameter(plot,"players.blacklist",bannedPlayers);
    }

    public void kickPlayer(Player player) {
        Plot playerPlot = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot.equals(playerPlot)) {
            teleportToLobby(player);
            player.sendMessage(getLocaleMessage("world.players.kick.player").replace("%player%",player.getName()));
            player.playSound(player.getLocation(),Sound.ENTITY_CAT_HURT,100,1);
        }
    }

    public Set<String> getAllPlayersFromConfig() {
        Set<String> allPlayers = new HashSet<>();
        plot.getPlayers().forEach(player -> allPlayers.add(player.getName()));
        allPlayers.addAll(buildersTrusted);
        allPlayers.addAll(buildersNotTrusted);
        allPlayers.addAll(developersTrusted);
        allPlayers.addAll(developersNotTrusted);
        allPlayers.addAll(developersGuests);
        allPlayers.addAll(bannedPlayers);
        allPlayers.remove(plot.getOwner());
        return allPlayers;
    }

    public Set<String> getBuildersTrusted() {
        return new HashSet<>(buildersTrusted);
    }

    public Set<String> getBuildersNotTrusted() {
        return new HashSet<>(buildersNotTrusted);
    }

    public Set<String> getDevelopersGuests() {
        return new HashSet<>(developersGuests);
    }

    public Set<String> getDevelopersTrusted() {
        return new HashSet<>(developersTrusted);
    }

    public Set<String> getDevelopersNotTrusted() {
        return new HashSet<>(developersNotTrusted);
    }

    public String getBuilders() {
        return String.join(", ", plot.getWorldPlayers().getAllBuilders());
    }

    public String getDevelopers() {
        return String.join(", ", plot.getWorldPlayers().getAllDevelopers());
    }

    public boolean isBanned(String nickname) {
        for (String banned : bannedPlayers) {
            if (banned.equalsIgnoreCase(nickname)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getBannedPlayers() {
        return bannedPlayers;
    }

    public void purgeData() {
        List<String> empty = new ArrayList<>();
        buildersTrusted.clear();
        buildersNotTrusted.clear();
        developersGuests.clear();
        developersTrusted.clear();
        developersNotTrusted.clear();
        bannedPlayers.clear();
        setPlotConfigParameter(plot,"players.unique",empty);
        setPlotConfigParameter(plot,"players.liked",empty);
        setPlotConfigParameter(plot,"players.disliked",empty);
        setPlotConfigParameter(plot,"players.blacklist",empty);
        setPlotConfigParameter(plot,"players.whitelist",empty);
        setPlotConfigParameter(plot,"players.developers.trusted",empty);
        setPlotConfigParameter(plot,"players.developers.not-trusted",empty);
        setPlotConfigParameter(plot,"players.developers.guests",empty);
        setPlotConfigParameter(plot,"players.builders.trusted",empty);
        setPlotConfigParameter(plot,"players.builders.not-trusted",empty);
    }
}
