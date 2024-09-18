package mcchickenstudio.creative.coding.blocks.actions.repeatactions;
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

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.actions.ActionCategory;
import mcchickenstudio.creative.coding.blocks.actions.MultiAction;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public abstract class RepeatAction extends MultiAction {

    private final int callsLimit = 200;
    private int calls = 0;

    public RepeatAction(Executor executor, Target target, int x, Arguments args, List<Action> actions) {
        super(executor, target, x, args, actions);
    }

    @Override
    public void executeActions() {
        super.executeActions();
        increaseCalls();
    }

    public void increaseCalls() {
        calls++;
        if (calls > callsLimit) {
            throw new RuntimeException("Out of repeats");
        }
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                calls--;
            }
        };
        getPlot().addBukkitRunnable(runnable);
        runnable.runTaskLater(Main.getPlugin(),20L);
    }

    @Override
    public ActionCategory getActionCategory() {
        return ActionCategory.REPEAT_ACTION;
    }
}
