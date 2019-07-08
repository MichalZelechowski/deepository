package org.mz.deepository.lego.builder;

import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContinuousDataInitializer implements BrickDataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(ContinuousDataInitializer.class);

    @Override
    public Integer[][] prepareData(Integer[][] data) {
        Integer longestInput = Stream.of(data).map(array -> array.length).max(Comparators.naturalOrder()).orElse(0);
        int sumOfInput = Stream.of(data).map(array -> array.length).mapToInt(i -> i).sum();
        Integer[] compressed = new Integer[sumOfInput];
        LOG.info("Compressed data size is {}", sumOfInput);

        int index = 0;
        Integer[] indexes = new Integer[data.length];
        int i = 0;
        for (Integer[] singleData : data) {
            System.arraycopy(singleData, 0, compressed, index, singleData.length);
            index += singleData.length;
            indexes[i++] = index;
        }

        Integer[][] result = new Integer[compressed.length / longestInput][];
        index = 0;
        for (int j = 0; j < result.length; j++) {
            result[j] = new Integer[longestInput];
            System.arraycopy(compressed, index, result[j], 0, longestInput);
            index += longestInput;
        }

        ArrayUtils.shuffle(result);

        return result;
    }

}
