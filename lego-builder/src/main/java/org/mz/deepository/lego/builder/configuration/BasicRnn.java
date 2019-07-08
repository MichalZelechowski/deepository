package org.mz.deepository.lego.builder.configuration;

import javax.inject.Provider;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicRnn implements Provider<MultiLayerConfiguration> {

    private static final Logger LOG = LoggerFactory.getLogger(BasicRnn.class);
    private final int inputColumns;
    private final int outputColumns;

    public BasicRnn(int inputColumns, int outputColumns) {
        this.inputColumns = inputColumns;
        this.outputColumns = outputColumns;
    }

    @Override
    public MultiLayerConfiguration get() {
        int lstmLayerSize = 128;
        int tbpttLength = 220;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .cacheMode(CacheMode.DEVICE)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.0005))
                .list()
                .layer(0, new LSTM.Builder().nIn(this.inputColumns).nOut(lstmLayerSize)
                        .activation(Activation.TANH).build())
                .layer(1, new LSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
                        .activation(Activation.TANH).build())
                .layer(2, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT).activation(Activation.SOFTMAX)
                        .nIn(lstmLayerSize).nOut(this.outputColumns)
                        .build())
                .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength).tBPTTBackwardLength(tbpttLength)
                .build();

        return conf;
    }

    public void summary() {
        MultiLayerNetwork network = new MultiLayerNetwork(this.get());
        network.init();
        LOG.info(network.summary());
    }
}
