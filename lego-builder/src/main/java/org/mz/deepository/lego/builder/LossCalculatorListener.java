package org.mz.deepository.lego.builder;

import javax.inject.Provider;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.nn.api.Model;
import org.mz.deepository.workbench.ExperimentListener;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LossCalculatorListener extends ExperimentListener<Model> {

    private static final Logger LOG = LoggerFactory.getLogger(LossCalculatorListener.class);

    private final Provider<DataSetIterator> dataset;
    private final int everyIteration;

    public LossCalculatorListener(Provider<DataSetIterator> dataset, int everyIteration) {
        this.dataset = dataset;
        this.everyIteration = everyIteration;
    }

    @Override
    public void onIteration(Model model, int iteration, int epoch) {
        if (iteration % everyIteration == 0) {
            try {
                DataSetLossCalculator lossCalculator = new DataSetLossCalculator(dataset.get(), true);
                double datasetScore = lossCalculator.calculateScore(model);
                LOG.info("Dataset average loss is: {}", datasetScore);
            } catch (RuntimeException e) {
                LOG.error("Error when calculating loss on dataset", e);
            }
        }
    }

}
