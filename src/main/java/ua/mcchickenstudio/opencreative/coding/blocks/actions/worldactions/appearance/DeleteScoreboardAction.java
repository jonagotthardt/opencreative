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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.appearance;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public final class DeleteScoreboardAction extends WorldAction {
    public DeleteScoreboardAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        if (!getArguments().pathExists("scoreboards")) {
            return;
        }
        List<String> scoreboards = getArguments().getTextList("scoreboards",this);
        for (String name : scoreboards) {
            Scoreboard scoreboard = getPlanet().getTerritory().getScoreboards().get(name.toLowerCase());
            if (scoreboard != null) {
                Objective objective = scoreboard.getObjective("score");
                if (objective != null) {
                    objective.unregister();
                }
                for (Player player : getPlanet().getPlayers()) {
                    if (player.getScoreboard().equals(scoreboard)) {
                        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                    }
                }
                getPlanet().getTerritory().getScoreboards().remove(name.toLowerCase());
            }
        }


    }

    @Override
    public ActionType getActionType() {
        return ActionType.WORLD_DELETE_SCORE_BOARD;
    }
}
