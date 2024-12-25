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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.blocks;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SetSignGlowingTextAction extends WorldAction {
    public SetSignGlowingTextAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        List<Location> locations = getArguments().getLocationList("locations",this);
        boolean glowing = getArguments().getValue("glowing",true,this);
        String sideString = getArguments().getValue("side","front",this);
        Side side = (sideString.equals("back") ? Side.BACK : Side.FRONT);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                getPlanet().getLimits().setLastModifiedBlocksAmount(0);
            }
        };
        getPlanet().getTerritory().addBukkitRunnable(runnable);
        for (Location location : locations) {
            if (getPlanet().getLimits().getLastModifiedBlocksAmount() > getPlanet().getLimits().getModifyingBlocksLimit()) {
                runnable.runTaskLater(OpenCreative.getPlugin(),20L);
                getPlanet().getTerritory().removeBukkitRunnable(runnable);
                return;
            }
            if (location.getBlock().getState() instanceof Sign sign) {
                sign.getSide(side).setGlowingText(glowing);
                getPlanet().getLimits().setLastModifiedBlocksAmount(getPlanet().getLimits().getLastModifiedBlocksAmount()+1);
            }
        }
        runnable.runTaskLater(OpenCreative.getPlugin(),20L);
        getPlanet().getTerritory().removeBukkitRunnable(runnable);

    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SET_SIGN_GLOWING_TEXT;
    }
}
