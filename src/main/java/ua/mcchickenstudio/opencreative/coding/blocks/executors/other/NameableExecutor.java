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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.other;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.planets.Planet;

/**
 * <h1>NameableExecutor</h1>
 * This class represents an executor, that has name (custom id)
 * to call it with other actions.
 */
public abstract class NameableExecutor extends Executor  {

    private final String name;

    public NameableExecutor(Planet planet, int x, int y, int z, @NotNull String name) {
        super(planet, x, y, z);
        this.name = name;
    }

    /**
     * Returns name of executor for calling it.
     *
     * @return custom id of executor.
     */
    public final @NotNull String getName() {
        return name;
    }

}
