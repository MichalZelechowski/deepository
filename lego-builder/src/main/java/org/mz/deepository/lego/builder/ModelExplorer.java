package org.mz.deepository.lego.builder;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

public class ModelExplorer {

    public static void main(String[] args) throws IOException {
        String modelPath = args[0];
        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(modelPath);
        Buildings trainBuildings = new Buildings(args[1]);

        Integer[][] trainData = readData(trainBuildings);
        BrickIterator trainDataIterator = new BrickIterator(trainData, 64, new ContinuousWithPaddingDataInitializer());

        BuildingPredictor buildingPredictor = new BuildingPredictor(1000, 220);

        Building[][] buildings = buildingPredictor.predict(model, trainDataIterator);

        BuildingCodec buildingCodec = new BuildingCodec();
        PovrayExporter povrayExporter = new PovrayExporter();
        int buildingsMatchingInput = 0;
        int totalBuildings = 0;
        for (Building[] buildingPerSample : buildings) {
            for (Building building : buildingPerSample) {
                Integer[] generatedBuildingCode = buildingCodec.encode(building);
                boolean matched = false;
                for (Integer[] trainBuilding : trainData) {
                    if (Arrays.equals(generatedBuildingCode, trainBuilding)) {
                        buildingsMatchingInput++;
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    System.out.println(povrayExporter.export(building));
                }
                totalBuildings++;
            }
        }
        System.out.println("Building matching is " + buildingsMatchingInput + " out of " + totalBuildings);

        int newOutlines = 0;
        int totalOutlines = 0;
        for (Building[] buildingPerSample : buildings) {
            for (Building building : buildingPerSample) {
                Integer[] generatedBuildingCode = buildingCodec.encode(building);
                boolean matched = false;
                for (Integer[] trainBuilding : trainData) {
                    if (Arrays.equals(generatedBuildingCode, trainBuilding)) {
                        buildingsMatchingInput++;
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    boolean hasNewOutlines = false;
                    int newOutlinesPerBuilding = 0;
                    List<Integer[]> buildingToCheck = splitBy0(generatedBuildingCode);
                    for (Integer[] outlineToCheck : buildingToCheck) {
                        boolean alreadyExists = false;
                        for (Integer[] trainBuilding : trainData) {
                            List<Integer[]> checkAgainst = splitBy0(trainBuilding);

                            alreadyExists = checkAgainst.stream().anyMatch(againstOutline -> Arrays.equals(againstOutline, outlineToCheck));
                            if (alreadyExists) {
                                break;
                            }
                        }
                        if (!alreadyExists) {
                            newOutlines++;
                            newOutlinesPerBuilding++;
                            hasNewOutlines = true;
                        }
                    }
                    if (hasNewOutlines) {
                        System.out.println(povrayExporter.export(building));
                        System.out.println("New outlines per building is " + newOutlinesPerBuilding + " against " + buildingToCheck.size());
                    }
                    totalOutlines += buildingToCheck.size();
                }
            }
            totalBuildings++;
        }
        System.out.format("New outlines %d out of total %d\n", newOutlines, totalOutlines);
    }

    private static List<Integer[]> splitBy0(Integer[] generatedBuildingCode) {
        //split by 0
        List<Integer[]> splitBy0 = Lists.newLinkedList();
        int lastIndex = 0;
        for (int i = 0; i < generatedBuildingCode.length; i++) {
            if (generatedBuildingCode[i] == 0 || generatedBuildingCode[i] == 1) {
                final int outlineLength = i - lastIndex;
                Integer[] outline = new Integer[outlineLength];
                System.arraycopy(generatedBuildingCode, lastIndex, outline, 0, outlineLength);
                splitBy0.add(outline);
                lastIndex = i + 1;
            }
        }
        return splitBy0;
    }

    private static Integer[][] readData(Buildings buildings) throws IOException {
        BuildingCodec codec = new BuildingCodec();
        AtomicInteger counter = new AtomicInteger();
        System.out.println("Reading buildings data: " + buildings);
        Integer[][] trainData = buildings.load().map(building -> codec.encode(building))
                .peek(value -> {
                    int countValue = counter.incrementAndGet();
                    if (countValue % 10000 == 0) {
                        System.out.println("So far read " + countValue + " buildings.");
                    }
                })
                .toArray(size -> new Integer[size][]);
        return trainData;
    }
}
