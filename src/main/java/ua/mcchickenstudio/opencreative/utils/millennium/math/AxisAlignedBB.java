package ua.mcchickenstudio.opencreative.utils.millennium.math;

import lombok.Getter;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec3;

@Getter
public final class AxisAlignedBB {

    public double minX, minY, minZ;
    public double maxX, maxY, maxZ;

    public AxisAlignedBB(AxisAlignedBB bb) {
        this.minX = bb.minX;
        this.minY = bb.minY;
        this.minZ = bb.minZ;
        this.maxX = bb.maxX;
        this.maxY = bb.maxY;
        this.maxZ = bb.maxZ;
    }

    public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public AxisAlignedBB(Vec3 location) {
        this(location.xCoord - 0.3F, location.yCoord, location.zCoord - 0.3F,
                        location.xCoord + 0.3F, location.yCoord + 1.8F, location.zCoord + 0.3F);
    }

    public static AxisAlignedBB fromBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public AxisAlignedBB addCoord(double x, double y, double z) {
        double newMinX = this.minX;
        double newMinY = this.minY;
        double newMinZ = this.minZ;
        double newMaxX = this.maxX;
        double newMaxY = this.maxY;
        double newMaxZ = this.maxZ;

        if (x < 0.0D) newMinX += x;
        else if (x > 0.0D) newMaxX += x;

        if (y < 0.0D) newMinY += y;
        else if (y > 0.0D) newMaxY += y;

        if (z < 0.0D) newMinZ += z;
        else if (z > 0.0D) newMaxZ += z;

        return new AxisAlignedBB(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public AxisAlignedBB expand(double x, double y, double z) {
        return new AxisAlignedBB(minX - x, minY - y, minZ - z, maxX + x, maxY + y, maxZ + z);
    }

    public AxisAlignedBB union(AxisAlignedBB other) {
        return new AxisAlignedBB(
                        Math.min(this.minX, other.minX),
                        Math.min(this.minY, other.minY),
                        Math.min(this.minZ, other.minZ),
                        Math.max(this.maxX, other.maxX),
                        Math.max(this.maxY, other.maxY),
                        Math.max(this.maxZ, other.maxZ)
        );
    }

    public AxisAlignedBB offset(double x, double y, double z) {
        return new AxisAlignedBB(
                        this.minX + x, this.minY + y, this.minZ + z,
                        this.maxX + x, this.maxY + y, this.maxZ + z
        );
    }

    public double calculateXOffset(AxisAlignedBB other, double offsetX) {
        if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
            if (offsetX > 0.0D && other.maxX <= this.minX) {
                double diff = this.minX - other.maxX;
                if (diff < offsetX) offsetX = diff;
            } else if (offsetX < 0.0D && other.minX >= this.maxX) {
                double diff = this.maxX - other.minX;
                if (diff > offsetX) offsetX = diff;
            }
        }
        return offsetX;
    }

    public double calculateYOffset(AxisAlignedBB other, double offsetY) {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ) {
            if (offsetY > 0.0D && other.maxY <= this.minY) {
                double diff = this.minY - other.maxY;
                if (diff < offsetY) offsetY = diff;
            } else if (offsetY < 0.0D && other.minY >= this.maxY) {
                double diff = this.maxY - other.minY;
                if (diff > offsetY) offsetY = diff;
            }
        }
        return offsetY;
    }

    public double calculateZOffset(AxisAlignedBB other, double offsetZ) {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY) {
            if (offsetZ > 0.0D && other.maxZ <= this.minZ) {
                double diff = this.minZ - other.maxZ;
                if (diff < offsetZ) offsetZ = diff;
            } else if (offsetZ < 0.0D && other.minZ >= this.maxZ) {
                double diff = this.maxZ - other.minZ;
                if (diff > offsetZ) offsetZ = diff;
            }
        }
        return offsetZ;
    }

    public boolean intersectsWith(AxisAlignedBB other) {
        return other.maxX > this.minX && other.minX < this.maxX &&
                        other.maxY > this.minY && other.minY < this.maxY &&
                        other.maxZ > this.minZ && other.minZ < this.maxZ;
    }

    public boolean isVecInside(Vec3 vec) {
        return vec.xCoord > this.minX && vec.xCoord < this.maxX &&
                        vec.yCoord > this.minY && vec.yCoord < this.maxY &&
                        vec.zCoord > this.minZ && vec.zCoord < this.maxZ;
    }

    public double getAverageEdgeLength() {
        return ((maxX - minX) + (maxY - minY) + (maxZ - minZ)) / 3.0D;
    }

    public AxisAlignedBB contract(double x, double y, double z) {
        return new AxisAlignedBB(minX + x, minY + y, minZ + z, maxX - x, maxY - y, maxZ - z);
    }

    public MovingObjectPosition calculateIntercept(Vec3 start, Vec3 end) {
        Vec3 hitX1 = start.getIntermediateWithXValue(end, this.minX);
        Vec3 hitX2 = start.getIntermediateWithXValue(end, this.maxX);
        Vec3 hitY1 = start.getIntermediateWithYValue(end, this.minY);
        Vec3 hitY2 = start.getIntermediateWithYValue(end, this.maxY);
        Vec3 hitZ1 = start.getIntermediateWithZValue(end, this.minZ);
        Vec3 hitZ2 = start.getIntermediateWithZValue(end, this.maxZ);

        if (!isVecInYZ(hitX1)) hitX1 = null;
        if (!isVecInYZ(hitX2)) hitX2 = null;
        if (!isVecInXZ(hitY1)) hitY1 = null;
        if (!isVecInXZ(hitY2)) hitY2 = null;
        if (!isVecInXY(hitZ1)) hitZ1 = null;
        if (!isVecInXY(hitZ2)) hitZ2 = null;

        Vec3 closest = null;

        for (Vec3 hit : new Vec3[]{hitX1, hitX2, hitY1, hitY2, hitZ1, hitZ2}) {
            if (hit != null && (closest == null || start.squareDistanceTo(hit) < start.squareDistanceTo(closest))) {
                closest = hit;
            }
        }

        if (closest == null) return null;

        EnumFacing facing;
        if (closest == hitX1) facing = EnumFacing.WEST;
        else if (closest == hitX2) facing = EnumFacing.EAST;
        else if (closest == hitY1) facing = EnumFacing.DOWN;
        else if (closest == hitY2) facing = EnumFacing.UP;
        else if (closest == hitZ1) facing = EnumFacing.NORTH;
        else facing = EnumFacing.SOUTH;

        return new MovingObjectPosition(closest, facing);
    }

    private boolean isVecInYZ(Vec3 vec) {
        return vec != null && vec.yCoord >= minY && vec.yCoord <= maxY && vec.zCoord >= minZ && vec.zCoord <= maxZ;
    }

    private boolean isVecInXZ(Vec3 vec) {
        return vec != null && vec.xCoord >= minX && vec.xCoord <= maxX && vec.zCoord >= minZ && vec.zCoord <= maxZ;
    }

    private boolean isVecInXY(Vec3 vec) {
        return vec != null && vec.xCoord >= minX && vec.xCoord <= maxX && vec.yCoord >= minY && vec.yCoord <= maxY;
    }

    @Override
    public String toString() {
        return "box[" + minX + ", " + minY + ", " + minZ + " -> " + maxX + ", " + maxY + ", " + maxZ + "]";
    }

    public AxisAlignedBB offsetAndUpdate(double x, double y, double z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;
        return this;
    }

    public void copyFrom(AxisAlignedBB bb) {
        this.minX = Math.min(this.minX, bb.minX);
        this.minY = Math.min(this.minY, bb.minY);
        this.minZ = Math.min(this.minZ, bb.minZ);
        this.maxX = Math.max(this.maxX, bb.maxX);
        this.maxY = Math.max(this.maxY, bb.maxY);
        this.maxZ = Math.max(this.maxZ, bb.maxZ);
    }

    public double getEyeHeight() {
        return (maxY - minY) * 0.85F;
    }

    @Override
    public AxisAlignedBB clone() {
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
