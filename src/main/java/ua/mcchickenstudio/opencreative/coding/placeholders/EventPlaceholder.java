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

import org.bukkit.entity.Entity;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.MobDamagesPlayerEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerDamagesMobEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerDamagesPlayerEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerKilledPlayerEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.MobInteractionEvent;

public class EventPlaceholder extends KeyPlaceholder {

    public EventPlaceholder() {
        super("killer","damager","killer_uuid","damager_uuid","victim","victim_uuid");
    }

    @Override
    public String parse(String text, ActionsHandler handler, Action action) {
        WorldEvent worldEvent = handler.getEvent();
        Entity killer = null;
        Entity victim = null;
        if (worldEvent instanceof PlayerDamagesMobEvent event) {
            killer = event.getDamager();
            victim = event.getVictim();
        } else if (worldEvent instanceof PlayerDamagesPlayerEvent event) {
            killer = event.getDamager();
            victim = event.getVictim();
        } if (worldEvent instanceof MobDamagesPlayerEvent event) {
            killer = event.getDamager();
            victim = event.getVictim();
        } if (worldEvent instanceof PlayerKilledPlayerEvent event) {
            killer = event.getKiller();
            victim = event.getVictim();
        }
        if (killer != null) {
            text = text
                    .replace("%killer%",killer.getName())
                    .replace("%killer_uuid%",killer.getUniqueId().toString())
                    .replace("%damager%",killer.getName())
                    .replace("%damager_uuid%",killer.getUniqueId().toString());
        }
        if (victim != null) {
            text = text
                    .replace("%victim%",victim.getName())
                    .replace("%victim_uuid%",victim.getUniqueId().toString());
        }
        return text;
    }

    @Override
    public String getCodingPackId() {
        return "default";
    }

    @Override
    public String getName() {
        return "Event Placeholder";
    }

    @Override
    public String getDescription() {
        return "Parses event placeholders";
    }
}
