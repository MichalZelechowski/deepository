package org.mz.deepository.lego.builder;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.mz.deepository.lego.builder.configuration.ConfigurableBasicRnn;

public class ExperimentExplorer {

    public static void main(String[] args) throws IOException {
        ConfigurableBasicRnn basicRnn = new ConfigurableBasicRnn(27, 27, 512, 256);
        basicRnn.summary();

        String modelPath = args[0];
        String datasetPath = args[1];

        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(modelPath);

        System.out.println(model.summary());
        System.out.println(model.score());

        Buildings trainBuildings = new Buildings(datasetPath);

        Integer[][] trainData = readData(trainBuildings);
        System.out.println("Train data size: " + trainData.length);
        System.out.println("Train data total size: " + trainData.length * 200);
        System.out.println("Batch train data size: " + 64 * 200);
        BrickIterator trainDataIterator = new BrickIterator(trainData, 64, new ContinuousWithPaddingDataInitializer());

        DataSetLossCalculator avgLossCalculator = new DataSetLossCalculator(trainDataIterator, true);

        double avgLoss = avgLossCalculator.calculateScore(model);
        System.out.println("Avg loss is " + avgLoss);
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
