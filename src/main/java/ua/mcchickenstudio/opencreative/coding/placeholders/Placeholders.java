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

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;

public class Placeholders {

    private static Placeholders instance;

    private final List<Placeholder> placeholders = new ArrayList<>();

    public synchronized static Placeholders getInstance() {
        if (instance == null) {
            instance = new Placeholders();
            instance.registerPlaceholder(new SymbolPlaceholder());
            instance.registerPlaceholder(new PlayerPlaceholder());
            instance.registerPlaceholder(new TargetPlaceholder());
            instance.registerPlaceholder(new EntityPlaceholder());
            instance.registerPlaceholder(new RandomPlaceholder());
            instance.registerPlaceholder(new EventPlaceholder());
            instance.registerPlaceholder(new PlanetPlaceholder());
            instance.registerPlaceholder(new VarPlaceholder());
        }
        return instance;
    }

    public void registerPlaceholder(Placeholder placeholder) {
        if (placeholder instanceof KeyPlaceholder key) {
            if (key.getKeys().length == 0) {
                sendWarningErrorMessage("[PLACEHOLDERS] " + placeholder + " will be not registered, because has 0 keys.");
                return;
            }
            for (Placeholder listedPlaceholder : placeholders) {
                if (listedPlaceholder instanceof KeyPlaceholder key2) {
                    Set<String> sameKeys = getSameKeys(key,key2);
                    if (sameKeys.isEmpty()) break;
                    sendWarningErrorMessage("[PLACEHOLDERS] Same placeholders keys conflict " + key + ", " + key2 +" in: " + String.join(", ",sameKeys));
                }
            }
        }
        sendDebug("[PLACEHOLDERS] Registered " + placeholder);
        placeholders.add(placeholder);
    }

    private Set<String> getSameKeys(KeyPlaceholder first, KeyPlaceholder second) {
        Set<String> sameKeys = new HashSet<>();
        for (String key : first.getKeys()) {
            if (Arrays.asList(second.getKeys()).contains(key)) {
                sameKeys.add(key);
            }
        }
        return sameKeys;
    }

    public String parseAction(String text, ActionsHandler handler, Action action) {
        text = text.replace("\\n","\n");
        try {
            for (Placeholder placeholder : placeholders) {
                if (placeholder.matches(text)) {
                    text = placeholder.parse(text,handler,action);
                }
            }
        } catch (Exception error) {
            sendCriticalErrorMessage("[PLACEHOLDERS] Can't parse placeholder",error);
        }

        return text;
    }

}
