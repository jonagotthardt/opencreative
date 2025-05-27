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

package ua.mcchickenstudio.opencreative.settings.groups;

public enum LimitType {

    ENTITIES("entities-amount"),
    CODE_OPERATIONS("executor-calls"),
    SCOREBOARDS("scoreboards-amount"),
    BOSSBARS("bossbars-amount"),
    REDSTONE_OPERATIONS("redstone-changes"),
    OPENING_INVENTORIES("opening-inventories"),
    SENDING_WEB_REQUESTS("sending-web-requests"),
    PHYSICAL_OBJECTS("physical-objects"),
    VARIABLES("variables-amount"),
    CODING_PLATFORMS("coding-platforms"),
    LIST_ELEMENTS_CHANGES("changing-list-elements"),
    MODIFYING_BLOCKS("modifying-blocks"),
    BUILDERS_AMOUNT("builders-amount"),
    DEVELOPERS_AMOUNT("developers-amount"),
    BLACKLISTED_AMOUNT("blacklisted-amount"),
    WHITELISTED_AMOUNT("whitelisted-amount");

    private final String path;

    LimitType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
