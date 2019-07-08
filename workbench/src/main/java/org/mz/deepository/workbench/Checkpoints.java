package org.mz.deepository.workbench;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.Checkpoint;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.base.Preconditions;

public final class Checkpoints {

    static Checkpoint lastCheckpoint(File checkpointsDir) {
        List<Checkpoint> all = availableCheckpoints(checkpointsDir);
        if (all.isEmpty()) {
            return null;
        }
        return all.get(all.size() - 1);
    }

    public static File getFileForCheckpoint(File rootDir, int checkpointNum) {
        if (checkpointNum < 0) {
            throw new IllegalArgumentException("Invalid checkpoint number: " + checkpointNum);
        }
        File f = null;
        for (String s : new String[]{"MultiLayerNetwork", "ComputationGraph", "Model"}) {
            f = new File(rootDir, getFileName(checkpointNum, s));
            if (f.exists()) {
                return f;
            }
        }
        throw new IllegalStateException("Model file for checkpoint " + checkpointNum + " does not exist");
    }

    private static String getFileName(int checkpointNum, String modelType) {
        return "checkpoint_" + checkpointNum + "_" + modelType + ".zip";
    }

    public static MultiLayerNetwork loadCheckpointMLN(File rootDir, Checkpoint checkpoint) {
        return loadCheckpointMLN(rootDir, checkpoint.getCheckpointNum());
    }

    public static MultiLayerNetwork loadCheckpointMLN(File rootDir, int checkpointNum) {
        File f = getFileForCheckpoint(rootDir, checkpointNum);
        try {
            return ModelSerializer.restoreMultiLayerNetwork(f, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Checkpoint> availableCheckpoints(File directory) {
        File checkpointRecordFile = new File(directory, "checkpointInfo.txt");
        Preconditions.checkState(checkpointRecordFile.exists(), "Could not find checkpoint record file at expected path %s",
                checkpointRecordFile.getAbsolutePath());
        List<String> lines;
        try (final InputStream is = new BufferedInputStream(new FileInputStream(checkpointRecordFile))) {
            lines = IOUtils.readLines(is, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Error loading checkpoint data from file: " + checkpointRecordFile.getAbsolutePath(), e);
        }
        List<Checkpoint> out = new ArrayList<>(lines.size() - 1); //Assume first line is header
        for (int i = 1; i < lines.size(); i++) {
            Checkpoint c = Checkpoint.fromFileString(lines.get(i));
            if (new File(directory, c.getFilename()).exists()) {
                out.add(c);
            }
        }
        return out;
    }

}
