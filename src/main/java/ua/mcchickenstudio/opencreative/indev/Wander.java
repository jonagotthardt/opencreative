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

package ua.mcchickenstudio.opencreative.indev;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.UUID;

/**
 * <h1>Wander</h1>
 * This class represents wander, that could be online.
 * Wander is a player, who plays on planets.
 * He has nickname, description, gender, favorite
 * worlds and last played world.
 */
public final class Wander extends OfflineWander implements Audience {

    private final Player player;

    public Wander(@NotNull Player player) {
        super(player);
        this.player = player;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Returns current planet, where wander is currently in.
     * @return current planet, or null.
     */
    public @Nullable Planet getCurrentPlanet() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return null;
        return OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
    }
}
