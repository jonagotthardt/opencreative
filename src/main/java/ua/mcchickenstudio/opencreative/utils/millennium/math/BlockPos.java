package ua.mcchickenstudio.opencreative.utils.millennium.math;

import com.google.common.collect.AbstractIterator;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec3;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec3i;

import java.util.Iterator;

public class BlockPos extends Vec3i {

    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    private static final int NUM_X_BITS = 1 + FastMath.calculateLogBaseTwo(FastMath.roundUpToPowerOfTwo(30000000));
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;

    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final int Y_SHIFT = NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;

    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;

    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockPos(double x, double y, double z) {
        super(x, y, z);
    }

    public BlockPos(Vec3 vector) {
        this(vector.xCoord, vector.yCoord, vector.zCoord);
    }

    public BlockPos(Vec3i source) {
        this(source.getX(), source.getY(), source.getZ());
    }

    public static BlockPos fromLong(long serialized) {
        int x = (int) (serialized << (64 - X_SHIFT - NUM_X_BITS) >> (64 - NUM_X_BITS));
        int y = (int) (serialized << (64 - Y_SHIFT - NUM_Y_BITS) >> (64 - NUM_Y_BITS));
        int z = (int) (serialized << (64 - NUM_Z_BITS)         >> (64 - NUM_Z_BITS));
        return new BlockPos(x, y, z);
    }

    public static Iterable<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
        final BlockPos minPos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPos maxPos = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));

        return new Iterable<BlockPos>() {
            public Iterator<BlockPos> iterator() {
                return new AbstractIterator<BlockPos>() {
                    private BlockPos lastReturned = null;

                    @Override
                    protected BlockPos computeNext() {
                        if (this.lastReturned == null) {
                            this.lastReturned = minPos;
                            return this.lastReturned;
                        } else if (this.lastReturned.equals(maxPos)) {
                            return this.endOfData();
                        } else {
                            int currentX = this.lastReturned.getX();
                            int currentY = this.lastReturned.getY();
                            int currentZ = this.lastReturned.getZ();

                            if (currentX < maxPos.getX()) {
                                ++currentX;
                            } else if (currentY < maxPos.getY()) {
                                currentX = minPos.getX();
                                ++currentY;
                            } else if (currentZ < maxPos.getZ()) {
                                currentX = minPos.getX();
                                currentY = minPos.getY();
                                ++currentZ;
                            } else {
                                return this.endOfData();
                            }
                            this.lastReturned = new BlockPos(currentX, currentY, currentZ);
                            return this.lastReturned;
                        }
                    }
                };
            }
        };
    }

    public static Iterable<MutableBlockPos> getAllInBoxMutable(BlockPos from, BlockPos to) {
        final BlockPos minPos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPos maxPos = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));

        return new Iterable<MutableBlockPos>() {
            public Iterator<MutableBlockPos> iterator() {
                return new AbstractIterator<MutableBlockPos>() {
                    private MutableBlockPos currentPos = null;

                    @Override
                    protected MutableBlockPos computeNext() {
                        if (this.currentPos == null) {
                            this.currentPos = new MutableBlockPos(minPos.getX(), minPos.getY(), minPos.getZ());
                            return this.currentPos;
                        } else if (this.currentPos.equals(maxPos)) {
                            return this.endOfData();
                        } else {
                            int nextX = this.currentPos.getX();
                            int nextY = this.currentPos.getY();
                            int nextZ = this.currentPos.getZ();

                            if (nextX < maxPos.getX()) {
                                ++nextX;
                            } else if (nextY < maxPos.getY()) {
                                nextX = minPos.getX();
                                ++nextY;
                            } else if (nextZ < maxPos.getZ()) {
                                nextX = minPos.getX();
                                nextY = minPos.getY();
                                ++nextZ;
                            } else {
                                return this.endOfData();
                            }

                            this.currentPos.setPos(nextX, nextY, nextZ);
                            return this.currentPos;
                        }
                    }
                };
            }
        };
    }

    public BlockPos add(double x, double y, double z) {
        return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPos((double) this.getX() + x, (double) this.getY() + y, (double) this.getZ() + z);
    }

    public BlockPos add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPos add(Vec3i vector) {
        return vector.getX() == 0 && vector.getY() == 0 && vector.getZ() == 0 ? this : new BlockPos(this.getX() + vector.getX(), this.getY() + vector.getY(), this.getZ() + vector.getZ());
    }

    public BlockPos subtract(Vec3i vector) {
        return vector.getX() == 0 && vector.getY() == 0 && vector.getZ() == 0 ? this : new BlockPos(this.getX() - vector.getX(), this.getY() - vector.getY(), this.getZ() - vector.getZ());
    }

    public BlockPos multiply(int factor) {
        if (factor == 1) {
            return this;
        } else {
            return factor == 0 ? ORIGIN : new BlockPos(this.getX() * factor, this.getY() * factor, this.getZ() * factor);
        }
    }

    public BlockPos offsetUp() {
        return this.offset(EnumFacing.UP, 1);
    }

    public BlockPos offsetUp(int n) {
        return this.offset(EnumFacing.UP, n);
    }

    public BlockPos offsetDown() {
        return this.offset(EnumFacing.DOWN, 1);
    }

    public BlockPos offsetDown(int n) {
        return this.offset(EnumFacing.DOWN, n);
    }

    public BlockPos offsetNorth() {
        return this.offset(EnumFacing.NORTH, 1);
    }

    public BlockPos offsetNorth(int n) {
        return this.offset(EnumFacing.NORTH, n);
    }

    public BlockPos offsetSouth() {
        return this.offset(EnumFacing.SOUTH, 1);
    }

    public BlockPos offsetSouth(int n) {
        return this.offset(EnumFacing.SOUTH, n);
    }

    public BlockPos offsetWest() {
        return this.offset(EnumFacing.WEST, 1);
    }

    public BlockPos offsetWest(int n) {
        return this.offset(EnumFacing.WEST, n);
    }

    public BlockPos offsetEast() {
        return this.offset(EnumFacing.EAST, 1);
    }

    public BlockPos offsetEast(int n) {
        return this.offset(EnumFacing.EAST, n);
    }

    public BlockPos offset(EnumFacing facing) {
        return this.offset(facing, 1);
    }

    public BlockPos offset(EnumFacing facing, int n) {
        return n == 0 ? this : new BlockPos(
                        this.getX() + facing.getFrontOffsetX() * n,
                        this.getY() + facing.getFrontOffsetY() * n,
                        this.getZ() + facing.getFrontOffsetZ() * n
        );
    }

    public BlockPos crossProductBP(Vec3i vector) {
        return new BlockPos(
                        this.getY() * vector.getZ() - this.getZ() * vector.getY(),
                        this.getZ() * vector.getX() - this.getX() * vector.getZ(),
                        this.getX() * vector.getY() - this.getY() * vector.getX()
        );
    }

    public long toLong() {
        return ((long) this.getX() & X_MASK) << X_SHIFT |
                        ((long) this.getY() & Y_MASK) << Y_SHIFT |
                        ((long) this.getZ() & Z_MASK);
    }

    @Override
    public BlockPos crossProduct(Vec3i vector) {
        return this.crossProductBP(vector);
    }

    public static final class MutableBlockPos extends BlockPos {
        public int x;
        public int y;
        public int z;

        private MutableBlockPos(int initialX, int initialY, int initialZ) {
            super(0, 0, 0);
            this.x = initialX;
            this.y = initialY;
            this.z = initialZ;
        }

        @Override
        public int getX() {
            return this.x;
        }

        @Override
        public int getY() {
            return this.y;
        }

        @Override
        public int getZ() {
            return this.z;
        }

        public MutableBlockPos setPos(int newX, int newY, int newZ) {
            this.x = newX;
            this.y = newY;
            this.z = newZ;
            return this;
        }

        public MutableBlockPos setPos(double newX, double newY, double newZ) {
            return this.setPos(FastMath.floor(newX), FastMath.floor(newY), FastMath.floor(newZ));
        }

        @Override
        public BlockPos crossProduct(Vec3i vector) {
            return super.crossProductBP(vector);
        }
    }
}