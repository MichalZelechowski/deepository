package org.mz.deepository.lego.builder;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class BuildingRecognizerTest {

    private BuildingRecognizer objectUnderTest = new BuildingRecognizer();

    @Test
    public void testRecognizeEmptySet() {
        //setup
        Integer[] encoded = new Integer[]{};
        //examine
        Building[] result = objectUnderTest.recognize(encoded);
        //verify
        assertThat(result).isEmpty();
    }

    @Test
    public void testRecognizeEmptySetWithOnes() {
        //setup
        Integer[] encoded = new Integer[]{1, 1};
        //examine
        Building[] result = objectUnderTest.recognize(encoded);
        //verify
        assertThat(result).isEmpty();
    }

    @Test
    public void testRecognizeEmptySetWithSpecialValues() {
        //setup
        Integer[] encoded = new Integer[]{0, 1, 0, -1, 0, 1};
        //examine
        Building[] result = objectUnderTest.recognize(encoded);
        //verify
        assertThat(result).isEmpty();
    }

    @Test
    public void testSkipInvalidBuildingEnd() {
        //setup
        Integer[] encoded = new Integer[]{104, -1, 108};
        //examine
        Building[] result = objectUnderTest.recognize(encoded);
        //verify
        assertThat(result).isEmpty();
    }

    @Test
    public void testSkipUnfinishedBuilding() {
        //setup
        Integer[] encoded = new Integer[]{104, 108, 0};
        //examine
        Building[] result = objectUnderTest.recognize(encoded);
        //verify
        assertThat(result).isEmpty();
    }

    @Test
    public void testRecognizeBuilding() {
        //setup
        Integer[] encoded = new Integer[]{104, 105, 106, 107, 1, -1, -1};
        //examine
        Building[] result = objectUnderTest.recognize(encoded);
        //verify
        Building expectedBuilding = new Building();
        expectedBuilding
                .add(new Outline()
                        .add(new Brick(1, 0, 0, 0, Brick.Orientation.EAST))
                        .add(new Brick(1, 1, 0, 0, Brick.Orientation.NORTH))
                        .add(new Brick(1, 1, 0, 1, Brick.Orientation.WEST))
                        .add(new Brick(1, 0, 0, 1, Brick.Orientation.SOUTH))
                );
        assertThat(result).hasSize(1);
        assertThat(result[0]).isEqualTo(expectedBuilding);
    }

    @Test
    public void testRecognizeBuildings() {
        //setup
        Integer[] encoded = new Integer[]{0, -1, 104, 105, 106, 107, 0, 104, 105, 106, 107, 1, -1, 108, 109, 110, 111, 0, 108, 109, 110, 111, 1, -1};
        //examine
        Building[] result = objectUnderTest.recognize(encoded);
        //verify
        Building firstExpectedBuilding = new Building();
        firstExpectedBuilding
                .add(new Outline()
                        .add(new Brick(1, 0, 0, 0, Brick.Orientation.EAST))
                        .add(new Brick(1, 1, 0, 0, Brick.Orientation.NORTH))
                        .add(new Brick(1, 1, 0, 1, Brick.Orientation.WEST))
                        .add(new Brick(1, 0, 0, 1, Brick.Orientation.SOUTH))
                )
                .add(new Outline()
                        .add(new Brick(1, 0, 1, 0, Brick.Orientation.EAST))
                        .add(new Brick(1, 1, 1, 0, Brick.Orientation.NORTH))
                        .add(new Brick(1, 1, 1, 1, Brick.Orientation.WEST))
                        .add(new Brick(1, 0, 1, 1, Brick.Orientation.SOUTH))
                );
        Building secondExpectedBuilding = new Building();
        secondExpectedBuilding
                .add(new Outline()
                        .add(new Brick(2, 0, 0, 0, Brick.Orientation.EAST))
                        .add(new Brick(2, 2, 0, 0, Brick.Orientation.NORTH))
                        .add(new Brick(2, 2, 0, 2, Brick.Orientation.WEST))
                        .add(new Brick(2, 0, 0, 2, Brick.Orientation.SOUTH))
                )
                .add(new Outline()
                        .add(new Brick(2, 0, 1, 0, Brick.Orientation.EAST))
                        .add(new Brick(2, 2, 1, 0, Brick.Orientation.NORTH))
                        .add(new Brick(2, 2, 1, 2, Brick.Orientation.WEST))
                        .add(new Brick(2, 0, 1, 2, Brick.Orientation.SOUTH))
                );
        assertThat(result).hasSize(2);
        assertThat(result[0]).isEqualTo(firstExpectedBuilding);
        assertThat(result[1]).isEqualTo(secondExpectedBuilding);
    }

    @Test
    public void testRecognizeBuildingWithEmptyFloor() {
        //setup
        Integer[] encoded = new Integer[]{104, 105, 106, 107, 0, 0, 104, 105, 106, 107, 1, -1, -1};
        //examine
        Building[] result = objectUnderTest.recognize(encoded);
        //verify
        Building expectedBuilding = new Building();
        expectedBuilding
                .add(new Outline()
                        .add(new Brick(1, 0, 0, 0, Brick.Orientation.EAST))
                        .add(new Brick(1, 1, 0, 0, Brick.Orientation.NORTH))
                        .add(new Brick(1, 1, 0, 1, Brick.Orientation.WEST))
                        .add(new Brick(1, 0, 0, 1, Brick.Orientation.SOUTH))
                )
                .add(new Outline())
                .add(new Outline()
                        .add(new Brick(1, 0, 2, 0, Brick.Orientation.EAST))
                        .add(new Brick(1, 1, 2, 0, Brick.Orientation.NORTH))
                        .add(new Brick(1, 1, 2, 1, Brick.Orientation.WEST))
                        .add(new Brick(1, 0, 2, 1, Brick.Orientation.SOUTH))
                );
        assertThat(result).hasSize(1);
        assertThat(result[0]).isEqualTo(expectedBuilding);
    }

}
