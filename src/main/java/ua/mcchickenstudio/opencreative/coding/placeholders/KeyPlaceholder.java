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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class KeyPlaceholder extends Placeholder {

    private final String[] keys;
    private final static Pattern PATTERN_PLACEHOLDER = Pattern.compile("%[A-Za-z0-9]+%");

    public KeyPlaceholder(String... keys) {
        this.keys = keys;
    }

    @Override
    public boolean matches(String text) {
        Matcher matcher = PATTERN_PLACEHOLDER.matcher(text);
        while (matcher.find()) {
            String placeholder = matcher.group();
            String key = placeholder.substring(1, placeholder.length() - 1);
            for (String listedKey : keys) {
                if (key.equals(listedKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String[] getKeys() {
        return keys;
    }

    public abstract String parse(String text, ActionsHandler handler, Action action);

}
