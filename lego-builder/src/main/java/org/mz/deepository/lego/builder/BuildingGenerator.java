package org.mz.deepository.lego.builder;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class BuildingGenerator {

    private final Random rnd;

    public BuildingGenerator(long seed) {
        this.rnd = new Random(seed);
    }

    Stream<Building> generate(int amount) {
        final BuildingBuilder builder = new BuildingBuilder();
        final AtomicInteger counter = new AtomicInteger();
        return IntStream.range(0, amount).
                mapToObj((int i) -> {
                    Building nextBuilding = builder.build(rnd.nextInt(18) + 2, rnd.nextInt(8) + 2, rnd.nextInt(18) + 2);
                    int cntValue = counter.incrementAndGet();
                    if (cntValue % 10000 == 0) {
                        System.out.println(cntValue);
                    }
                    return nextBuilding;
                });
    }

}
