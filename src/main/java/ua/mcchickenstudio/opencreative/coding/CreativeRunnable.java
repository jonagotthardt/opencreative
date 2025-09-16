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

package ua.mcchickenstudio.opencreative.coding;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>CreativeRunnable</h1>
 * This class represents BukkitRunnable with modifications that
 * stop executing code when planet is unloaded, or planet is not
 * in Play mode anymore, or player is offline.
 */
public abstract class CreativeRunnable {

    private final Planet planet;
    private int id;

    public CreativeRunnable(Planet planet) {
        this.planet = planet;
    }

    public abstract void execute(Player player);

    public synchronized void runTaskTimer(List<Player> onlinePlayers, long period, long timer) {
        List<Player> currentPlayers = new ArrayList<>(onlinePlayers);
        id = new BukkitRunnable() {
            @Override
            public void run() {
                if (planet != null && planet.getMode() == Planet.Mode.PLAYING) {
                    for (Player player : onlinePlayers) {
                        if (currentPlayers.isEmpty()) {
                            CreativeRunnable.this.cancel();
                        }
                        if (planet.getMode() != Planet.Mode.PLAYING) {
                            CreativeRunnable.this.cancel();
                        }
                        if (player == null) {
                           continue;
                        }
                        if (!player.isOnline()) {
                            currentPlayers.remove(player);
                            continue;
                        }
                        Planet playerPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                        if (!planet.equals(playerPlanet)) {
                            currentPlayers.remove(player);
                            continue;
                        }
                        DevPlanet playerDevPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (playerDevPlanet != null) {
                            currentPlayers.remove(player);
                            continue;
                        }
                        execute(player);
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(OpenCreative.getPlugin(),period,timer).getTaskId();
    }

    public synchronized void runTaskLater(List<Player> onlinePlayers, long delay) {
        List<Player> currentPlayers = new ArrayList<>(onlinePlayers);
        id = new BukkitRunnable() {
            @Override
            public void run() {
                if (planet != null && planet.getMode() == Planet.Mode.PLAYING) {
                    for (Player player : onlinePlayers) {
                        if (currentPlayers.isEmpty()) {
                            CreativeRunnable.this.cancel();
                        }
                        if (planet.getMode() != Planet.Mode.PLAYING) {
                            CreativeRunnable.this.cancel();
                        }
                        if (player == null) {
                            continue;
                        }
                        if (!player.isOnline()) {
                            currentPlayers.remove(player);
                            continue;
                        }
                        Planet playerPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                        if (!planet.equals(playerPlanet)) {
                            currentPlayers.remove(player);
                            continue;
                        }
                        DevPlanet playerDevPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (playerDevPlanet != null) {
                            currentPlayers.remove(player);
                            continue;
                        }
                        execute(player);
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskLater(OpenCreative.getPlugin(),delay).getTaskId();
    }

    public synchronized void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
