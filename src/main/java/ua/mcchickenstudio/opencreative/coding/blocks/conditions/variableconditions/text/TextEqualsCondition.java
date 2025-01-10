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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.text;

import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.VariableCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

import java.util.List;

public class TextEqualsCondition extends VariableCondition {
    public TextEqualsCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, boolean isOpposed) {
        super(executor, target, x, args, actions, isOpposed);
    }

    @Override
    public boolean check(Entity entity) {
        if (!getArguments().pathExists("text") || !getArguments().pathExists("content")) {
            return false;
        }
        String text = getArguments().getValue("text","",this);
        String content = getArguments().getValue("content","",this);
        boolean ignoreColors = getArguments().getValue("ignore-colors",false,this);
        boolean ignoreCaps = getArguments().getValue("ignore-caps",false,this);
        if (ignoreColors) {
            text = ChatColor.stripColor(text);
            content = ChatColor.stripColor(content);
        }
        if (ignoreCaps) {
            return text.equalsIgnoreCase(content);
        } else {
            return text.equals(content);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_VAR_TEXT_EQUALS;
    }
}
