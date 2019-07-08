package org.mz.deepository.lego.builder;

import com.google.inject.util.Providers;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.mz.deepository.lego.builder.configuration.EmbeddedRnn;
import org.mz.deepository.workbench.ExperimentException;
import org.mz.deepository.workbench.GradientListener;
import org.mz.deepository.workbench.LocalExperiment;
import org.mz.deepository.workbench.WeightListener;

public class BasicExperiment {

    public static void main(String[] args) throws IOException, ExperimentException {
        String trainSet = args[0];
        String testSet = args[1];
        String experimentsDir = args[2];
        String generatedBuildingsDir = args[3];

        Buildings trainBuildings = new Buildings(trainSet);
        Buildings testBuildings = new Buildings(testSet);

        Integer[][] trainData = readData(trainBuildings);
        Integer[][] testData = readData(testBuildings);
        System.out.println("Train data size: " + trainData.length);
        System.out.println("Train data total size: " + trainData.length * 200);
        System.out.println("Batch train data size: " + 64 * 200);
        BrickIterator trainDataIterator = new EmbeddingBrickIterator(trainData, 64);
        BrickIterator testDataIterator = new EmbeddingBrickIterator(testData, 64);

        LocalExperiment experiment = new LocalExperiment("EmbeddedRnn", "1.2",
                new EmbeddedRnn(trainDataIterator.inputColumns(), trainDataIterator.totalOutcomes(), 32, 512, 256),
                Providers.of(trainDataIterator), Providers.of(testDataIterator),
                experimentsDir,
                new BuildingPredictorListener(10, 100, trainDataIterator, generatedBuildingsDir),
                new GradientListener(),
                new WeightListener());

        int epochs = Integer.parseInt(args[0]);
        try {
            experiment.run(epochs);
            experiment.evaluate(() -> trainDataIterator);
            experiment.evaluate(() -> testDataIterator);
        } finally {
            experiment.shutdown();
        }
    }

    private static Integer[][] readData(Buildings buildings) throws IOException {
        BuildingCodec codec = new BuildingCodec();
        AtomicInteger counter = new AtomicInteger();
        System.out.println("Reading buildings data: " + buildings);
        Integer[][] trainData = buildings.load().map(building -> codec.encode(building))
                .peek(value -> {
                    int countValue = counter.incrementAndGet();
                    if (countValue % 10000 == 0) {
                        System.out.println("So far read " + countValue + " buildings.");
                    }
                })
                .toArray(size -> new Integer[size][]);
        return trainData;
    }

}
