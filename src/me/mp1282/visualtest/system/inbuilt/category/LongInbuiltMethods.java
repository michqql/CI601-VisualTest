package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

@InbuiltFunctionProvider(providerName = "Longs")
public class LongInbuiltMethods {

    public static long add(long a, long b) {
        return a + b;
    }

    public static long subtract(long a, long b) {
        return a - b;
    }

    public static long multiply(long a, long b) {
        return a * b;
    }

    public static long divide(long a, long b) {
        return a / b;
    }

    public static long mod(long a, long b) {
        return a % b;
    }

    public static long abs(long a) {
        return Math.abs(a);
    }

    public static long negate(long a) {
        return -a;
    }

    public static long max(long a, long b) {
        return Math.max(a, b);
    }

    public static long min(long a, long b) {
        return Math.min(a, b);
    }

    public static boolean equal(long a, long b) {
        return a == b;
    }

    public static boolean notEqual(long a, long b) {
        return a != b;
    }

    public static boolean greaterThan(long a, long b) {
        return a > b;
    }

    public static boolean lessThan(long a, long b) {
        return a < b;
    }

    public static boolean greaterThanOrEqual(long a, long b) {
        return a >= b;
    }

    public static boolean lessThanOrEqual(long a, long b) {
        return a <= b;
    }

    public static int compare(long a, long b) {
        return Long.compare(a, b);
    }

    public static int longToInt(long a) {
        return (int) a;
    }

    public static double longToDouble(long a) {
        return (double) a;
    }

    public static String longToString(long a) {
        return Long.toString(a);
    }

    private LongInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("LongInbuiltMethods should not be instantiated");
    }
}
