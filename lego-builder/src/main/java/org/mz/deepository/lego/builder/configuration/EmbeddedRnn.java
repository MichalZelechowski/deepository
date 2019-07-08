package org.mz.deepository.lego.builder.configuration;

import javax.inject.Provider;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.EmbeddingLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedRnn implements Provider<MultiLayerConfiguration> {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedRnn.class);
    private final int inputColumns;
    private final int outputColumns;
    private final int[] layerSizes;
    private final int embeddingSize;

    public EmbeddedRnn(int inputColumns, int outputColumns, int embeddingSize, int... layerSizes) {
        this.inputColumns = inputColumns;
        this.outputColumns = outputColumns;
        this.layerSizes = layerSizes;
        this.embeddingSize = embeddingSize;
    }

    @Override
    public MultiLayerConfiguration get() {
        int tbpttLength = 220;

        NeuralNetConfiguration.ListBuilder builder = new NeuralNetConfiguration.Builder()
                .cacheMode(CacheMode.DEVICE)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.0005))
                .list();
        int index = 1;
        int previousSize = this.embeddingSize;
        builder = builder.layer(0, new EmbeddingLayer.Builder().nIn(this.inputColumns).nOut(this.embeddingSize).build());
        for (int layerSize : layerSizes) {
            builder = builder.layer(index++, new LSTM.Builder().nIn(previousSize).nOut(layerSize)
                    .activation(Activation.TANH).build());
            previousSize = layerSize;
        }

        MultiLayerConfiguration conf = builder.layer(layerSizes.length + 1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT).activation(
                Activation.SOFTMAX)
                .nIn(previousSize).nOut(this.outputColumns)
                .build())
                .inputPreProcessor(0, new RnnToFeedForwardPreProcessor())
                .inputPreProcessor(1, new FeedForwardToRnnPreProcessor())
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
