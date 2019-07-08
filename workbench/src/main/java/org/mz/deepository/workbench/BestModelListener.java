package org.mz.deepository.workbench;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import org.deeplearning4j.util.ModelSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BestModelListener extends BaseTrainingListener {

    private static final Logger LOG = LoggerFactory.getLogger(BestModelListener.class);
    private double bestScore = Double.POSITIVE_INFINITY;
    private final Path bestModelPath;

    public BestModelListener(Path bestModelPath) {
        this(bestModelPath, Double.POSITIVE_INFINITY);
    }

    public BestModelListener(Path bestModelPath, double bestScore) {
        this.bestModelPath = bestModelPath;
        this.bestScore = bestScore;
    }

    @Override
    public void iterationDone(Model model, int iteration, int epoch) {
        if (model.score() < this.bestScore) {
            try {
                ModelSerializer.writeModel(model, bestModelPath.toFile(), true);
                this.bestScore = model.score();
                Files.write(bestModelPath.resolveSibling("bestModel.score"), Double.toString(this.bestScore).getBytes(), StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE);
            } catch (IOException ex) {
                LOG.error("Cannot write best model", ex);
            }
        }
    }

}
