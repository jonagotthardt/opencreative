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
import org.bukkit.entity.Entity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class GiveItemsToContainerAction extends WorldAction {
    public GiveItemsToContainerAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        List<ItemStack> items = getArguments().getItemList("items",this);
        Location location = getArguments().getLocation("location",getPlanet().getTerritory().getSpawnLocation(),this);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                getPlanet().getLimits().setLastModifiedBlocksAmount(0);
            }
        };
        if (location.getBlock().getState() instanceof InventoryHolder container) {
            if (getPlanet().getLimits().getLastModifiedBlocksAmount() > getPlanet().getLimits().getModifyingBlocksLimit()) {
                runnable.runTaskLater(OpenCreative.getPlugin(),20L);
                getPlanet().getTerritory().removeBukkitRunnable(runnable);
                new LimitReachedBlocksEvent(getPlanet()).callEvent();
                return;
            }
            for (ItemStack item : items) {
                container.getInventory().addItem(item);
            }
            getPlanet().getLimits().setLastModifiedBlocksAmount(getPlanet().getLimits().getLastModifiedBlocksAmount()+1);
        }
        runnable.runTaskLater(OpenCreative.getPlugin(),20L);
        getPlanet().getTerritory().removeBukkitRunnable(runnable);

    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_GIVE_CONTAINER_ITEMS;
    }
}
