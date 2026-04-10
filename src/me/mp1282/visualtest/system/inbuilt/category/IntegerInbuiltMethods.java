package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

@InbuiltFunctionProvider(providerName = "Integers")
public class IntegerInbuiltMethods {

    // --- Arithmetic ---

    public static int add(int a, int b) {
        return a + b;
    }

    public static int subtract(int a, int b) {
        return a - b;
    }

    public static int multiply(int a, int b) {
        return a * b;
    }

    public static int divide(int a, int b) {
        return a / b;
    }

    public static int mod(int a, int b) {
        return a % b;
    }

    public static int negate(int a) {
        return -a;
    }

    public static int abs(int a) {
        return Math.abs(a);
    }

    public static int signum(int a) {
        return Integer.signum(a);
    }

    public static int power(int base, int exp) {
        return (int) Math.pow(base, exp);
    }

    // --- Min / Max / Clamp ---

    public static int max(int a, int b) {
        return Math.max(a, b);
    }

    public static int min(int a, int b) {
        return Math.min(a, b);
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    // --- Comparisons ---

    public static boolean equal(int a, int b) {
        return a == b;
    }

    public static boolean notEqual(int a, int b) {
        return a != b;
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

    public static int compare(int a, int b) {
        return Integer.compare(a, b);
    }

    // --- Number Theory ---

    public static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    public static int lcm(int a, int b) {
        return (a == 0 || b == 0) ? 0 : Math.abs(a / gcd(a, b) * b);
    }

    // --- Bitwise ---

    public static int bitwiseAnd(int a, int b) {
        return a & b;
    }

    public static int bitwiseOr(int a, int b) {
        return a | b;
    }

    public static int bitwiseXor(int a, int b) {
        return a ^ b;
    }

    public static int bitwiseNot(int a) {
        return ~a;
    }

    public static int shiftLeft(int a, int bits) {
        return a << bits;
    }

    public static int shiftRight(int a, int bits) {
        return a >> bits;
    }

    public static int unsignedShiftRight(int a, int bits) {
        return a >>> bits;
    }

    public static int bitCount(int a) {
        return Integer.bitCount(a);
    }

    public static int highestOneBit(int a) {
        return Integer.highestOneBit(a);
    }

    public static int lowestOneBit(int a) {
        return Integer.lowestOneBit(a);
    }

    // --- String Representations ---

    public static String toBinaryString(int a) {
        return Integer.toBinaryString(a);
    }

    public static String toHexString(int a) {
        return Integer.toHexString(a);
    }

    public static String toOctalString(int a) {
        return Integer.toOctalString(a);
    }

    public static int parseHex(String s) {
        return Integer.parseInt(s, 16);
    }

    // --- Type Conversions ---

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

    private IntegerInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("IntegerInbuiltMethods should not be instantiated");
    }
}
