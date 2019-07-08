package org.mz.deepository.lego.builder;

import java.util.Random;

class BuildingBuilder {

    Building build(int x, int y, int z) {
        OutlineBuilder outlineBuilder = new OutlineBuilder(new Random());

        Building building = new Building();
        for (int i = 0; i < y; i++) {
            Outline outline = outlineBuilder.build(x, z);
            outline.translate(0.0f, (float) i, 0.0f);
            building.add(outline);
        }
        return building;
    }

}
