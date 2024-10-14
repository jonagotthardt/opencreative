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

package mcchickenstudio.creative.coding.blocks.actions.worldactions.blocks;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.WorldAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
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
                getPlot().lastModifiedBlocksAmount = 0;
            }
        };
        getPlot().addBukkitRunnable(runnable);
        for (Location location : locations) {
            if (getPlot().lastModifiedBlocksAmount > getPlot().getModifyingBlocksLimit()) {
                runnable.runTaskLater(Main.getPlugin(),20L);
                getPlot().removeBukkitRunnable(runnable);
                return;
            }
            if (location.getBlock().getState() instanceof Sign sign) {
                sign.getSide(side).setGlowingText(glowing);
                getPlot().lastModifiedBlocksAmount++;
            }
        }
        runnable.runTaskLater(Main.getPlugin(),20L);
        getPlot().removeBukkitRunnable(runnable);

    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SET_SIGN_GLOWING_TEXT;
    }
}
