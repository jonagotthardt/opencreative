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

package mcchickenstudio.creative.coding.blocks.actions.worldactions.world;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.WorldAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardSetDisplayNameAction extends WorldAction {
    public ScoreboardSetDisplayNameAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        if (!getArguments().pathExists("name") || !getArguments().pathExists("display-name")) {
            return;
        }
        String name = getArguments().getValue("name","board",this);
        String displayName = getArguments().getValue("display-name","Scoreboard",this);
        Scoreboard scoreboard = getPlot().getScoreboards().get(name.toLowerCase());
        if (scoreboard == null) {
            return;
        }
        Objective objective = scoreboard.getObjective("score");
        if (objective != null) {
            objective.displayName(Component.text(displayName));
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_SCOREBOARD_SET_SCORE;
    }
}
