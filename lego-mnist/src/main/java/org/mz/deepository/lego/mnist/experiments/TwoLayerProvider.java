package org.mz.deepository.lego.mnist.experiments;

import javax.inject.Provider;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.mz.deepository.workbench.GlobalRandom;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class TwoLayerProvider implements Provider<MultiLayerConfiguration> {

    @Override
    public MultiLayerConfiguration get() {
        float rate = 0.01f;
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(GlobalRandom.getSeed())
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(rate))
                .l2(rate * 0.005)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(64 * 64 * 3)
                        .nOut(500)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(500)
                        .nOut(100)
                        .build())
                .layer(new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(100)
                        .nOut(10)
                        .build())
                .build();
        return configuration;
    }

}
