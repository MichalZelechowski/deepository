package org.mz.deepository.lego.mnist;

import java.io.File;
import java.util.concurrent.Callable;
import javax.inject.Provider;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.mz.deepository.workbench.ExperimentSetupException;
import org.mz.deepository.workbench.LocalExperiment;
import picocli.CommandLine;
import picocli.CommandLine.Option;

public class ExperimentLauncher implements Callable<Integer> {

    @Option(names = {"-d", "--dataSetPath"}, description = "Full path to data set", required = true)
    private String dataSetPath;

    @Option(names = {"-m", "--modelName"}, description = "Name of the model", required = true)
    private String modelName;

    @Option(names = {"-v", "--version"}, description = "Version of the model", required = true)
    private String version;

    @Option(names = {"-p", "--experimentPath"}, description = "Path where all experiment data is stored", required = true)
    private String experimentPath;

    @Option(names = {"-c", "--configuration"}, description = "Configuration name, equal to class name", required = true)
    private String configuration;

    @Option(names = {"-b", "--batchSize"}, description = "Size of the trianing batch", required = false)
    private Integer batchSize = 32;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ExperimentLauncher()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Train set path is " + dataSetPath);
        System.out.println("Batch size is " + batchSize);

        MnistDataSetProvider trainDataProvider = new MnistDataSetProvider(this.dataSetPath + File.separator + "train", true, this.batchSize, 64);
        MnistDataSetProvider testDataProvider = new MnistDataSetProvider(this.dataSetPath + File.separator + "test", true, this.batchSize, 64);

        Provider<MultiLayerConfiguration> configurationProvider = loadConfiguration(configuration);

        LocalExperiment experiment = new LocalExperiment(this.modelName, this.version, configurationProvider, trainDataProvider,
                testDataProvider, this.experimentPath);
        experiment.setupListenerFrequencies(1, 10, 10, 1, 10);

        experiment.run(20);

        experiment.evaluate(trainDataProvider);
        experiment.evaluate(testDataProvider);

        return 0;
    }

    private Provider<MultiLayerConfiguration> loadConfiguration(String configuration) throws ExperimentSetupException {
        try {
            return (Provider) Class.forName(configuration).newInstance();
        } catch (Exception ex) {
            throw new ExperimentSetupException("Cannot find configuration at " + configuration, ex);
        }
    }

}
