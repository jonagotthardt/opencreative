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

package ua.mcchickenstudio.opencreative.coding.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.MobInteractionEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;

public class PlanetPlaceholder extends KeyPlaceholder {

    public PlanetPlaceholder() {
        super("online","players_amount","entities_amount");
    }

    @Override
    public String parse(String text, ActionsHandler handler, Action action) {
        Planet planet = handler.getExecutor().getPlanet();
        text = text
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%players_amount%", String.valueOf(planet.getPlayers().size()))
                .replace("%entities_amount%", String.valueOf(planet.getTerritory().getWorld().getEntityCount() + (planet.getDevPlanet() != null && planet.getDevPlanet().getWorld() != null ? planet.getDevPlanet().getWorld().getEntityCount() : 0)));
        return text;
    }

    @Override
    public String getCodingPackId() {
        return "default";
    }

    @Override
    public String getName() {
        return "Planet Placeholder";
    }

    @Override
    public String getDescription() {
        return "Parses planet placeholders";
    }
}
