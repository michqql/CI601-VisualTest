package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

@InbuiltFunctionProvider(providerName = "Numbers")
public class NumberInbuiltMethods {

    public static boolean equal(int a, int b) {
        return a == b;
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static int subtract(int a, int b) {
        return a - b;
    }

    private NumberInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("BooleanLogic should not be instantiated");
    }
}
