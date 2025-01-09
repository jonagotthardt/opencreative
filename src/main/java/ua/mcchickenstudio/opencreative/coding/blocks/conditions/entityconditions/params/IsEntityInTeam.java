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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.entityconditions.params;

import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.entityconditions.EntityCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

public class IsEntityInTeam extends EntityCondition {
    public IsEntityInTeam(Executor executor, Target target, int x, Arguments args, List<Action> actions, boolean isOpposed) {
        super(executor, target, x, args, actions, isOpposed);
    }

    @Override
    public boolean check(Entity entity) {
        if (!getArguments().pathExists("scoreboard") || !getArguments().pathExists("team")) {
            return false;
        }
        String scoreboardName = getArguments().getValue("scoreboard","board",this);
        String teamName = getArguments().getValue("team","team",this);
        Scoreboard scoreboard = getPlanet().getTerritory().getScoreboards().get(scoreboardName.toLowerCase());
        if (scoreboard == null) {
            return false;
        }
        Team team = scoreboard.getTeam(teamName);
        if (team != null) {
            return team.hasEntity(entity);
        }
        return false;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_ENTITY_IS_IN_RAIN;
    }
}
