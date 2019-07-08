package org.mz.deepository.lego.builder;

import java.util.Objects;

class Brick {

    private final float length;
    private final float x;
    private final float y;
    private final float z;
    private final Orientation orientation;

    public Brick(float length, float x, float y, float z, Orientation orientation) {
        this.length = length;
        this.x = x;
        this.y = y;
        this.z = z;
        this.orientation = orientation;
    }

    float length() {
        return this.length;
    }

    float x() {
        return this.x;
    }

    float z() {
        return this.z;
    }

    float y() {
        return this.y;
    }

    Orientation orientation() {
        return this.orientation;
    }

    Brick translate(float vx, float vy, float vz) {
        return new Brick(length, x + vx, y + vy, z + vz, orientation);
    }

    static enum Orientation {
        EAST, NORTH, WEST, SOUTH;
    }

    public float[] nextPosition() {
        float[] result = new float[]{x, y, z};
        switch (this.orientation()) {
            case EAST:
                result[0] += this.length();
                break;
            case WEST:
                result[0] -= this.length();
                break;
            case NORTH:
                result[2] += this.length();
                break;
            case SOUTH:
                result[2] -= this.length();
                break;
        }
        return result;
    }

    @Override
    public String toString() {
        return "Brick{" + "length=" + length + ", x=" + x + ", y=" + y + ", z=" + z + ", orientation=" + orientation + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Float.floatToIntBits(this.length);
        hash = 41 * hash + Float.floatToIntBits(this.x);
        hash = 41 * hash + Float.floatToIntBits(this.y);
        hash = 41 * hash + Float.floatToIntBits(this.z);
        hash = 41 * hash + Objects.hashCode(this.orientation);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Brick other = (Brick) obj;
        if (Float.floatToIntBits(this.length) != Float.floatToIntBits(other.length)) {
            return false;
        }
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        if (Float.floatToIntBits(this.z) != Float.floatToIntBits(other.z)) {
            return false;
        }
        if (this.orientation != other.orientation) {
            return false;
        }
        return true;
    }

}
