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

package ua.mcchickenstudio.opencreative.indev.values;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebug;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventValues;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.indev.values.world.WorldIdValue;

public class EventValuesConcept {
    
    private static EventValuesConcept instance;
    private final List<EventValueTest> eventValues = new LinkedList<>();

    /**
     * Returns instance of event values controller class.
     * @return instance of event values.
     */
    public synchronized static EventValuesConcept getInstance() {
        if (instance == null) {
            instance = new EventValuesConcept();
            instance.registerDefaults();
        }
        return instance;
    }

    /**
     * Registers event value, that will be replaced in coding.
     * @param value event value to register.
     */
    public void registerEventValue(@NotNull EventValueTest value) {
        sendDebug("[VALUES] Registered " + value);
        eventValues.add(value);
    }

    /**
     * Registers event values, that will be replaced in coding.
     * @param values event values to register.
     */
    public void registerEventValue(@NotNull EventValueTest... values) {
        for (EventValueTest value : values) {
            registerEventValue(value);
        }
    }

    /**
     * Unregisters event value if list contains it.
     * @param value event value to unregister.
     */
    public void unregisterEventValue(@NotNull EventValueTest value) {
        eventValues.remove(value);
    }

    /**
     * Returns a copy of list that contains all registered event values.
     * @return event values list.
     */
    public @NotNull List<EventValueTest> getEventValueTests() {
        return new ArrayList<>(eventValues);
    }

    private void registerDefaults() {
        registerEventValue(new WorldIdValue());
    }

    public @NotNull List<EventValueTest> getByCategories(@NotNull MenusCategory menusCategory) {
        List<EventValueTest> list = new ArrayList<>();
        for (EventValueTest type : eventValues) {
            if (type.getCategory() == menusCategory) {
                list.add(type);
            }
        }
        return list;
    }

}
