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

import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;

public class PlayerPlaceholder extends KeyPlaceholder {

    public PlayerPlaceholder() {
        super("player","player_uuid","display_name");
    }

    @Override
    public String parse(String text, ActionsHandler handler, Action action) {
        if (handler.getEvent().getSelection().getFirst() instanceof Player player) {
            text = text
                    .replace("%player%",player.getName())
                    .replace("%player_uuid%",player.getUniqueId().toString())
                    .replace("%display_name%",player.getDisplayName());
        }
        return text;
    }

    @Override
    public String getCodingPackId() {
        return "default";
    }

    @Override
    public String getName() {
        return "Player Placeholder";
    }

    @Override
    public String getDescription() {
        return "Parses player placeholders";
    }
}
