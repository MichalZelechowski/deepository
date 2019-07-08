package org.mz.deepository.lego.builder;

import java.util.Random;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.nd4j.linalg.dataset.DataSet;

public class BrickIteratorTest {

    @Test
    public void testHasDatasets() {
        //setup
        Integer[][] data = new Integer[64][];

        Random rnd = new Random();
        for (int i = 0; i < data.length; i++) {
            data[i] = new Integer[rnd.nextInt(20)];
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] = rnd.nextInt(BrickIterator.MAX_LENGTH * 4) + 101;
            }
        }

        //examine
        BrickIterator iterator = new BrickIterator(data, 16, new FullPaddingInitializer());
        //verify
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isNotNull();
        assertThat(iterator.next()).isNotNull();
        assertThat(iterator.next()).isNotNull();
        final DataSet last = iterator.next();
        assertThat(last).isNotNull();
        assertThat(last.getFeatures().shape()[0]).isEqualTo(16);
        assertThat(iterator.hasNext()).isFalse();
    }

}
