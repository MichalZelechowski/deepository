package org.mz.deepository.lego.builder;

import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.collections.impl.block.factory.Comparators;

public class CyclicDataInitializer implements BrickDataInitializer {

    @Override
    public Integer[][] prepareData(Integer[][] data) {
        Integer longestInput = Stream.of(data).map(array -> array.length).max(Comparators.naturalOrder()).orElse(0);

        Integer[][] result = new Integer[data.length][];
        for (int i = 0; i < data.length; i++) {
            int index = 0;
            Integer[] unpadded = new Integer[longestInput];
            if (data[i].length == 0) {
                Arrays.fill(unpadded, -1);
            } else {
                while (index < longestInput) {
                    int charsLeft = longestInput - index;
                    System.arraycopy(data[i], 0, unpadded, index, Math.min(data[i].length, charsLeft));
                    index += data[i].length;
                }
            }
            result[i] = unpadded;
        }
        ArrayUtils.shuffle(result);

        return result;
    }

}
