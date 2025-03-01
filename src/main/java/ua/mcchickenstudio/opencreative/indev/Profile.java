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

package ua.mcchickenstudio.opencreative.indev;

import java.util.UUID;

/**
 * In development.
 */
public class Profile {

    private final UUID uuid;

    private String name;
    private String description;

    public Profile(UUID uuid) {
        this.uuid = uuid;
    }

    public void loadInfo() {
        /*
         * example yaml
         *
         * uuid: OOO-OOO-OOO-OOO
         * name: Notch
         * description: The famous games creator ever.
         * gender: Male
         * country: Scotland
         * last-location:
         *   world: world
         *   x: 1
         *   y: 1
         *   z: 1
         * birth:
         *   day: 21
         *   month: 08
         *   year: 1990
         * favourite-worlds: [1, 2, 3, 4, 5, 6, 7]
         * club: AAA-AAA-AAA-AAA
         */
    }
}
