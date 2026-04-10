package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

@InbuiltFunctionProvider(providerName = "Math")
public class MathInbuiltMethods {

    public static double pi() {
        return Math.PI;
    }

    public static double e() {
        return Math.E;
    }

    public static double squareRoot(double a) {
        return Math.sqrt(a);
    }

    public static double cubeRoot(double a) {
        return Math.cbrt(a);
    }

    public static double pow(double base, double exp) {
        return Math.pow(base, exp);
    }

    public static double exp(double a) {
        return Math.exp(a);
    }

    public static double log(double a) {
        return Math.log(a);
    }

    public static double log10(double a) {
        return Math.log10(a);
    }

    public static double sin(double a) {
        return Math.sin(a);
    }

    public static double cos(double a) {
        return Math.cos(a);
    }

    public static double tan(double a) {
        return Math.tan(a);
    }

    public static double asin(double a) {
        return Math.asin(a);
    }

    public static double acos(double a) {
        return Math.acos(a);
    }

    public static double atan(double a) {
        return Math.atan(a);
    }

    public static double atan2(double y, double x) {
        return Math.atan2(y, x);
    }

    public static double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    public static double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }

    public static double signum(double a) {
        return Math.signum(a);
    }

    public static double hypot(double x, double y) {
        return Math.hypot(x, y);
    }

    private MathInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("MathInbuiltMethods should not be instantiated");
    }
}
