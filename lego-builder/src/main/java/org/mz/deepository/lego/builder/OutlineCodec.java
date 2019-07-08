package org.mz.deepository.lego.builder;

import org.mz.deepository.workbench.Codec;

public class OutlineCodec implements Codec<Outline, Integer[]> {

    private final BrickCodec delegate = new BrickCodec();

    @Override
    public Outline decode(Integer[] target) {
        Outline outline = new Outline();

        float[] nextPosition = new float[3];
        for (Integer t : target) {
            Brick brick = this.delegate.decode(t).translate(nextPosition[0], nextPosition[1], nextPosition[2]);

            nextPosition = brick.nextPosition();

            outline.add(brick);
        }

        return outline;
    }

    @Override
    public Integer[] encode(Outline source) {
        return source.bricks().stream()
                .map(this.delegate::encode)
                .toArray(Integer[]::new);
    }

}
