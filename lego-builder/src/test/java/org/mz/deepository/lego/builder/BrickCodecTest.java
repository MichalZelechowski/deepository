package org.mz.deepository.lego.builder;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class BrickCodecTest {

    @Test
    public void testBrickEcoding() {
        //setup
        Brick brick = new Brick(5, 1, 2, 3, Brick.Orientation.WEST);
        BrickCodec brickCodec = new BrickCodec();
        //examine
        int encodedValue = brickCodec.encode(brick);
        //verify
        assertThat(encodedValue).isEqualTo(100 + 4 * 5 + 2);
    }

    @Test
    public void testBrickDecoding() {
        //setup
        final int encodedValue = 100 + 4 * 3 + 1;
        BrickCodec brickCodec = new BrickCodec();
        //examine
        Brick brick = brickCodec.decode(encodedValue);
        //verify
        assertThat(brick).isEqualTo(new Brick(3, 0, 0, 0, Brick.Orientation.NORTH));
    }

}
