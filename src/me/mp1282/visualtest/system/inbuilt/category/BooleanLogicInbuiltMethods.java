package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

@InbuiltFunctionProvider(providerName = "Boolean Logic")
public class BooleanLogicInbuiltMethods {

    public static boolean and(boolean a, boolean b) {
        return a && b;
    }

    public static boolean nand(boolean a, boolean b) {
        return !(a && b);
    }

    public static boolean or(boolean a, boolean b) {
        return a || b;
    }

    public static boolean xor(boolean a, boolean b) {
        return a ^ b;
    }

    public static boolean nor(boolean a, boolean b) {
        return !(a || b);
    }

    public static boolean not(boolean a) {
        return !a;
    }

    public static boolean isEqual(boolean a, boolean b) {
        return a == b;
    }

    public static boolean isNotEqual(boolean a, boolean b) {
        return a != b;
    }
}
