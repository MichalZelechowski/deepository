package org.mz.deepository.workbench;

import com.beust.jcommander.internal.Lists;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.inject.Provider;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.text.RandomStringGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import org.deeplearning4j.datasets.iterator.FloatsDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.primitives.Pair;

public class ExperimentTest {

    @Test
    public void testIfExperimentIsExecuted() throws ExperimentException {
        //setup
        Provider<MultiLayerConfiguration> networkProvider = () -> {
            return someNetworkProvider();
        };

        Provider<DataSetIterator> dataProvider = () -> {
            List data = Lists.newArrayList(1000);
            for (int i = 0; i < 1000; i++) {
                final float nextFloat = RandomUtils.nextFloat(0, 1);
                data.add(Pair.of(new float[]{nextFloat, nextFloat + 1}, new float[]{nextFloat + 2}));
            }
            FloatsDataSetIterator iterator = new FloatsDataSetIterator(data, 1);
            return iterator;
        };

        String marker = new RandomStringGenerator.Builder().withinRange(new char[]{'a', 'z'}).build().generate(10);
        Experiment experiment = new LocalExperiment("LEGO model", "basic nn" + marker, networkProvider, dataProvider, dataProvider,
                "/tmp/Experiments");

        //examine
        experiment.run(2);
        //verify
        assertThatModelIsSaved("/tmp/Experiments/LEGO model/basic nn" + marker);
        assertThatConfigurationIsSaved("/tmp/Experiments/LEGO model/basic nn" + marker);
        assertThatLogIsSaved("/tmp/Experiments/LEGO model/basic nn" + marker);
    }

    @Test
    public void testIfExperimentIsContinued() throws ExperimentException {
        //setup
        Provider<MultiLayerConfiguration> networkProvider = () -> {
            return someNetworkProvider();
        };

        Provider<DataSetIterator> dataProvider = () -> {
            List data = Lists.newArrayList(100);
            for (int i = 0; i < 100; i++) {
                final float nextFloat = RandomUtils.nextFloat(0, 1);
                data.add(Pair.of(new float[]{nextFloat, nextFloat + 1}, new float[]{nextFloat + 2}));
            }
            FloatsDataSetIterator iterator = new FloatsDataSetIterator(data, 1);
            return iterator;
        };

        String marker = new RandomStringGenerator.Builder().withinRange(new char[]{'a', 'z'}).build().generate(10);
        Experiment experiment = new LocalExperiment("LEGO model", "basic nn" + marker, networkProvider, dataProvider, dataProvider,
                "/tmp/Experiments");

        //examine
        experiment.run(2);
        experiment.shutdown();

        Experiment experimentContinuation = new LocalExperiment("LEGO model", "basic nn" + marker, networkProvider, dataProvider, dataProvider,
                "/tmp/Experiments");
        experimentContinuation.run(7);
        //verify
        assertThatModelIsSaved("/tmp/Experiments/LEGO model/basic nn" + marker);
        assertThatConfigurationIsSaved("/tmp/Experiments/LEGO model/basic nn" + marker);
        assertThatLogIsSaved("/tmp/Experiments/LEGO model/basic nn" + marker);
    }

    private MultiLayerConfiguration someNetworkProvider() {
        return new NeuralNetConfiguration.Builder()
                .seed(1)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.NESTEROVS.getIUpdaterWithDefaultConfig())
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(2)
                        .nOut(2)
                        .activation(Activation.RELU6)
                        .weightInit(WeightInit.XAVIER_UNIFORM)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.XENT)
                        .nIn(2)
                        .nOut(1)
                        .activation(Activation.SIGMOID)
                        .weightInit(WeightInit.XAVIER_UNIFORM)
                        .build())
                .build();
    }

    private void assertThatModelIsSaved(String path) {
        Path experimentPath = Paths.get(path);
        assertThat(experimentPath).exists();

        Path modelPath = experimentPath.resolve("models");
        assertThat(modelPath).exists();

        Path bestModel = modelPath.resolve("bestModel.zip");
        assertThat(bestModel).exists();

        Path iterationModel = modelPath.resolve("checkpoint_5_MultiLayerNetwork.zip");
        assertThat(iterationModel).exists();
    }

    private void assertThatConfigurationIsSaved(String path) {
        Path experimentPath = Paths.get(path);
        assertThat(experimentPath).exists();

        Path configuration = experimentPath.resolve("configuration");
        assertThat(configuration).exists();

        Path jsonConfiguration = configuration.resolve("network.json");
        assertThat(jsonConfiguration).exists();

        Path txtConfiguration = configuration.resolve("network.txt");
        assertThat(txtConfiguration).exists();
    }

    private void assertThatLogIsSaved(String path) {
        Path experimentPath = Paths.get(path);
        assertThat(experimentPath).exists();

        Path logPath = experimentPath.resolve("logs");
        assertThat(logPath).exists();

        Path trainingLog = logPath.resolve("training.log");
        assertThat(trainingLog).exists();

        Path stats = logPath.resolve("statistics.bin");
        assertThat(stats).exists();
    }

}
