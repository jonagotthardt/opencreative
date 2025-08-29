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

package ua.mcchickenstudio.opencreative.utils;

/**
 * <h1>PlayerConfirmation</h1>
 * This enum stores all confirmation types for
 * players, when they have to confirm their action.
 */
public enum PlayerConfirmation {

    /**
     * When world's owner changes world's name.
     */
    WORLD_NAME_CHANGE,
    /**
     * When world's owner changes world's description.
     */
    WORLD_DESCRIPTION_CHANGE,
    /**
     * When world's owner changes world's custom id.
     */
    WORLD_CUSTOM_ID_CHANGE,
    /**
     * When player searches worlds by id.
     */
    FIND_PLANETS_BY_ID,
    /**
     * When player searches worlds by name.
     */
    FIND_PLANETS_BY_NAME,
    /**
     * When player searches worlds by owner.
     */
    FIND_PLANETS_BY_OWNER,
    /**
     * When world's owner transfers ownership to other player.
     */
    TRANSFER_OWNERSHIP,
    /**
     * When new world's owner accepts taking ownership of world.
     */
    GET_OWNERSHIP,
    /**
     * When module's owner changes module's display name.
     */
    MODULE_NAME_CHANGE,
    /**
     * When module's owner changes module's description.
     */
    MODULE_DESCRIPTION_CHANGE,

}
