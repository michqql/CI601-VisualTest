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

    private RandomInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("BooleanLogic should not be instantiated");
    }
}
