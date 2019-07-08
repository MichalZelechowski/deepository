package org.mz.deepository.lego.builder;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;

public class OutlineBuilder {

    private final Random random;

    OutlineBuilder(Random random) {
        this.random = random;
    }

    Outline build(float length, float width) {
        Outline outline = new Outline();

        Float[] a = this.buildSegment(length - 1);
        Float[] b = this.buildSegment(width - 1);
        Float[] c = this.buildSegment(length - 1);
        Float[] d = this.buildSegment(width - 1);

        float x = 0, y = 0, z = 0;
        for (Float segmentLength : a) {
            outline.add(new Brick(segmentLength, x, y, z, Brick.Orientation.EAST));
            x += segmentLength;
        }
        for (Float segmentLength : b) {
            outline.add(new Brick(segmentLength, x, y, z, Brick.Orientation.NORTH));
            z += segmentLength;
        }
        for (Float segmentLength : c) {
            outline.add(new Brick(segmentLength, x, y, z, Brick.Orientation.WEST));
            x -= segmentLength;
        }
        for (Float segmentLength : d) {
            outline.add(new Brick(segmentLength, x, y, z, Brick.Orientation.SOUTH));
            z -= segmentLength;
        }
        return outline;
    }

    private Float[] buildSegment(float size) {
        List<Float> segments = Lists.newLinkedList();
        while (size > 0) {
            int nextBrickSize = this.random.nextInt((int) Math.min(size, 6)) + 1;
            segments.add((float) nextBrickSize);
            size -= nextBrickSize;
        }
        return segments.toArray(new Float[0]);
    }

}
