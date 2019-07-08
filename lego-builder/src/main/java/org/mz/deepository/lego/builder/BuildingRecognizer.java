package org.mz.deepository.lego.builder;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;

public class BuildingRecognizer {

    private BuildingCodec codec = new BuildingCodec();

    public Building[] recognize(Integer[] encoded) {
        List<Building> buildings = Lists.newLinkedList();
        boolean buildingInProgress = false;
        int startIndex = -1;
        for (int i = 0; i < encoded.length; ++i) {
            int code = encoded[i];
            if (code > 100 && !buildingInProgress) {
                buildingInProgress = true;
                startIndex = i;
            } else if (code == 1 && buildingInProgress) {
                buildingInProgress = false;
                Integer[] buildingEncoded = Arrays.copyOfRange(encoded, startIndex, i + 1);
                buildings.add(codec.decode(buildingEncoded));
            } else if (code == -1 && buildingInProgress) {
                buildingInProgress = false;
            }
        }
        return buildings.stream().filter(Building::isValid).toArray(size -> new Building[size]);
    }
}
