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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.MobInteractionEvent;

import java.util.List;
import java.util.Random;

public class TargetPlaceholder extends KeyPlaceholder {

    public TargetPlaceholder() {
        super("selected","selected_uuid","target","target_uuid","targets","selection");
    }

    @Override
    public String parse(String text, ActionsHandler handler, Action action) {
        Entity entity = action.getEntity();
        if (entity != null) {
            text = text
                    .replace("%selected%",entity.getName())
                    .replace("%selected_uuid%",entity.getUniqueId().toString())
                    .replace("%target%",entity.getName())
                    .replace("%target_uuid%",entity.getUniqueId().toString());
        }
        if (text.contains("%targets") || text.contains("%selection")) {
            List<String> names = action.getHandler().getSelectedTargets().stream().map(CommandSender::getName).toList();
            text = text.replace("%targets%",String.join(", ",names));
            text = text.replace("%selection%",String.join(", ",names));
        }
        return text;
    }

    @Override
    public String getCodingPackId() {
        return "default";
    }

    @Override
    public String getName() {
        return "Target Placeholder";
    }

    @Override
    public String getDescription() {
        return "Parses target placeholders";
    }
}
