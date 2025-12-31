/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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

package ua.mcchickenstudio.opencreative.utils.millennium.shapes;

import ua.mcchickenstudio.opencreative.utils.millennium.math.BuildSpeed;
import ua.mcchickenstudio.opencreative.utils.millennium.math.GeneralMath;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec2;

import java.util.ArrayList;
import java.util.List;


/*
Draw a circle to ArrayList
 */

public final class Circle {
    private final List<Vec2> vectors;

    public Circle() {
        this.vectors = new ArrayList<>();
    }

    // round
    // 1 - circle
    // 0 - square

    // res
    // 1 - 360
    // 2 - 180
    // 4 - 90
    public void build(BuildSpeed s, double size, double round, int res) {
        vectors.clear();
        Vec2 firstVector = null;
        for (int k = 0; k <= 720; k += res) {
            double x = GeneralMath.sin((float) Math.toRadians(k), s) * res;
            double y = GeneralMath.cos((float) Math.toRadians(k), s) * res;
            double xj = x / Math.max(Math.abs(x), 1) * round;
            double yj = y / Math.max(Math.abs(y), 1) * round;
            Vec2 v2 = new Vec2(xj * size, yj * size);
            if (k == 0) {
                firstVector = v2;
            } else if (v2.compare(firstVector)) {
                break;
            }
            vectors.add(v2);
        }
    }

}
