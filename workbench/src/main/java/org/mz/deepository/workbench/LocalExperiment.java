package org.mz.deepository.workbench;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import javax.inject.Provider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.deeplearning4j.nn.api.NeuralNetwork;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.Checkpoint;
import org.deeplearning4j.optimize.listeners.CheckpointListener;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.stats.impl.DefaultStatsUpdateConfiguration;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalExperiment implements Experiment {

    private static final Logger LOG = LoggerFactory.getLogger(LocalExperiment.class);
    final String modelName;
    final String version;
    final Provider<MultiLayerConfiguration> configurationProvider;
    final Provider<DataSetIterator> data;
    private final Provider<DataSetIterator> testData;
    final String rootPath;
    final ExperimentListener[] listeners;
    FileStatsStorage fileStats;
    private final UIServer uiServer;

    private NeuralNetwork network;

    private Integer testLossFrequency;
    private Integer statsFrequency;
    private Integer checkpointFrequency;
    private Integer performanceFrequency;
    private Integer scoreFrequency;

    public LocalExperiment(String modelName, String version, Provider<MultiLayerConfiguration> configurationProvider,
            Provider<DataSetIterator> trainData, Provider<DataSetIterator> testData,
            String rootPath, ExperimentListener... listeners) {
        this.modelName = modelName;
        this.version = version;
        this.configurationProvider = configurationProvider;
        this.data = trainData;
        this.testData = testData;
        this.rootPath = rootPath;
        this.listeners = ObjectUtils.firstNonNull(listeners, new ExperimentListener[0]);
        this.setupListenerFrequencies(null, null, null, null, null);
        this.uiServer = UIServer.getInstance();
    }

    @Override
    public NeuralNetwork run(int epochs) throws ExperimentException {
        this.setupLogger();
        final Path experimentPath = experimentPath();
        LOG.info("Running experiment in path {}", experimentPath);
        try {
            createDirectories();
        } catch (IOException ex) {
            throw new ExperimentSetupException("create basic directories", ex);
        }
        MultiLayerNetwork network = prepareNetwork();
        network.init();
        LOG.info(network.summary());

        if (epochs != 0) {
            final DataSetIterator dataSetIterator = data.get();
            network.fit(dataSetIterator, epochs);
        }

        this.network = network;
        return network;
    }

    public void setupListenerFrequencies(Integer scoreFrequency, Integer performanceFrequency, Integer checkpointFrequency,
            Integer statsFrequency, Integer testLossFrequency) {
        this.scoreFrequency = Optional.ofNullable(scoreFrequency).orElse(10);
        this.performanceFrequency = Optional.ofNullable(performanceFrequency).orElse(100);
        this.checkpointFrequency = Optional.ofNullable(checkpointFrequency).orElse(100);
        this.statsFrequency = Optional.ofNullable(statsFrequency).orElse(10);
        this.testLossFrequency = Optional.ofNullable(testLossFrequency).orElse(100);
    }

    void setupLogger() {
        LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder logEncoder = new PatternLayoutEncoder();
        logEncoder.setContext(logCtx);
        logEncoder.setPattern("%-12date{YYYY-MM-dd HH:mm:ss.SSS} %-5level - %msg%n");
        logEncoder.start();
        FileAppender logFileAppender = new FileAppender();
        logFileAppender.setContext(logCtx);
        logFileAppender.setName("logFile");
        logFileAppender.setEncoder(logEncoder);
        logFileAppender.setAppend(true);
        logFileAppender.setFile(logsPath().resolve("training.log").toString());
        logFileAppender.start();
        ch.qos.logback.classic.Logger log = logCtx.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        log.addAppender(logFileAppender);
    }

    private Path experimentPath() {
        return Paths.get(rootPath, modelName, version);
    }

    private void createDirectories() throws IOException {
        Files.createDirectories(modelsPath());
        Files.createDirectories(logsPath());
        Files.createDirectories(configurationPath());
        Files.createDirectories(historyPath());
    }

    private Path logsPath() {
        return experimentPath().resolve("logs");
    }

    private Path configurationPath() {
        return experimentPath().resolve("configuration");
    }

    private Path modelsPath() {
        return experimentPath().resolve("models");
    }

    private Path historyPath() {
        return experimentPath().resolve("history");
    }

    private MultiLayerNetwork prepareNetwork() throws ExperimentSetupException {
        if (isContinuation()) {
            try {
                LOG.info("Continuation found");
                Checkpoint lastCheckpoint = Checkpoints.lastCheckpoint(modelsPath().toFile());
                MultiLayerNetwork lastModel = Checkpoints.loadCheckpointMLN(modelsPath().toFile(), lastCheckpoint);
                lastModel.setEpochCount(lastCheckpoint.getEpoch());
                lastModel.setIterationCount(lastCheckpoint.getIteration());
                final Path checkpointPath = historyPath().resolve(Long.toString(lastCheckpoint.getTimestamp()));

                final Path bestModelPath = modelsPath().resolve("bestModel.zip");
                double bestScore = readBestScore(bestModelPath);
                BestModelListener bestModelListener = new BestModelListener(bestModelPath, bestScore);

                Files.createDirectories(checkpointPath);
                Files.move(modelsPath(), checkpointPath, StandardCopyOption.ATOMIC_MOVE);
                Files.createDirectories(modelsPath());
                this.prepareNetworkListeners(lastModel);
                lastModel.addListeners(bestModelListener);
                return lastModel;
            } catch (IllegalStateException isex) {
                throw new ExperimentSetupException(isex.getMessage(), isex);
            } catch (IOException ex) {
                throw new ExperimentSetupException(ex.getMessage(), ex);
            }
        } else {
            LOG.info("New model");
            final MultiLayerConfiguration configuration = configurationProvider.get();
            try {
                FileUtils.write(configurationPath().resolve("network.json").toFile(), configuration.toJson(), "UTF-8");
                FileUtils.write(configurationPath().resolve("network.txt").toFile(), configuration.toString(), "UTF-8");
                MultiLayerNetwork network = new MultiLayerNetwork(configuration);
                this.prepareNetworkListeners(network);

                final Path bestModelPath = modelsPath().resolve("bestModel.zip");
                BestModelListener bestModelListener = new BestModelListener(bestModelPath, Float.POSITIVE_INFINITY);
                network.addListeners(bestModelListener);
                return network;
            } catch (IOException ex) {
                throw new ExperimentSetupException("network configuration", ex);
            }
        }
    }

    void prepareNetworkListeners(MultiLayerNetwork network) {
        final Path stats = logsPath().resolve("statistics.bin");
        LOG.info("Storing stats in {}", stats);
        this.fileStats = new FileStatsStorage(stats.toFile());
        uiServer.attach(this.fileStats);

        network.addListeners(
                new ScoreIterationListener(this.scoreFrequency),
                new PerformanceListener(this.performanceFrequency, false),
                new CheckpointListener.Builder(modelsPath().toFile()).saveEveryNIterations(this.checkpointFrequency).keepLastAndEvery(100, 10).
                        deleteExisting(true).build(),
                new StatsListener(this.fileStats, null,
                        new DefaultStatsUpdateConfiguration.Builder().reportingFrequency(this.statsFrequency).build(),
                        "Training", "master"),
                new LossCalculatorListener(testData, this.testLossFrequency)
        );
        network.addListeners(listeners);
    }

    private double readBestScore(final Path bestModelPath) {
        double bestScore = Double.POSITIVE_INFINITY;
        try {
            bestScore = Double.parseDouble(new String(Files.readAllBytes(bestModelPath.resolveSibling("bestModel.score")), Charset.defaultCharset()));
        } catch (Exception ex) {
            LOG.info("No best score, probably fresh training");
        }
        return bestScore;
    }

    private boolean isContinuation() throws ExperimentSetupException {
        try {
            Checkpoints.availableCheckpoints(modelsPath().toFile());
            return true;
        } catch (IllegalStateException ex) {
            LOG.info("What?", ex);
            return false;
        }
    }

    @Override
    public void shutdown() {
        if (fileStats != null) {
            this.fileStats.close();
        }
        if (uiServer != null) {
            this.uiServer.stop();
        }
    }

    public void evaluate(Provider<DataSetIterator> data) {
        Evaluation eval = this.network.doEvaluation(data.get(), new Evaluation())[0];
        LOG.info(eval.stats());
    }

}
