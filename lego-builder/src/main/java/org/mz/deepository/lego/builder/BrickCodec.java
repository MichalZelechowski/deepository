package org.mz.deepository.lego.builder;

import org.mz.deepository.lego.builder.Brick.Orientation;
import org.mz.deepository.workbench.Codec;

public class BrickCodec implements Codec<Brick, Integer> {

    private static final int RESERVED = 100;

    @Override
    public Integer encode(Brick brick) {
        int result = RESERVED;

        result += brick.length() * 4;
        result += brick.orientation().ordinal();

        return result;
    }

    @Override
    public Brick decode(Integer value) {
        value -= RESERVED;

        final int orientationIndex = value % 4;
        final int size = value / 4;
        Brick brick = new Brick(size, 0, 0, 0, Orientation.values()[orientationIndex]);

        return brick;
    }
}
