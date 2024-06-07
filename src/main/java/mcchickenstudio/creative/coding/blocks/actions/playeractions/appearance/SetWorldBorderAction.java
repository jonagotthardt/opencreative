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

package mcchickenstudio.creative.coding.blocks.actions.playeractions.appearance;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class SetWorldBorderAction extends PlayerAction {
    public SetWorldBorderAction(Executor executor, int x, Arguments args) {
        super(executor, x, args);
    }

    @Override
    public void execute(List<Entity> selection) {
        double radius = getArguments().getValue("radius",(getWorld() == null ? 10d : getWorld().getWorldBorder().getSize()));
        int time = getArguments().getValue("time",0);
        int warningDistance = getArguments().getValue("warning-distance",5);
        int warningTime = getArguments().getValue("warning-time",15);
        double damage = getArguments().getValue("damage",0.2d);
        int safeDistance = getArguments().getValue("safe-distance",5);
        WorldBorder border = Bukkit.createWorldBorder();
        border.setSize(radius,time);
        border.setWarningTime(warningTime);
        border.setWarningDistance(warningDistance);
        border.setDamageAmount(damage);
        border.setDamageBuffer(safeDistance);
        for (Player player : getPlayers(selection)) {
            Location center = getArguments().getValue("center",player.getLocation());
            border.setCenter(center);
            player.setWorldBorder(border);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PLAYER_SET_WORLD_BORDER;
    }
}
