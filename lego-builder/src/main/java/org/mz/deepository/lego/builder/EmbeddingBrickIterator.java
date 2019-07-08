package org.mz.deepository.lego.builder;

import java.util.NoSuchElementException;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddingBrickIterator extends BrickIterator {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddingBrickIterator.class);

    public EmbeddingBrickIterator(Integer[][] data, int minibatchSize) {
        super(data, minibatchSize, new ContinuousWithPaddingDataInitializer());
    }

    @Override
    public DataSet next(int num) {
        LOG.debug("Next set from iterator at batch offset {}", this.batchOffset);
        if (data.length == 0) {
            throw new NoSuchElementException();
        }

        int currMinibatchSize = Math.min(num, data.length - this.batchOffset);

        float[][][] input = new float[currMinibatchSize][1][this.longestInput - 1];
        float[][][] labels = new float[currMinibatchSize][this.totalOutcomes()][this.longestInput - 1];
        for (int i = 0; i < currMinibatchSize; i++) {
            int instanceIndex = this.batchOffset + i;

            int currCharIdx = this.dataToIndex(data[instanceIndex][0]);
            for (int j = 1; j < this.longestInput; j++) {
                input[i][0][j - 1] = currCharIdx;
                int nextCharIdx = this.dataToIndex(data[instanceIndex][j]);
                labels[i][nextCharIdx][j - 1] = 1.0f;
                currCharIdx = nextCharIdx;
            }
        }

        this.batchOffset += currMinibatchSize;
        INDArray inputData = Nd4j.create(input);
        inputData.setOrder('f');
        INDArray labelsData = Nd4j.create(labels);
        labelsData.setOrder('f');

        return new DataSet(inputData, labelsData);
    }

}
