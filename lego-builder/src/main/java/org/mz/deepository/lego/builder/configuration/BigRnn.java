package org.mz.deepository.lego.builder.configuration;

import javax.inject.Provider;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class BigRnn implements Provider<MultiLayerConfiguration> {

    private final int inputColumns;
    private final int outputColumns;

    public BigRnn(int inputColumns, int outputColumns) {
        this.inputColumns = inputColumns;
        this.outputColumns = outputColumns;
    }

    @Override
    public MultiLayerConfiguration get() {
        int lstmLayerSize = 1024;
        int tbpttLength = 50;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .cacheMode(CacheMode.DEVICE)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.01))
                .list()
                .layer(0, new LSTM.Builder().nIn(this.inputColumns).nOut(lstmLayerSize)
                        .activation(Activation.TANH).build())
                .layer(1, new LSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
                        .activation(Activation.TANH).build())
                .layer(2, new LSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
                        .activation(Activation.TANH).build())
                .layer(3, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT).activation(Activation.SOFTMAX)
                        .nIn(lstmLayerSize).nOut(this.outputColumns).build())
                .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength).tBPTTBackwardLength(tbpttLength)
                .build();

        return conf;
    }

}
