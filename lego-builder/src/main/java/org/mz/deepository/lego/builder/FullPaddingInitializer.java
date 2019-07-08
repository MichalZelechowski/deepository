package org.mz.deepository.lego.builder;

import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FullPaddingInitializer implements BrickDataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(FullPaddingInitializer.class);

    @Override
    public Integer[][] prepareData(Integer[][] data) {
        Integer longestInput = Stream.of(data).map(array -> array.length).max(Comparators.naturalOrder()).orElse(0);
        Integer[][] result = new Integer[data.length][];
        float unpaddedCount = 0;
        for (int i = 0; i < data.length; i++) {
            Integer[] unpadded = new Integer[longestInput];
            System.arraycopy(data[i], 0, unpadded, 0, data[i].length);
            Arrays.fill(unpadded, data[i].length, longestInput, -1);
            unpaddedCount += data[i].length;
            result[i] = unpadded;
        }
        LOG.info("Padding percentag is {}", unpaddedCount / (data.length * longestInput));
        ArrayUtils.shuffle(result);

        return result;
    }

}
