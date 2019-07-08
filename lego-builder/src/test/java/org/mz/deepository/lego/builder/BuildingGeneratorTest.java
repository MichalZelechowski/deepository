package org.mz.deepository.lego.builder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class BuildingGeneratorTest {

    @Test
    public void testIfCanGenerate100Buildings() {
        //setup
        BuildingGenerator objectUnderTest = new BuildingGenerator(1);
        //examine
        Stream<Building> result = objectUnderTest.generate(100);
        //verify
        assertThat(result.toArray()).hasSize(100);
    }

    @Test
    public void testIfBuildingsAreDumped() throws IOException {
        //setup
        BuildingGenerator generator = new BuildingGenerator(2);
        List<Building> generatedData = generator.generate(100).collect(Collectors.toList());
        Buildings buildings = new Buildings(File.createTempFile("buildings", "").getAbsolutePath());
        //examine
        buildings.store(generatedData.stream());
        Stream<Building> readData = buildings.load();
        //verify
        assertThat(readData.collect(Collectors.toList())).isEqualTo(generatedData);
    }

}
