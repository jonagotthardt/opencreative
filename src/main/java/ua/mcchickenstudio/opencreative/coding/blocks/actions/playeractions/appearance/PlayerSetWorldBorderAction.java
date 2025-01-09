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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.appearance;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

public final class PlayerSetWorldBorderAction extends PlayerAction {
    public PlayerSetWorldBorderAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(Player player) {
        double radius = getArguments().getValue("radius",(getWorld() == null ? 10d : getWorld().getWorldBorder().getSize()),this);
        int time = getArguments().getValue("time",0,this);
        int warningDistance = getArguments().getValue("warning-distance",5,this);
        int warningTime = getArguments().getValue("warning-time",15,this);
        double damage = getArguments().getValue("damage",0.2d,this);
        int safeDistance = getArguments().getValue("safe-distance",5,this);
        WorldBorder border = Bukkit.createWorldBorder();
        border.setSize(radius,time);
        border.setWarningTime(warningTime);
        border.setWarningDistance(warningDistance);
        border.setDamageAmount(damage);
        border.setDamageBuffer(safeDistance);
        Location center = getArguments().getValue("center",player.getLocation(),this);
        border.setCenter(center);
        player.setWorldBorder(border);
    }


    @Override
    public ActionType getActionType() {
        return ActionType.PLAYER_SET_WORLD_BORDER;
    }
}
