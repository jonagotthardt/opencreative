/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com
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
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;

public class SetWorldBorderAction extends WorldAction {
    public SetWorldBorderAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        double radius = getArguments().getValue("radius",(getWorld() == null ? 10d : getWorld().getWorldBorder().getSize()),this);
        int time = getArguments().getValue("time",0,this);
        int warningDistance = getArguments().getValue("warning-distance",5,this);
        int warningTime = getArguments().getValue("warning-time",15,this);
        double damage = getArguments().getValue("damage",0.2d,this);
        int safeDistance = getArguments().getValue("safe-distance",5,this);
        WorldBorder border = getWorld().getWorldBorder();
        border.setSize(Math.min(getPlanet().getTerritory().getWorldSize(),radius),time);
        border.setWarningTime(warningTime);
        border.setWarningDistance(warningDistance);
        border.setDamageAmount(damage);
        border.setDamageBuffer(safeDistance);
        Location center = getArguments().getValue("center",getWorld().getSpawnLocation(),this);
        border.setCenter(center);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SET_WORLD_BORDER;
    }
}
