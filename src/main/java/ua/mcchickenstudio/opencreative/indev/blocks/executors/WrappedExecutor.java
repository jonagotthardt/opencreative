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

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.indev.blocks.WrappedActionBlock;
import ua.mcchickenstudio.opencreative.indev.blocks.WrappedCodingBlock;
import ua.mcchickenstudio.opencreative.indev.blocks.actions.ActionBlock;
import ua.mcchickenstudio.opencreative.indev.blocks.actions.WrappedAction;
import ua.mcchickenstudio.opencreative.indev.blocks.multiactions.MultiActionBlock;

import java.util.List;

public class WrappedExecutor extends WrappedCodingBlock<ExecutorBlock> {

    private final List<WrappedActionBlock> actions;

    public WrappedExecutor(ExecutorBlock executor, List<WrappedActionBlock> actions, int x, int y, int z) {
        super(executor, x, y, z);
        this.actions = actions;
    }

    public void execute(@NotNull WorldEvent worldEvent) {
        codingBlock.execute(worldEvent, actions);
    }
}
