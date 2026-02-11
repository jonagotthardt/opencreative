/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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

package ua.mcchickenstudio.opencreative.managers.downloader;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * <h1>Downloader</h1>
 * This class represents a download manager, that will be used
 * to compress and upload planet folders archive by using
 * command: /world download.
 */
public final class Downloader implements DownloadManager {

    @Override
    public @NotNull CompletableFuture<String> uploadPlanet(@NotNull Planet planet, @NotNull Player player) {
        CompletableFuture<String> future = new CompletableFuture<>();
        compressPlanetToArchive(planet);
        return null;
    }

    @Override
    public @NotNull File compressPlanetToArchive(@NotNull Planet planet) {
        return null;
    }

    @Override
    public void clearArchives(@NotNull Planet planet) {

    }

    @Override
    public void clearAllArchives() {

    }

    @Override
    public void init() {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return "";
    }
}
