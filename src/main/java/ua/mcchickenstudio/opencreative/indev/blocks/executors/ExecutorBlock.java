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

package ua.mcchickenstudio.opencreative.indev.blocks.executors;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.indev.blocks.actions.ActionBlock;
import ua.mcchickenstudio.opencreative.indev.blocks.CodingBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ExecutorBlock extends CodingBlock {

    private final List<ActionBlock> actions = new ArrayList<>();

    public ExecutorBlock(@NotNull String id, @NotNull Material mainBlock, @NotNull Material offBlock) {
        super(id, mainBlock, offBlock);
    }

    public void execute(WorldEvent event, Arguments arguments) {
        /*ActionsHandler handler = new ActionsHandler();
        for (ActionBlock action : actions) {
            action.execute(actionsHandler,arguments);
        }*/
    }

    @Override
    public void onSignClick(PlayerInteractEvent event) {
    }

    @Override
    public @Nullable CodingBlock deserialize(Map<String, Object> args) {
        return null;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of();
    }
}
