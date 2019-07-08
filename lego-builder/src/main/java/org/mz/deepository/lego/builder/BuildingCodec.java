package org.mz.deepository.lego.builder;

import java.util.Arrays;
import static org.apache.commons.lang3.ArrayUtils.addAll;
import org.mz.deepository.workbench.Codec;

public class BuildingCodec implements Codec<Building, Integer[]> {

    private final OutlineCodec delegate = new OutlineCodec();

    @Override
    public Building decode(Integer[] target) {
        Building building = new Building();

        int lastIndex = 0;
        float vx = 0, vy = 0, vz = 0;
        for (int i = 0; i < target.length; i++) {
            Integer value = target[i];
            if (value == 0 || value == 1) {
                Integer[] newTarget = Arrays.copyOfRange(target, lastIndex, i);
                Outline decodedOutline = delegate.decode(newTarget);
                decodedOutline.translate(vx, vy, vz);

                if (decodedOutline.bricks().isEmpty()) {
                    vy++;
                } else {
                    float[] nextPosition = decodedOutline.bricks().get(decodedOutline.bricks().size() - 1).nextPosition();
                    vx = nextPosition[0];
                    vy = nextPosition[1] + 1;
                    vz = nextPosition[2];
                }

                building.add(decodedOutline);
                lastIndex = i + 1;
            }
        }

        return building;
    }

    @Override
    public Integer[] encode(Building source) {
        return addAll(
                source.outlines().stream()
                        .map(this.delegate::encode)
                        .reduce((Integer[] a, Integer[] b) -> mergeArrays(a, b))
                        .orElse(new Integer[0]),
                new Integer[]{1});
    }

    private static Integer[] mergeArrays(Integer[] a, Integer[] b) {
        return addAll(addAll(a, new Integer[]{0}), b);
    }

}
