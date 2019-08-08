package org.mz.deepository.lego.mnist;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Option;

public class ExperimentLauncher implements Callable<Integer> {

    @Option(names = {"-t", "--trainSetPath"}, description = "Full path to training set", required = true)
    private String trainSetPath;

    @Option(names = {"-b", "--batchSize"}, description = "Size of the trianing batch", required = false)
    private Integer batchSize = 32;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ExperimentLauncher()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Train set path is " + trainSetPath);
        System.out.println("Batch size is " + batchSize);

        MnistDataSetProvider trainData = new MnistDataSetProvider(this.trainSetPath, true, this.batchSize);

        return 0;
    }

}
