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

package mcchickenstudio.creative.coding.blocks.actions.worldactions.appearance;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.WorldAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import static mcchickenstudio.creative.utils.ErrorUtils.sendCodingDebugLog;

public class CreateScoreboardAction extends WorldAction {
    public CreateScoreboardAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        if (!getArguments().pathExists("name")) {
            return;
        }
        String name = getArguments().getValue("name","board",this);
        String displayName = getArguments().getValue("display-name","Scoreboard",this);
        try {
            if (getPlot().getTerritory().getScoreboards().size() >= getPlot().getLimits().getScoreboardsLimit()) {
                // FIXME: Replace with hard-coded message, sendMessageOnce()
                sendCodingDebugLog(getPlot(),"Limit of " + getPlot().getLimits().getScoreboardsLimit() + " scoreboards reached.");
                return;
            }
            Scoreboard scoreboard;
            if (getPlot().getTerritory().getScoreboards().containsKey(name.toLowerCase())) {
                scoreboard = getPlot().getTerritory().getScoreboards().get(name.toLowerCase());
                Objective objective = scoreboard.getObjective("score");
                if (objective == null) {
                    objective = scoreboard.registerNewObjective("score",Criteria.DUMMY,displayName);
                }
                objective.displayName(Component.text(displayName));
            } else {
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective objective = scoreboard.registerNewObjective("score",Criteria.DUMMY,displayName);
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            getPlot().getTerritory().getScoreboards().put(name.toLowerCase(),scoreboard);
        } catch (Exception ignored) {}
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_CREATE_SCOREBOARD;
    }
}
