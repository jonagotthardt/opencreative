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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.state;

import org.bukkit.Location;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PolarBear;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class EntitySetAllayDancingAction extends EntityAction {
    public EntitySetAllayDancingAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        if (!(entity instanceof Allay allay)) {
            return;
        }
        boolean value = getArguments().getValue("boolean", true, this);
        Location jukebox = getArguments().getValue("jukebox",
                getPlanet().getTerritory().getWorld().getSpawnLocation(), this);
        if (value) {
            if (getArguments().pathExists("jukebox")) {
                allay.startDancing(jukebox);
            } else {
                allay.startDancing();
            }
        } else {
            allay.stopDancing();
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ENTITY_SET_ALLAY_DANCING;
    }
}
