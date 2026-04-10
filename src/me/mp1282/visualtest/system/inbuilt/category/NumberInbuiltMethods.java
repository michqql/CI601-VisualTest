package me.mp1282.visualtest.system.inbuilt.category;

/* Renamed to IntegerInbuiltMethods — this class is no longer registered. */
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

    public static int mod(int a, int b) {
        return  a % b;
    }

    public static int abs(int a) {
        return Math.abs(a);
    }

    public static int multiply(int a, int b) {
        return a * b;
    }

    public static int divide(int a, int b) {
        return a / b;
    }

    public static int negate(int a) {
        return -a;
    }

    public static int max(int a, int b) {
        return Math.max(a, b);
    }

    public static int min(int a, int b) {
        return Math.min(a, b);
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int power(int base, int exp) {
        return (int) Math.pow(base, exp);
    }

    public static boolean greaterThan(int a, int b) {
        return a > b;
    }

    public static boolean lessThan(int a, int b) {
        return a < b;
    }

    public static boolean greaterThanOrEqual(int a, int b) {
        return a >= b;
    }

    public static boolean lessThanOrEqual(int a, int b) {
        return a <= b;
    }

    public static boolean notEqual(int a, int b) {
        return a != b;
    }

    public static int compare(int a, int b) {
        return Integer.compare(a, b);
    }

    public static long intToLong(int a) {
        return (long) a;
    }

    public static float intToFloat(int a) {
        return (float) a;
    }

    public static double intToDouble(int a) {
        return (double) a;
    }

    public static String intToString(int a) {
        return Integer.toString(a);
    }

    private NumberInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("BooleanLogic should not be instantiated");
    }
}
