package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

@InbuiltFunctionProvider(providerName = "Time")
public class TimeInbuiltMethods {

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long nanoTime() {
        return System.nanoTime();
    }

    public static double millisToSeconds(long millis) {
        return millis / 1000.0;
    }

    public static long secondsToMillis(double seconds) {
        return (long) (seconds * 1000.0);
    }

    public static long minutesToMillis(double minutes) {
        return (long) (minutes * 60_000.0);
    }

    public static double millisToMinutes(long millis) {
        return millis / 60_000.0;
    }

    public static long elapsedMillis(long startMillis) {
        return System.currentTimeMillis() - startMillis;
    }

    private TimeInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("TimeInbuiltMethods should not be instantiated");
    }
}
