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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.repeatactions;
import org.bukkit.entity.Entity;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooManyRepeatsException;
import ua.mcchickenstudio.opencreative.planets.PlanetRunnable;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugAction;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public abstract class RepeatAction extends MultiAction {

    private int calls = 0;

    public RepeatAction(Executor executor, Target target, int x, Arguments args, List<Action> actions) {
        super(executor, target, x, args, actions);
    }

    @Override
    public final void executeActions() {
        if (!checkCanContinue()) {
            return;
        }
        ActionsHandler handler = new ActionsHandler(this);
        handler.executeActions(getActions());
        increaseCalls();
        if (handler.getWaitDelay() > 0) {
            getPlanet().getTerritory().scheduleRunnable(
                    new PlanetRunnable(getPlanet()) {
                        @Override
                        public void execute() {
                            executeActions();
                        }
                    }, handler.getWaitDelay());
        } else {
            executeActions();
        }
    }

    @Override
    protected void execute(Entity entity) {
        executeActions();
    }

    public void increaseCalls() {
        calls++;
        if (calls > getPlanet().getLimits().getRepeatsAmountLimit()) {
            throw new TooManyRepeatsException();
        }
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                calls--;
            }
        };
        getPlanet().getTerritory().addBukkitRunnable(runnable);
        runnable.runTaskLater(OpenCreative.getPlugin(),20L);
    }

    public abstract boolean checkCanContinue();

    @Override
    public ActionCategory getActionCategory() {
        return ActionCategory.REPEAT_ACTION;
    }
}
