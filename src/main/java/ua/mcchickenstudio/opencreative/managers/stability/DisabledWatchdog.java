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

package ua.mcchickenstudio.opencreative.managers.stability;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class DisabledWatchdog implements StabilityManager {

    @Override
    public void init() {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public @NotNull StabilityState getDatabaseState() {
        return StabilityState.FINE;
    }

    @Override
    public @NotNull StabilityState getMemoryState() {
        return StabilityState.FINE;
    }

    @Override
    public @NotNull StabilityState getStorageState() {
        return StabilityState.FINE;
    }

    @Override
    public @NotNull StabilityState getTicksState() {
        return StabilityState.FINE;
    }

    @Override
    public String getName() {
        return "Disabled Watchdog";
    }


    @Override
    public @NotNull StabilityState getState() {
        return StabilityState.FINE;
    }
}
