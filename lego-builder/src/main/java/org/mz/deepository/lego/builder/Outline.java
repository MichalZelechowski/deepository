package org.mz.deepository.lego.builder;

import com.beust.jcommander.internal.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class Outline {

    private List<Brick> bricks = Lists.newArrayList();

    public Outline add(Brick brick) {
        bricks.add(brick);
        return this;
    }

    Brick brick(int i) {
        return this.bricks.get(i);
    }

    List<Brick> bricks() {
        return this.bricks;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.bricks);
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
        final Outline other = (Outline) obj;
        if (!Objects.equals(this.bricks, other.bricks)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Outline{" + "bricks=" + bricks + '}';
    }

    void translate(float vx, float vy, float vz) {
        this.bricks = this.bricks.stream().map(brick -> brick.translate(vx, vy, vz)).collect(Collectors.toList());
    }

    boolean isValid() {
        if (bricks.isEmpty()) {
            return true;
        }

        if (bricks.size() < 4) {
            return false;
        }

        Brick lastBrick = this.bricks.get(this.bricks.size() - 1);
        return Arrays.equals(lastBrick.nextPosition(), new float[]{0.0f, lastBrick.y(), 0.0f});
    }

}
