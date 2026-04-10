package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

import java.util.Objects;

@InbuiltFunctionProvider(providerName = "Objects")
public class ObjectInbuiltMethods {

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isNotNull(Object o) {
        return o != null;
    }

    public static boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    public static boolean notEquals(Object a, Object b) {
        return !Objects.equals(a, b);
    }

    public static String toString(Object o) {
        return String.valueOf(o);
    }

    public static int hashCode(Object o) {
        return Objects.hashCode(o);
    }

    public static Object requireNonNull(Object o, Object fallback) {
        return o != null ? o : fallback;
    }

    private ObjectInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("ObjectInbuiltMethods should not be instantiated");
    }
}
