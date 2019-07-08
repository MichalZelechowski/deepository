package org.mz.deepository.lego.builder;

import java.util.Random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.mz.deepository.lego.builder.Brick.Orientation.*;

public class OutlineBuilderTest {

    @org.junit.Test
    public void testBuildOutline() {
        //setup
        Random random = Mockito.mock(Random.class);
        when(random.nextInt(any(Integer.class))).thenReturn(2, 2, 1, 1, 2, 3, 3, 1, 1, 0);
        OutlineBuilder outlineBuilder = new OutlineBuilder(random);

        //examine
        Outline outline = outlineBuilder.build(9, 6f);
        //verify
        assertThat(outline.bricks()).hasSize(10);
        assertThat(outline.brick(0).length()).isEqualTo(3.0f);
        assertThat(outline.brick(0).x()).isEqualTo(0.0f);
        assertThat(outline.brick(0).z()).isEqualTo(0.0f);
        assertThat(outline.brick(0).orientation()).isEqualTo(EAST);
        assertThat(outline.brick(1).length()).isEqualTo(3.0f);
        assertThat(outline.brick(1).x()).isEqualTo(3.0f);
        assertThat(outline.brick(1).z()).isEqualTo(0.0f);
        assertThat(outline.brick(1).orientation()).isEqualTo(EAST);
        assertThat(outline.brick(2).length()).isEqualTo(2.0f);
        assertThat(outline.brick(2).x()).isEqualTo(6.0f);
        assertThat(outline.brick(2).z()).isEqualTo(0.0f);
        assertThat(outline.brick(2).orientation()).isEqualTo(EAST);
        assertThat(outline.brick(3).length()).isEqualTo(2.0f);
        assertThat(outline.brick(3).x()).isEqualTo(8.0f);
        assertThat(outline.brick(3).z()).isEqualTo(0.0f);
        assertThat(outline.brick(3).orientation()).isEqualTo(NORTH);
        assertThat(outline.brick(4).length()).isEqualTo(3.0f);
        assertThat(outline.brick(4).x()).isEqualTo(8.0f);
        assertThat(outline.brick(4).z()).isEqualTo(2.0f);
        assertThat(outline.brick(4).orientation()).isEqualTo(NORTH);
        assertThat(outline.brick(5).length()).isEqualTo(4.0f);
        assertThat(outline.brick(5).x()).isEqualTo(8.0f);
        assertThat(outline.brick(5).z()).isEqualTo(5.0f);
        assertThat(outline.brick(5).orientation()).isEqualTo(WEST);
        assertThat(outline.brick(6).length()).isEqualTo(4.0f);
        assertThat(outline.brick(6).x()).isEqualTo(4.0f);
        assertThat(outline.brick(6).z()).isEqualTo(5.0f);
        assertThat(outline.brick(6).orientation()).isEqualTo(WEST);
        assertThat(outline.brick(7).length()).isEqualTo(2.0f);
        assertThat(outline.brick(7).x()).isEqualTo(0.0f);
        assertThat(outline.brick(7).z()).isEqualTo(5.0f);
        assertThat(outline.brick(7).orientation()).isEqualTo(SOUTH);
        assertThat(outline.brick(8).length()).isEqualTo(2.0f);
        assertThat(outline.brick(8).x()).isEqualTo(0.0f);
        assertThat(outline.brick(8).z()).isEqualTo(3.0f);
        assertThat(outline.brick(8).orientation()).isEqualTo(SOUTH);
        assertThat(outline.brick(9).length()).isEqualTo(1.0f);
        assertThat(outline.brick(9).x()).isEqualTo(0.0f);
        assertThat(outline.brick(9).z()).isEqualTo(1.0f);
        assertThat(outline.brick(9).orientation()).isEqualTo(SOUTH);
    }

    @org.junit.Test
    public void testPovrayOutput() {
        // setup
        Random random = Mockito.mock(Random.class);
        when(random.nextInt(any(Integer.class))).thenReturn(2, 2, 1, 1, 2, 3, 3, 1, 1, 0);
        OutlineBuilder outlineBuilder = new OutlineBuilder(random);
        Outline outline = outlineBuilder.build(9, 6f);

        PovrayExporter exporter = new PovrayExporter();
        //examine
        String povrayStatement = exporter.export(outline);
        //verify
        String manuallyTypedStatement = "  object { Brick_3x1 texture { medium_stone_grey } east() brick_translate(<0.0, 0.0, 0.0>) }\n"
                + "  object { Brick_3x1 texture { reddish_brown } east() brick_translate(<3.0, 0.0, 0.0>) }\n"
                + "  object { Brick_2x1 texture { black } east() brick_translate(<6.0, 0.0, 0.0>) }\n"
                + "  object { Brick_2x1 texture { bright_red } north() brick_translate(<8.0, 0.0, 0.0>) }\n"
                + "  object { Brick_3x1 texture { bright_yellow } north() brick_translate(<8.0, 0.0, 2.0>) }\n"
                + "  object { Brick_4x1 texture { dark_green } west() brick_translate(<8.0, 0.0, 5.0>) }\n"
                + "  object { Brick_4x1 texture { bright_green } west() brick_translate(<4.0, 0.0, 5.0>) }\n"
                + "  object { Brick_2x1 texture { yellowish_green } south() brick_translate(<0.0, 0.0, 5.0>) }\n"
                + "  object { Brick_2x1 texture { sand_yellow } south() brick_translate(<0.0, 0.0, 3.0>) }\n"
                + "  object { Brick_1x1 texture { dark_stone_grey } south() brick_translate(<0.0, 0.0, 1.0>) }\n";
        assertThat(povrayStatement).isEqualTo(manuallyTypedStatement);
    }

    @org.junit.Test
    public void testBuildingBuild() {
        //setup
        BuildingBuilder buildingBuilder = new BuildingBuilder();
        //examine
        Building building = buildingBuilder.build(9, 6, 5);
        //verify
        for (Outline outline : building.outlines()) {
//            assertClosed(outline);
        }
        PovrayExporter exporter = new PovrayExporter();
        System.out.println(exporter.export(building));
    }

}
