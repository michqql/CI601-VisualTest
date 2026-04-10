package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

@InbuiltFunctionProvider(providerName = "Floats")
public class FloatInbuiltMethods {

    public static float add(float a, float b) {
        return a + b;
    }

    public static float subtract(float a, float b) {
        return a - b;
    }

    public static float multiply(float a, float b) {
        return a * b;
    }

    public static float divide(float a, float b) {
        return a / b;
    }

    public static float mod(float a, float b) {
        return a % b;
    }

    public static float abs(float a) {
        return Math.abs(a);
    }

    public static float negate(float a) {
        return -a;
    }

    public static float max(float a, float b) {
        return Math.max(a, b);
    }

    public static float min(float a, float b) {
        return Math.min(a, b);
    }

    public static boolean equal(float a, float b) {
        return a == b;
    }

    public static boolean notEqual(float a, float b) {
        return a != b;
    }

    public static boolean greaterThan(float a, float b) {
        return a > b;
    }

    public static boolean lessThan(float a, float b) {
        return a < b;
    }

    public static boolean greaterThanOrEqual(float a, float b) {
        return a >= b;
    }

    public static boolean lessThanOrEqual(float a, float b) {
        return a <= b;
    }

    public static float floor(float a) {
        return (float) Math.floor(a);
    }

    public static float ceil(float a) {
        return (float) Math.ceil(a);
    }

    public static int round(float a) {
        return Math.round(a);
    }

    public static boolean isNaN(float a) {
        return Float.isNaN(a);
    }

    public static boolean isInfinite(float a) {
        return Float.isInfinite(a);
    }

    public static int floatToInt(float a) {
        return (int) a;
    }

    public static double floatToDouble(float a) {
        return (double) a;
    }

    public static String floatToString(float a) {
        return Float.toString(a);
    }

    private FloatInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("FloatInbuiltMethods should not be instantiated");
    }
}
