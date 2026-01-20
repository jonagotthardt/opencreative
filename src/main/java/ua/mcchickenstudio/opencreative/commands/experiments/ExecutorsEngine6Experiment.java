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

package ua.mcchickenstudio.opencreative.commands.experiments;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.indev.blocks.ExecutorsNew;
import ua.mcchickenstudio.opencreative.indev.blocks.executors.player.world.PlayerConnectExecutor;

import java.util.List;

public final class ExecutorsEngine6Experiment extends Experiment {

    private ExecutorsNew executorsNew;

    @Override
    public @NotNull String getId() {
        return "executors_engine_6";
    }

    @Override
    public @NotNull String getName() {
        return "Executors Engine 6";
    }

    @Override
    public @NotNull String getDescription() {
        return "New executors creator and handler";
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, @NotNull String[] args) {}

    @Override
    public void onEnable() {
        OpenCreative.getPlugin().getLogger().info("[EXPERIMENT] Enabled Executors Engine 6");
        executorsNew = new ExecutorsNew();
        executorsNew.registerExecutor(new PlayerConnectExecutor());
    }

    @Override
    public void onDisable() {
        executorsNew = null;
    }

    @Override
    public @Nullable List<String> tabCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        return null;
    }

}
