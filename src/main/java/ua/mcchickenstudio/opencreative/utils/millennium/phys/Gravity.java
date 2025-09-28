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

package ua.mcchickenstudio.opencreative.utils.millennium.phys;


public final class Gravity {

    private final double gravity;
    private double motion;

    public Gravity(double gravity) {
        this.gravity = gravity;
    }

    public Gravity(Number gravity) {
        this.gravity = gravity.doubleValue();
    }

    public void run() {
        this.motion -= this.gravity;
    }

    public void addMotion(double motion) {
        this.motion += motion;
    }

    public void addMotion(Number motion) {
        this.motion += motion.doubleValue();
    }

    public void reset() {
        this.motion = 0.0;
    }

}
