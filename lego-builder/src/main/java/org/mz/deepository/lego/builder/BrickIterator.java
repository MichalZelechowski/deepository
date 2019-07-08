package org.mz.deepository.lego.builder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrickIterator implements DataSetIterator {

    private static final Logger LOG = LoggerFactory.getLogger(BrickIterator.class);
    protected static final int MAX_LENGTH = 6;
    protected final int minibatchSize;
    protected final Integer[][] data;
    protected final Integer longestInput;
    protected Integer batchOffset = 0;

    public BrickIterator(Integer[][] data, int minibatchSize, BrickDataInitializer initializer) {
        this.minibatchSize = minibatchSize;

        this.longestInput = Stream.of(data).map(array -> array.length).max(Comparators.naturalOrder()).orElse(0);

        this.data = initializer.prepareData(data);
    }

    @Override
    public DataSet next(int num) {
        LOG.debug("Next set from iterator at batch offset {}", this.batchOffset);
        if (data.length == 0) {
            throw new NoSuchElementException();
        }

        int currMinibatchSize = Math.min(num, data.length - this.batchOffset);

        float[][][] input = new float[currMinibatchSize][this.inputColumns()][this.longestInput - 1];
        float[][][] labels = new float[currMinibatchSize][this.inputColumns()][this.longestInput - 1];
        for (int i = 0; i < currMinibatchSize; i++) {
            int instanceIndex = this.batchOffset + i;

            int currCharIdx = this.dataToIndex(data[instanceIndex][0]);
            for (int j = 1; j < this.longestInput; j++) {
                int nextCharIdx = this.dataToIndex(data[instanceIndex][j]);
                input[i][currCharIdx][j - 1] = 1.0f;
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

    @Override
    public int inputColumns() {
        return 1 + 1 + 1 + MAX_LENGTH * 4;
    }

    @Override
    public int totalOutcomes() {
        return this.inputColumns();
    }

    @Override
    public boolean resetSupported() {
        return true;
    }

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void reset() {
        this.batchOffset = 0;
    }

    @Override
    public int batch() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPreProcessor(DataSetPreProcessor preProcessor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DataSetPreProcessor getPreProcessor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getLabels() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasNext() {
        return this.batchOffset < this.data.length;
    }

    @Override
    public DataSet next() {
        return this.next(this.minibatchSize);
    }

    public int dataToIndex(Integer data) {
        if (data == -1) {
            return 0;
        }
        if (data == 0) {
            return 1;
        }
        if (data == 1) {
            return 2;
        }
        int value = data - 100 - 1;
        return value;
    }

    public Integer indexToData(int index) {
        if (index == 0) {
            return -1;
        }
        if (index == 1) {
            return 0;
        }
        if (index == 2) {
            return 1;
        }
        int data = index + 100 + 1;
        return data;
    }

}
