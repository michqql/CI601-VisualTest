package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

@InbuiltFunctionProvider(providerName = "Strings")
public class StringInbuiltMethods {

    public static int length(String s) {
        return s.length();
    }

    public static boolean isEmpty(String s) {
        return s.isEmpty();
    }

    public static String charAt(String s, int index) {
        return String.valueOf(s.charAt(index));
    }

    public static String substring(String s, int start) {
        return s.substring(start);
    }

    public static String substringRange(String s, int start, int end) {
        return s.substring(start, end);
    }

    public static int indexOf(String s, String substr) {
        return s.indexOf(substr);
    }

    public static boolean contains(String s, String substr) {
        return s.contains(substr);
    }

    public static boolean startsWith(String s, String prefix) {
        return s.startsWith(prefix);
    }

    public static boolean endsWith(String s, String suffix) {
        return s.endsWith(suffix);
    }

    public static String toUpperCase(String s) {
        return s.toUpperCase();
    }

    public static String toLowerCase(String s) {
        return s.toLowerCase();
    }

    public static String trim(String s) {
        return s.trim();
    }

    public static String replace(String s, String target, String replacement) {
        return s.replace(target, replacement);
    }

    public static String concat(String a, String b) {
        return a + b;
    }

    public static boolean equals(String a, String b) {
        return a.equals(b);
    }

    public static boolean equalsIgnoreCase(String a, String b) {
        return a.equalsIgnoreCase(b);
    }

    public static int parseInt(String s) {
        return Integer.parseInt(s);
    }

    public static long parseLong(String s) {
        return Long.parseLong(s);
    }

    public static float parseFloat(String s) {
        return Float.parseFloat(s);
    }

    public static double parseDouble(String s) {
        return Double.parseDouble(s);
    }

    public static String fromInt(int n) {
        return Integer.toString(n);
    }

    public static String fromLong(long n) {
        return Long.toString(n);
    }

    public static String fromFloat(float n) {
        return Float.toString(n);
    }

    public static String fromDouble(double n) {
        return Double.toString(n);
    }

    public static String fromBoolean(boolean b) {
        return Boolean.toString(b);
    }

    private StringInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("StringInbuiltMethods should not be instantiated");
    }
}
