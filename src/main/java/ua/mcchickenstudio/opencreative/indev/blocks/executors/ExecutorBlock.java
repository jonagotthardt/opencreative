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
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.indev.blocks.WrappedActionBlock;
import ua.mcchickenstudio.opencreative.indev.blocks.CodingBlock;

import java.util.List;

public abstract class ExecutorBlock extends CodingBlock {

    public ExecutorBlock(@NotNull String id, @NotNull Material mainBlock, @NotNull Material offBlock) {
        super(id, mainBlock, offBlock);
    }

    public void execute(@NotNull WorldEvent event, @NotNull List<WrappedActionBlock> actions) {
        for (WrappedActionBlock block : actions) {

        }
        /*ActionsHandler handler = new ActionsHandler();
        for (ActionBlock action : actions) {
            action.execute(actionsHandler,arguments);
        }*/
    }

    @Override
    public void onSignClick(PlayerInteractEvent event) {
    }

    public abstract Class<? extends WorldEvent> getEventClass();

    @Override
    public String toString() {
        return "Executor " + getName() + ", ID: " + getId() + " by " + getCodingPackId();
    }
}
