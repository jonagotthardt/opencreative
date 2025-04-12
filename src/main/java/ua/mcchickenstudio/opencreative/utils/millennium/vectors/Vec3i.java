package ua.mcchickenstudio.opencreative.utils.millennium.vectors;

import lombok.Getter;

@Getter
public class Vec3i implements Comparable<Vec3i> {

    public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);

    private final int x;
    private final int y;
    private final int z;

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3i(double x, double y, double z) {
        this((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vec3i otherVec)) {
            return false;
        }
        return this.getX() == otherVec.getX() &&
                        this.getY() == otherVec.getY() &&
                        this.getZ() == otherVec.getZ();
    }

    @Override
    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    @Override
    public int compareTo(Vec3i otherVec) {
        if (this.getY() != otherVec.getY()) {
            return this.getY() - otherVec.getY();
        } else if (this.getZ() != otherVec.getZ()) {
            return this.getZ() - otherVec.getZ();
        } else {
            return this.getX() - otherVec.getX();
        }
    }

    public Vec3i crossProduct(Vec3i otherVec) {
        return new Vec3i(
                        this.getY() * otherVec.getZ() - this.getZ() * otherVec.getY(),
                        this.getZ() * otherVec.getX() - this.getX() * otherVec.getZ(),
                        this.getX() * otherVec.getY() - this.getY() * otherVec.getX()
        );
    }

    public double distanceSq(double toX, double toY, double toZ) {
        double deltaX = (double) this.getX() - toX;
        double deltaY = (double) this.getY() - toY;
        double deltaZ = (double) this.getZ() - toZ;
        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }

    public double distanceSqToCenter(double targetX, double targetY, double targetZ) {
        double deltaX = (double) this.getX() + 0.5D - targetX;
        double deltaY = (double) this.getY() + 0.5D - targetY;
        double deltaZ = (double) this.getZ() + 0.5D - targetZ;
        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }

    public double distanceSq(Vec3i toVec) {
        return this.distanceSq((double) toVec.getX(), (double) toVec.getY(), (double) toVec.getZ());
    }

    @Override
    public String toString() {
        return "Vec3i{" +
                        "x=" + x +
                        ", y=" + y +
                        ", z=" + z +
                        '}';
    }
}