package org.mz.deepository.workbench;

import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExperimentListener<MODEL> extends BaseTrainingListener {

    private static final Logger LOG = LoggerFactory.getLogger(ExperimentListener.class);

    private int failureCounter = 0;

    @Override
    public void iterationDone(Model model, int iteration, int epoch) {
        try {
            this.onIteration((MODEL) model, iteration, epoch);
        } catch (RuntimeException e) {
            LOG.warn("Listener " + this.getClass().getSimpleName() + " failed", e);
            failureCounter++;
            if (failureCounter > 100) {
                throw e;
            }
        }
    }

    public abstract void onIteration(MODEL model, int iteration, int epoch);

}
