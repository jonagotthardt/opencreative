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

package ua.mcchickenstudio.opencreative.coding.placeholders;

import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.coding.variables.WorldVariable;
import ua.mcchickenstudio.opencreative.coding.variables.WorldVariables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VarPlaceholder extends Placeholder {

    private static final Pattern PATTERN = Pattern.compile("%(var|var_local|var_game|var_global|var_save|var_saved)\\(([^)]+)\\)");
    private static final int limit = 20;

    @Override
    public boolean matches(String text) {
        Matcher matcher = PATTERN.matcher(text);
        return matcher.find();
    }

    public static Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public String parse(String text, ActionsHandler handler, Action action) {
        Matcher matcher = PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();
        WorldVariables variables = action.getExecutor().getPlanet().getVariables();
        int count = 0;
        while (matcher.find()) {
            count++;
            if (count >= limit) break;
            String type = matcher.group(1);
            String name = matcher.group(2);
            WorldVariable variable = switch (type) {
                case "var_local" ->
                        variables.getVariable(new VariableLink(name, VariableLink.VariableType.LOCAL), action);
                case "var", "var_game", "var_global" ->
                        variables.getVariable(new VariableLink(name, VariableLink.VariableType.GLOBAL), action);
                case "var_save", "var_saved" ->
                        variables.getVariable(new VariableLink(name, VariableLink.VariableType.SAVED), action);
                default -> null;
            };
            String replacement = "null! " + name + " - " + type.toUpperCase();
            if (variable != null) {
                replacement = String.valueOf(variable.getValue()).substring(0,Math.min(100,String.valueOf(variable.getValue()).length()));
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    @Override
    public String getCodingPackId() {
        return "default";
    }

    @Override
    public String getName() {
        return "Variable Placeholder";
    }

    @Override
    public String getDescription() {
        return "Parses variable placeholders";
    }
}
