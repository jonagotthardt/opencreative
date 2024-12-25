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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.other;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.scheduler.BukkitRunnable;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugExecutor;

public class Cycle extends Executor {

    private final String name;
    private final int repeatTime;
    private boolean enabled = false;
    private BukkitRunnable runnable = null;

    public Cycle(Planet planet, int x, int y, int z, String name, int repeatTime) {
        super(planet, x, y, z);
        this.name = name;
        this.repeatTime = repeatTime;
    }

    @Override
    public void run(WorldEvent event) {
        if (!enabled) {
            enabled = true;
            Executor executor = this;
            runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    sendCodingDebugExecutor(executor);
                    executeActions(event);
                }
            };
            getPlanet().getTerritory().addBukkitRunnable(runnable);
            runnable.runTaskTimer(OpenCreative.getPlugin(),0,repeatTime);
        }
    }

    public void stop() {
        if (runnable != null) {
            runnable.cancel();
            runnable = null;
            enabled = false;
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public ExecutorType getExecutorType() {
        return ExecutorType.CYCLE;
    }

    @Override
    public ExecutorCategory getExecutorCategory() {
        return ExecutorCategory.CYCLE;
    }
}
