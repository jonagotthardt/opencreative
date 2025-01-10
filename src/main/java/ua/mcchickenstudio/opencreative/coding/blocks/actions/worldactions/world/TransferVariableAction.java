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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import org.bukkit.entity.Entity;

public final class TransferVariableAction extends WorldAction {
    public TransferVariableAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        if (!getArguments().pathExists("world") || !getArguments().pathExists("key") || !getArguments().pathExists("value")) return;
        String worldId = getArguments().getValue("world","0",this);
        Planet planet = PlanetManager.getInstance().getPlanetById(worldId);
        if (planet == null) return;
        if (!planet.isLoaded()) return;
        if (!planet.isOwner(getPlanet().getOwner())) return;
        String key = getArguments().getValue("key","key",this);
        String value = getArguments().getValue("value","value",this);
        EventRaiser.raiseVariableTransferEvent(planet,key,value);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_TRANSFER_VARIABLE;
    }
}
