package org.mz.deepository.lego.mnist;

import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import javax.inject.Provider;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.mz.deepository.workbench.GlobalRandom;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.CompositeDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImageFlatteningDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

public class MnistDataSetProvider implements Provider<DataSetIterator> {

    private final String path;
    private final Boolean flat;
    private final Integer batchSize;

    public MnistDataSetProvider(String path, Boolean flat, Integer batchSize) {
        this.path = path;
        this.flat = flat;
        this.batchSize = batchSize;
    }

    @Override
    public DataSetIterator get() {
        try {
            File dataFile = new File(this.path);

            FileSplit dataSplit = new FileSplit(dataFile, NativeImageLoader.ALLOWED_FORMATS, GlobalRandom.getRandom());

            ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
            ImageRecordReader recordReader = new ImageRecordReader(512, 512, 3, labelMaker);

            recordReader.initialize(dataSplit);

            DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, 1, 10);

            iterator.setPreProcessor(this.getPreProcessor(iterator));

            return iterator;
        } catch (IOException exception) {
            throw Throwables.propagate(exception);
        }
    }

    private DataSetPreProcessor getPreProcessor(DataSetIterator trainIter) {
        DataNormalization imageScaler = new ImagePreProcessingScaler();
        imageScaler.fit(trainIter);

        if (this.flat) {
            ImageFlatteningDataSetPreProcessor flatten = new ImageFlatteningDataSetPreProcessor();
            return new CompositeDataSetPreProcessor(imageScaler, flatten);
        }

        return imageScaler;
    }

}
