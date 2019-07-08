package org.mz.deepository.lego.builder;

import java.util.Random;
import java.util.stream.Stream;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildingPredictor {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingPredictor.class);
    private final BrickCodec codec = new BrickCodec();
    private final BuildingRecognizer recognizer = new BuildingRecognizer();
    private final Random random = new Random();

    private final int numberOfSamples;
    private final int buildingSize;

    public BuildingPredictor(int numberOfSamples, int buildingSize) {
        this.numberOfSamples = numberOfSamples;
        this.buildingSize = buildingSize;
    }

    public Building[][] predict(MultiLayerNetwork model, BrickIterator iterator, Brick.Orientation orientation) {
        Integer initialization = codec.encode(new Brick(random.nextInt(6) + 1, 0, 0, 0, orientation));

        Integer[][] samples = sampleBricksFromNetwork(initialization, model, iterator, buildingSize, numberOfSamples);

        Building[][] result = Stream.of(samples).map(recognizer::recognize).toArray(size -> new Building[size][]);
        return result;
    }

    public Building[][] predict(MultiLayerNetwork model, BrickIterator iterator) {
        return this.predict(model, iterator, Brick.Orientation.EAST);
    }

    private Integer[][] sampleBricksFromNetwork(Integer initialization, MultiLayerNetwork net,
            BrickIterator iterator, int buildingSize, int numSamples) {
        INDArray initializationInput = Nd4j.zeros(numSamples, iterator.inputColumns(), 1);

        int idx = iterator.dataToIndex(initialization);
        for (int j = 0; j < numSamples; j++) {
            initializationInput.putScalar(new int[]{j, idx, 0}, 1.0f);
        }

        Integer[][] results = new Integer[numSamples][buildingSize];

        net.rnnClearPreviousState();
        INDArray output = net.rnnTimeStep(initializationInput);
        output = output.tensorAlongDimension((int) output.size(2) - 1, 1, 0);	//Gets the last time step output

        for (int i = 0; i < buildingSize; i++) {
            INDArray nextInput = Nd4j.zeros(numSamples, iterator.inputColumns());
            if (i % 10 == 0) {
                LOG.info("Generating building block {}", i);
            }

            for (int s = 0; s < numSamples; s++) {
                Double[] outputProbDistribution = new Double[iterator.totalOutcomes()];
                for (int j = 0; j < outputProbDistribution.length; j++) {
                    outputProbDistribution[j] = output.getDouble(s, j);
                }
                int sampledBrickIdx = sampleFromDistribution(outputProbDistribution);
                nextInput.putScalar(new int[]{s, sampledBrickIdx}, 1.0f);
                results[s][i] = iterator.indexToData(sampledBrickIdx);
            }

            output = net.rnnTimeStep(nextInput);	//Do one time step of forward pass
        }

        return results;
    }

    public int sampleFromDistribution(Double[] distribution) {
        double d = random.nextDouble();
        double sum = 0.0;
        for (int i = 0; i < distribution.length; i++) {
            sum += distribution[i];
            if (d <= sum) {
                return i;
            }
        }
        //Should never happen if distribution is a valid probability distribution
        throw new IllegalArgumentException("Distribution is invalid? d=" + d + ", sum=" + sum);
    }
}
