package org.mz.deepository.lego.builder;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class BuildingCodecTest {

    @Test
    public void testEncodeBuilding() {
        //setup
        BuildingCodec codec = new BuildingCodec();
        Building source = new Building();
        source
                .add(new Outline()
                        .add(new Brick(2, 0, 0, 0, Brick.Orientation.SOUTH))
                        .add(new Brick(3, 0, 0, 0, Brick.Orientation.SOUTH))
                        .add(new Brick(1, 0, 0, 0, Brick.Orientation.NORTH))
                )
                .add(new Outline()
                        .add(new Brick(4, 0, 0, 0, Brick.Orientation.EAST))
                        .add(new Brick(5, 0, 0, 0, Brick.Orientation.EAST))
                        .add(new Brick(6, 0, 0, 0, Brick.Orientation.WEST))
                );
        //examine
        Integer[] target = codec.encode(source);
        //verify
        assertThat(target).containsExactly(111, 115, 105, 0, 116, 120, 126, 1);
    }

    @Test
    public void testDecodeBuilding() {
        //setup
        Integer[] target = new Integer[]{111, 115, 105, 0, 116, 120, 126, 1};

        BuildingCodec codec = new BuildingCodec();
        Building expectedBuilding = new Building();
        expectedBuilding
                .add(new Outline()
                        .add(new Brick(2, 0, 0, 0, Brick.Orientation.SOUTH))
                        .add(new Brick(3, 0, 0, -2, Brick.Orientation.SOUTH))
                        .add(new Brick(1, 0, 0, -5, Brick.Orientation.NORTH))
                )
                .add(new Outline()
                        .add(new Brick(4, 0, 1, -4, Brick.Orientation.EAST))
                        .add(new Brick(5, 4, 1, -4, Brick.Orientation.EAST))
                        .add(new Brick(6, 9, 1, -4, Brick.Orientation.WEST))
                );
        //examine
        Building source = codec.decode(target);
        //verify
        assertThat(source).isEqualTo(expectedBuilding);
    }

}
