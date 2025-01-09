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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.params;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class EntitySetFireTicksAction extends EntityAction {
    public EntitySetFireTicksAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void execute(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) return;
        boolean add = getArguments().getValue("add",false,this);
        int ticks = getArguments().getValue("ticks",0,this);
        if (!add) {
            livingEntity.setFireTicks(ticks);
        } else {
            livingEntity.setFireTicks(livingEntity.getFireTicks()+ticks);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ENTITY_SET_FIRE_TICKS;
    }
}
