package org.mz.deepository.lego.builder;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class BuildingTest {

    @Test
    public void testOneFloorBuildingValid() {
        //setup
        Building building = new Building()
                .add(new Outline()
                        .add(new Brick(3, 0, 0, 0, Brick.Orientation.EAST))
                        .add(new Brick(3, 3, 0, 0, Brick.Orientation.NORTH))
                        .add(new Brick(3, 3, 0, 3, Brick.Orientation.WEST))
                        .add(new Brick(3, 0, 0, 3, Brick.Orientation.SOUTH))
                );
        //examine & verify
        assertThat(building.isValid()).isTrue();
    }

    @Test
    public void testUnclosedOutlineIsInvalid() {
        //setup
        Building building = new Building()
                .add(new Outline()
                        .add(new Brick(3, 0, 0, 0, Brick.Orientation.EAST))
                        .add(new Brick(3, 3, 0, 0, Brick.Orientation.NORTH))
                );
        //examine & verify
        assertThat(building.isValid()).isFalse();
    }

    @Test
    public void testTwoFloorBuildingValid() {
        //setup
        Building building = new Building()
                .add(new Outline()
                        .add(new Brick(3, 0, 0, 0, Brick.Orientation.EAST))
                        .add(new Brick(3, 3, 0, 0, Brick.Orientation.NORTH))
                        .add(new Brick(3, 3, 0, 3, Brick.Orientation.WEST))
                        .add(new Brick(3, 0, 0, 3, Brick.Orientation.SOUTH))
                )
                .add(new Outline()
                        .add(new Brick(3, 0, 1, 0, Brick.Orientation.EAST))
                        .add(new Brick(3, 3, 1, 0, Brick.Orientation.NORTH))
                        .add(new Brick(3, 3, 1, 3, Brick.Orientation.WEST))
                        .add(new Brick(3, 0, 1, 3, Brick.Orientation.SOUTH))
                );
        //examine & verify
        assertThat(building.isValid()).isTrue();
    }

    @Test
    public void testNotClosedBuildingInvalid() {
        //setup
        Building building = new Building()
                .add(new Outline()
                        .add(new Brick(3, 0, 0, 0, Brick.Orientation.EAST))
                        .add(new Brick(3, 3, 0, 0, Brick.Orientation.NORTH))
                        .add(new Brick(3, 3, 0, 3, Brick.Orientation.WEST))
                        .add(new Brick(3, 0, 0, 3, Brick.Orientation.WEST))
                );
        //examine & verify
        assertThat(building.isValid()).isFalse();
    }

    @Test
    public void testMissingFloorInvalid() {
        //setup
        Building building = new Building()
                .add(new Outline()
                        .add(new Brick(3, 0, 0, 0, Brick.Orientation.EAST))
                        .add(new Brick(3, 3, 0, 0, Brick.Orientation.NORTH))
                        .add(new Brick(3, 3, 0, 3, Brick.Orientation.WEST))
                        .add(new Brick(3, 0, 0, 3, Brick.Orientation.SOUTH))
                ).add(new Outline()
                        .add(new Brick(3, 0, 2, 0, Brick.Orientation.EAST))
                        .add(new Brick(3, 3, 2, 0, Brick.Orientation.NORTH))
                        .add(new Brick(3, 3, 2, 3, Brick.Orientation.WEST))
                        .add(new Brick(3, 0, 2, 3, Brick.Orientation.SOUTH))
                );
        //examine & verify
        assertThat(building.isValid()).isFalse();
    }

    @Test
    public void testUnequalFloorsInvalid() {
        //setup
        Building building = new Building()
                .add(new Outline()
                        .add(new Brick(3, 0, 0, 0, Brick.Orientation.EAST))
                        .add(new Brick(3, 3, 0, 0, Brick.Orientation.NORTH))
                        .add(new Brick(3, 3, 0, 3, Brick.Orientation.WEST))
                        .add(new Brick(3, 0, 0, 3, Brick.Orientation.SOUTH))
                ).add(new Outline()
                        .add(new Brick(4, 0, 1, 0, Brick.Orientation.EAST))
                        .add(new Brick(4, 4, 1, 0, Brick.Orientation.NORTH))
                        .add(new Brick(4, 4, 1, 4, Brick.Orientation.WEST))
                        .add(new Brick(4, 0, 1, 4, Brick.Orientation.SOUTH))
                );
        //examine & verify
        assertThat(building.isValid()).isFalse();
    }

}
