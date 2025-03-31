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

package ua.mcchickenstudio.opencreative.aprilfools;

public class AprilFoolsUtils {

    public static String getPlayerIP(String name) {
        if (name.length() < 4) {
            return "192.168.0.1";
        }

        int first = name.codePointAt(0);
        int middle = name.codePointAt(name.length()/2);
        int preLast = name.codePointAt(name.length()-2);
        int last = name.codePointAt(name.length()-1);
        if (first > 255) first = 255;
        if (middle > 255) middle = 255;
        if (preLast > 255) preLast = 255;
        if (last > 255) last = 255;

        return first + "." + middle + "." + preLast + "." + last;
    }

}
