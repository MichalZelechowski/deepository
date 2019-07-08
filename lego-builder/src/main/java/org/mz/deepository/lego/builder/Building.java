package org.mz.deepository.lego.builder;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;

class Building {

    private final List<Outline> outlines = Lists.newLinkedList();

    Building add(Outline outline) {
        outlines.add(outline);
        return this;
    }

    List<Outline> outlines() {
        return this.outlines;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.outlines);
        return hash;
    }

    public boolean isValid() {
        float y = 0.0f;
        boolean first = true;
        float gminX = Float.POSITIVE_INFINITY, gmaxX = Float.NEGATIVE_INFINITY;
        float gminZ = Float.POSITIVE_INFINITY, gmaxZ = Float.NEGATIVE_INFINITY;
        for (Outline outline : outlines) {
            if (!outline.isValid()) {
                return false;
            }

            if (!outline.bricks().isEmpty()) {
                if (outline.bricks().get(0).y() != y) {
                    return false;
                }

                float minX = Float.POSITIVE_INFINITY, maxX = Float.NEGATIVE_INFINITY;
                float minZ = Float.POSITIVE_INFINITY, maxZ = Float.NEGATIVE_INFINITY;
                for (Brick brick : outline.bricks()) {
                    minX = Math.min(minX, brick.x());
                    maxX = Math.max(maxX, brick.x());
                    minZ = Math.min(minZ, brick.z());
                    maxZ = Math.max(maxZ, brick.z());
                }
                if (!first) {
                    if (minX != gminX || minZ != gminZ || maxX != gmaxX || gmaxZ != maxZ) {
                        return false;
                    }
                } else {
                    first = false;
                    gminX = minX;
                    gminZ = minZ;
                    gmaxX = maxX;
                    gmaxZ = maxZ;
                }
            }

            y += 1.0f;
        }
        return true;
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
        final Building other = (Building) obj;
        if (!Objects.equals(this.outlines, other.outlines)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Building{" + "outlines=" + outlines + '}';
    }

}
