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

package ua.mcchickenstudio.opencreative.indev.space;

import ua.mcchickenstudio.opencreative.managers.Manager;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.Set;

/**
 * <h1>PlanetsManager</h1>
 * This interface represents a planets manager,
 * that can manipulates with planets. It has methods
 * to get current planet by player, world and info.
 * It creates, registers and deletes planets.
 */
public interface PlanetsManager extends Manager {

    Set<Planet> getPlanets();

    Set<Planet> getPlayerPlanets(String owner);

    Set<Planet> getPlanetsContainingName(String name);

    Set<Planet> getPlanetsContainingId(String id);

    Set<Planet> getPlanetsContainingOwner(String owner);

    void registerPlanet();

}
