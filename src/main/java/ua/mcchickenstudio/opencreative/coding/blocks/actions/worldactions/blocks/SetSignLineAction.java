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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.blocks;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;

import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.LimitReachedBlocksEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public final class SetSignLineAction extends WorldAction {
    public SetSignLineAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        Location location = getArguments().getValue("location",getWorld().getSpawnLocation(),this);
        int number = getArguments().getValue("number",1,this);
        Block block = location.getBlock();

        if (!(block.getState() instanceof Sign sign)) return;
        String text = getArguments().getValue("text","",this);
        String sideString = getArguments().getValue("side","front",this);
        Side side = (sideString.equals("back") ? Side.BACK : Side.FRONT);

        if (number <= 0 || number > sign.getSide(side).lines().size()) number = 1;

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                getPlanet().getLimits().setLastModifiedBlocksAmount(0);
            }
        };
        if (getPlanet().getLimits().getLastModifiedBlocksAmount() > getPlanet().getLimits().getModifyingBlocksLimit()) {
            runnable.runTaskLater(OpenCreative.getPlugin(),20L);
            getPlanet().getTerritory().removeBukkitRunnable(runnable);
            new LimitReachedBlocksEvent(getPlanet()).callEvent();
            return;
        }

        sign.getSide(side).setLine(number-1,text);
        getPlanet().getLimits().setLastModifiedBlocksAmount(getPlanet().getLimits().getLastModifiedBlocksAmount()+1);
        runnable.runTaskLater(OpenCreative.getPlugin(),20L);
        getPlanet().getTerritory().removeBukkitRunnable(runnable);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SET_SIGN_LINE;
    }
}
