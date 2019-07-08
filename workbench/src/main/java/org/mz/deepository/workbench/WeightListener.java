package org.mz.deepository.workbench;

import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.deeplearning4j.nn.api.Model;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeightListener extends ExperimentListener<Model> {

    private static final Logger LOG = LoggerFactory.getLogger(WeightListener.class);

    @Override
    public void onIteration(Model model, int iteration, int epoch) {
        if (iteration % 100 == 0) {
            StringBuilder stringBuilder = new StringBuilder();
            Map<String, INDArray> paramMap = model.paramTable();
            paramMap.entrySet().stream()
                    .map(entry -> {
                        INDArray summary = Nd4j.create(new float[]{
                            entry.getValue().minNumber().floatValue(),
                            entry.getValue().maxNumber().floatValue(),
                            entry.getValue().meanNumber().floatValue(),
                            entry.getValue().medianNumber().floatValue(),
                            entry.getValue().stdNumber().floatValue()},
                                new int[]{5});
                        return Pair.of(entry.getKey(), summary);
                    }).
                    map(pair -> pair.getKey() + ": " + pair.getValue()).
                    sorted().
                    forEach(value -> stringBuilder.append('\n').append(value));
            LOG.info("Iteration {}: {}", iteration, stringBuilder);
        }
    }

}
