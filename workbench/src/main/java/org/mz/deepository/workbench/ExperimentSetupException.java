package org.mz.deepository.workbench;

public class ExperimentSetupException extends ExperimentException {

    public ExperimentSetupException(String setupPart, Throwable cause) {
        super("Cannot setup experiment: " + setupPart, cause);
    }

}
