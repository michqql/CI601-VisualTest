package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

@InbuiltFunctionProvider(providerName = "Characters")
public class CharacterInbuiltMethods {

    // --- Classification (operate on first char of a single-character String) ---

    public static boolean isDigit(String ch) {
        return !ch.isEmpty() && Character.isDigit(ch.charAt(0));
    }

    public static boolean isLetter(String ch) {
        return !ch.isEmpty() && Character.isLetter(ch.charAt(0));
    }

    public static boolean isLetterOrDigit(String ch) {
        return !ch.isEmpty() && Character.isLetterOrDigit(ch.charAt(0));
    }

    public static boolean isAlphabetic(String ch) {
        return !ch.isEmpty() && Character.isAlphabetic(ch.charAt(0));
    }

    public static boolean isWhitespace(String ch) {
        return !ch.isEmpty() && Character.isWhitespace(ch.charAt(0));
    }

    public static boolean isUpperCase(String ch) {
        return !ch.isEmpty() && Character.isUpperCase(ch.charAt(0));
    }

    public static boolean isLowerCase(String ch) {
        return !ch.isEmpty() && Character.isLowerCase(ch.charAt(0));
    }

    // --- Conversion ---

    public static String toUpperCase(String ch) {
        return ch.isEmpty() ? ch : String.valueOf(Character.toUpperCase(ch.charAt(0)));
    }

    public static String toLowerCase(String ch) {
        return ch.isEmpty() ? ch : String.valueOf(Character.toLowerCase(ch.charAt(0)));
    }

    /** Returns the Unicode code point of the first character in the string. */
    public static int charCode(String ch) {
        return ch.isEmpty() ? -1 : ch.charAt(0);
    }

    /** Returns a single-character string for the given Unicode code point. */
    public static String fromCode(int code) {
        return String.valueOf((char) code);
    }

    public static int numericValue(String ch) {
        return ch.isEmpty() ? -1 : Character.getNumericValue(ch.charAt(0));
    }

    private CharacterInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("CharacterInbuiltMethods should not be instantiated");
    }
}
