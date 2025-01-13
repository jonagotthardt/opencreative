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

package ua.mcchickenstudio.opencreative.settings;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public enum Sounds {

    LOBBY,
    OPENCREATIVE,
    MENU_NEXT_PAGE,
    MENU_PREVIOUS_PAGE,
    MENU_NEXT_CHOICE,
    MENU_OPEN,
    MENU_OPEN_ENVIRONMENT,
    MENU_OPEN_WORLD_SETTINGS,
    MENU_OPEN_RECOMMENDATIONS,
    MENU_OPEN_WORLDS_BROWSER,
    MENU_OPEN_ENTITIES_BROWSER,
    MENU_OPEN_EVENTS_BROWSER,
    MENU_OPEN_ACTIONS_BROWSER,
    MENU_OPEN_CONDITIONS_BROWSER,
    MENU_OPEN_VALUES_BROWSER,
    MENU_OPEN_DEV_CHEST,
    MENU_OPEN_CONFIRMATION,
    WORLD_GENERATION,
    WORLD_CONNECTION,
    WORLD_CONNECTED,
    WORLD_LIKED,
    WORLD_DISLIKED,
    WORLD_ADVERTISED,
    WELCOME_TO_NEW_WORLD,
    PLAYER_ERROR,
    WORLD_CODE_ERROR,
    WORLD_CODE_CRITICAL_ERROR,
    WORLD_NOTIFICATION,
    WORLD_SETTINGS_FAIL,
    WORLD_SETTINGS_SUCCESS,
    WORLD_SETTINGS_TIME_CHANGE,
    WORLD_SETTINGS_SPAWN_TELEPORT,
    WORLD_SETTINGS_SPAWN_SET,
    WORLD_SETTINGS_OWNER_SET,
    DEV_CONNECTED,
    DEV_SET_EVENT,
    DEV_SET_ACTION,
    DEV_SET_CONDITION,
    DEV_NAMED_METHOD,
    DEV_NAMED_FUNCTION,
    DEV_NAMED_CYCLE,
    DEV_CLAIMED_PLATFORM,
    DEV_VALUE_SET,
    DEV_TEXT_SET,
    DEV_POTION_SET,
    DEV_POTION_APPLY,
    DEV_POTION_REMOVE,
    DEV_LOCATION_SET,
    DEV_LOCATION_TELEPORT,
    DEV_LOCATION_TELEPORT_BACK,
    DEV_VECTOR_SET,
    DEV_VECTOR_APPLY,
    DEV_BOOLEAN_TRUE,
    DEV_BOOLEAN_FALSE,
    DEV_MOVE_BLOCKS_RIGHT,
    DEV_MOVE_BLOCKS_LEFT,
    DEV_DEBUG_ON,
    DEV_DEBUG_OFF,
    MAINTENANCE_START,
    MAINTENANCE_COUNT,
    MAINTENANCE_END;

    void playSound(Audience audience) {
        audience.playSound(Sound.sound(Key.key(
                name().toLowerCase()),
                Sound.Source.RECORD,
                100f, 2f));
    }

}
