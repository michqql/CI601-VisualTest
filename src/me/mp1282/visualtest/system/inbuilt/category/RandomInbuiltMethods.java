package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

import java.util.Random;

@InbuiltFunctionProvider(providerName = "Random")
public class RandomInbuiltMethods {

    private static final Random GLOBAL_RANDOM = new Random();

    public static void setGlobalSeed(long seed) {
        GLOBAL_RANDOM.setSeed(seed);
    }

    public static boolean randomBoolean() {
        return GLOBAL_RANDOM.nextBoolean();
    }

    public static int randomInteger() {
        return GLOBAL_RANDOM.nextInt();
    }

    /** Returns a random int in [0, bound). */
    public static int randomIntegerBound(int bound) {
        return GLOBAL_RANDOM.nextInt(bound);
    }

    /** Returns a random int in [min, max). */
    public static int randomIntegerRange(int min, int max) {
        return min + GLOBAL_RANDOM.nextInt(max - min);
    }

    public static long randomLong() {
        return GLOBAL_RANDOM.nextLong();
    }

    /** Returns a random float in [0.0, 1.0). */
    public static float randomFloat() {
        return GLOBAL_RANDOM.nextFloat();
    }

    /** Returns a random double in [0.0, 1.0). */
    public static double randomDouble() {
        return GLOBAL_RANDOM.nextDouble();
    }

    /** Returns a random double in [min, max). */
    public static double randomDoubleRange(double min, double max) {
        return min + GLOBAL_RANDOM.nextDouble() * (max - min);
    }

    /** Returns a random double from a Gaussian (normal) distribution with mean 0 and std dev 1. */
    public static double randomGaussian() {
        return GLOBAL_RANDOM.nextGaussian();
    }

    private RandomInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("RandomInbuiltMethods should not be instantiated");
    }
}
