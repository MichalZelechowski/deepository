package org.mz.deepository.lego.builder;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContinuousWithPaddingDataInitializer implements BrickDataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(ContinuousWithPaddingDataInitializer.class);

    @Override
    public Integer[][] prepareData(Integer[][] data) {
        Integer longestInput = Stream.of(data).map(array -> array.length).max(Comparators.naturalOrder()).orElse(0);
        int sumOfInput = Stream.of(data).mapToInt(array -> array.length).sum();
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

        List<Integer[]> paddedExamples = Lists.newLinkedList();

        index = 0;
        while (index < compressed.length) {
            int sequenceStart = index;
            int sequenceEnd = index + longestInput;

            for (int j = Math.min(sequenceEnd, compressed.length) - 1; j >= sequenceStart; j--) {
                Integer checkedValue = compressed[j];
                if (checkedValue == 1 || checkedValue == 0) {
                    Integer[] nextInput = new Integer[longestInput];
                    System.arraycopy(compressed, sequenceStart, nextInput, 0, j - sequenceStart + 1);
                    if (j - sequenceStart + 1 <= longestInput) {
                        Arrays.fill(nextInput, j - sequenceStart + 1, longestInput, -1);
                    }
                    paddedExamples.add(nextInput);

                    index += j - sequenceStart + 1;
                    break;
                }
            }

        }

        Integer[][] result = paddedExamples.toArray(new Integer[0][0]);
        LOG.info("Has {} examples with longest input as {}, total input data {}", result.length, longestInput, result.length * longestInput);
        ArrayUtils.shuffle(result);

        return result;
    }

}
