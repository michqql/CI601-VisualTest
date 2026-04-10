package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

@InbuiltFunctionProvider(providerName = "Doubles")
public class DoubleInbuiltMethods {

    public static double add(double a, double b) {
        return a + b;
    }

    public static double subtract(double a, double b) {
        return a - b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static double divide(double a, double b) {
        return a / b;
    }

    public static double mod(double a, double b) {
        return a % b;
    }

    public static double abs(double a) {
        return Math.abs(a);
    }

    public static double negate(double a) {
        return -a;
    }

    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    public static double min(double a, double b) {
        return Math.min(a, b);
    }

    public static boolean equal(double a, double b) {
        return a == b;
    }

    public static boolean notEqual(double a, double b) {
        return a != b;
    }

    public static boolean greaterThan(double a, double b) {
        return a > b;
    }

    public static boolean lessThan(double a, double b) {
        return a < b;
    }

    public static boolean greaterThanOrEqual(double a, double b) {
        return a >= b;
    }

    public static boolean lessThanOrEqual(double a, double b) {
        return a <= b;
    }

    public static double floor(double a) {
        return Math.floor(a);
    }

    public static double ceil(double a) {
        return Math.ceil(a);
    }

    public static long round(double a) {
        return Math.round(a);
    }

    public static boolean isNaN(double a) {
        return Double.isNaN(a);
    }

    public static boolean isInfinite(double a) {
        return Double.isInfinite(a);
    }

    public static int doubleToInt(double a) {
        return (int) a;
    }

    public static float doubleToFloat(double a) {
        return (float) a;
    }

    public static String doubleToString(double a) {
        return Double.toString(a);
    }

    private DoubleInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("DoubleInbuiltMethods should not be instantiated");
    }
}
