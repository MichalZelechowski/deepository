package org.mz.deepository.lego.mnist.experiments;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Provider;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.mz.deepository.workbench.GlobalRandom;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.schedule.MapSchedule;
import org.nd4j.linalg.schedule.ScheduleType;

public class SimpleConvolutionProvider implements Provider<MultiLayerConfiguration> {

    @Override
    public MultiLayerConfiguration get() {
        Map<Integer, Double> learningRateSchedule = new HashMap<>();
        learningRateSchedule.put(0, 0.06);
        learningRateSchedule.put(200, 0.05);
        learningRateSchedule.put(600, 0.028);
        learningRateSchedule.put(800, 0.0060);
        learningRateSchedule.put(1000, 0.001);

        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(GlobalRandom.getSeed())
                .l2(0.0005)
                .updater(new Nesterovs(new MapSchedule(ScheduleType.ITERATION, learningRateSchedule)))
                .cudnnAlgoMode(ConvolutionLayer.AlgoMode.PREFER_FASTEST)
                .gradientNormalization(GradientNormalization.ClipL2PerLayer)
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(new ConvolutionLayer.Builder(5, 5)
                        .nIn(3)
                        .stride(1, 1)
                        .nOut(20)
                        .activation(Activation.IDENTITY)
                        .build())
                .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2)
                        .stride(2, 2)
                        .build())
                .layer(new ConvolutionLayer.Builder(5, 5)
                        .stride(1, 1)
                        .nOut(50)
                        .activation(Activation.IDENTITY)
                        .build())
                .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2)
                        .stride(2, 2)
                        .build())
                .layer(new DenseLayer.Builder().activation(Activation.RELU)
                        .nOut(500)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(10)
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutionalFlat(64, 64, 3))
                .build();
        return configuration;
    }

}
