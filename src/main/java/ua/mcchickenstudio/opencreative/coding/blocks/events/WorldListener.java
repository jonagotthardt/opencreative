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

package ua.mcchickenstudio.opencreative.coding.blocks.events;

import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.entities.EntitySpawnEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.GamePlayEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.VariableTransferEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.WebResponseEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executors;
import ua.mcchickenstudio.opencreative.indev.blocks.executors.ExecutorBlock;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebug;

/**
 * <h1>WorldListener</h1>
 * This class represents listener of all Creative events in planet.
 * It activates executors on events listening.
 */
public class WorldListener implements EventExecutor, Listener {

    public void registerExecutors() {
        try {
            Bukkit.getPluginManager().registerEvent(
                    WorldEvent.class,
                    this, EventPriority.NORMAL,this,
                    OpenCreative.getPlugin());
        } catch (Exception error) {
            sendCriticalErrorMessage("Cannot register world events listener: planets code will not work.", error);
        }
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
        if (event instanceof WorldEvent worldEvent) {
            Executors.activate(worldEvent);
        }
    }

}
