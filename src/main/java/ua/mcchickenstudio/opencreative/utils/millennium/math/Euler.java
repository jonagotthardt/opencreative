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

package ua.mcchickenstudio.opencreative.utils.millennium.math;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec2;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec3;

import static java.lang.Math.PI;

public final class Euler {

    public static boolean compareTwoVectorAngle(@NotNull Vec2 a, @NotNull Vec2 b, double radi) {
        double angleA = Math.atan2(a.getY(), a.getX());
        double angleB = Math.atan2(b.getY(), b.getX());

        double angleDiff = Math.abs(angleA - angleB);

        if (angleDiff > Math.PI) {
            angleDiff = 2 * Math.PI - angleDiff;
        }

        return angleDiff <= Math.toRadians(radi);
    }

    public static double calculateTwoVectorAngleDifference(@NotNull Vec2 a, @NotNull Vec2 b) {
        double angleA = Math.atan2(a.getY(), a.getX());
        double angleB = Math.atan2(b.getY(), b.getX());

        double angleDiff = Math.abs(angleA - angleB);

        if (angleDiff > Math.PI) {
            angleDiff = 2 * Math.PI - angleDiff;
        }
        return angleDiff;
    }

    public static Vec2 calculateVec2Vec(final Vec3 from, final Vec3 to) {
        final Vec3 diff = to.subtract(from);
        final double distance = Math.hypot(diff.xCoord, diff.zCoord);
        final float yaw = (float) (Math.atan2(diff.zCoord, diff.xCoord) * 180.0F / PI) - 90.0F;
        final float pitch = (float) (-(Math.atan2(diff.yCoord, distance) * 180.0F / PI));
        return new Vec2(yaw, pitch);
    }

    public static Vec2 calculateRotationToVec(final Vec3 pos) {
        final float deltaX = (float) pos.xCoord;
        final float deltaY = (float) pos.yCoord;
        final float deltaZ = (float) pos.zCoord;
        final float distance = (float) Math.hypot(deltaX, deltaZ);
        float yaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90);
        float pitch = (float) (Math.toDegrees(-Math.atan2(deltaY, distance)));

        return new Vec2(wrapDegrees(yaw), clamp(pitch, -90, 90));
    }

    public static float wrapDegrees(float value) {
        float f = value % 360.0F;

        if (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f < -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    public static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }

    public static double calculateVectorAngle(@NotNull Vec2 a) {
        return Math.atan2(a.getY(), a.getX());
    }

}
