package org.mz.deepository.lego.builder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.mz.deepository.workbench.ExperimentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildingPredictorListener extends ExperimentListener<MultiLayerNetwork> {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingPredictorListener.class);
    private final BuildingPredictor predictor;
    private final BrickIterator iterator;
    private final PovrayExporter exporter = new PovrayExporter();
    private final String path;

    public BuildingPredictorListener(int samples, int buildingSize, BrickIterator iterator, String path) {
        predictor = new BuildingPredictor(samples, buildingSize);
        this.iterator = iterator;
        this.path = path;
    }

    @Override
    public void onIteration(MultiLayerNetwork model, int iteration, int epoch) {
        if (iteration % 100 == 0) {
            Building[][] buildings = predictor.predict(model, iterator);
            LOG.info("At epoch {} iteration {} looking for buildings", epoch, iteration);
            int sampleIdx = 0;
            int samples = 0;
            for (Building[] building : buildings) {
                if (building.length != 0) {
                    LOG.info("Sample {}:", sampleIdx);
                    for (Building b : building) {
                        samples++;
                        final String export = this.exporter.export(b);
                        LOG.info(export);
                        final String povrayFile = "povray_" + Integer.toString(iteration) + "_" + Integer.toString(samples) + ".inc";
                        try {
                            FileUtils.write(new File(path, povrayFile), export, Charset.forName("UTF-8"));
                        } catch (IOException ex) {
                            LOG.warn("Cannot write export", ex);
                        }
                    }
                }
                sampleIdx++;
            }
            LOG.info("Total correctly generated samples: " + samples);
        }
    }

}
