package org.mz.deepository.workbench;

import org.deeplearning4j.nn.api.NeuralNetwork;

interface Experiment {

    public NeuralNetwork run(int epochs) throws ExperimentException;

    public void shutdown();

}
