package org.mz.deepository.workbench;

import java.util.Random;

public final class GlobalRandom {

    private static long SEED = 0;
    private static Random RANDOM = new Random(SEED);

    public static Long getSeed() {
        return SEED;
    }

    public static Random getRandom() {
        return RANDOM;
    }

    public static void setSeed(long seed) {
        SEED = seed;
        RANDOM = new Random(seed);
    }
}
