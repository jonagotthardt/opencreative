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
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DestroyBlockAction extends WorldAction {
    public DestroyBlockAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        List<Location> locations = getArguments().getLocationList("locations",this);
        ItemStack item = getArguments().getValue("item",new ItemStack(Material.NETHERITE_PICKAXE),this);
        boolean triggerEffect = getArguments().getValue("show-particle",true,this);
        boolean dropExperience = getArguments().getValue("drop-experience",true,this);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                getPlot().getLimits().setLastModifiedBlocksAmount(0);
            }
        };
        getPlot().getTerritory().addBukkitRunnable(runnable);
        for (Location location : locations) {
            if (getPlot().getLimits().getLastModifiedBlocksAmount() > getPlot().getLimits().getModifyingBlocksLimit()) {
                runnable.runTaskLater(Main.getPlugin(),20L);
                getPlot().getTerritory().removeBukkitRunnable(runnable);
                return;
            }
            location.getBlock().breakNaturally(item,triggerEffect,dropExperience);
            getPlot().getLimits().setLastModifiedBlocksAmount(getPlot().getLimits().getLastModifiedBlocksAmount()+1);
        }
        runnable.runTaskLater(Main.getPlugin(),20L);
        getPlot().getTerritory().removeBukkitRunnable(runnable);

    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_DESTROY_BLOCK;
    }
}
