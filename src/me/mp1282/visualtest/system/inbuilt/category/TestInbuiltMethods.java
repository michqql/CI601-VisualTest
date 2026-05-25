package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

@InbuiltFunctionProvider(providerName = "Test")
public class TestInbuiltMethods {

    public static int  oneInOneOut(int a) { return a; }
    public static void oneButWithAReallyLongMethodName(int a) {}
    public static void five(int a, int b, int c, int d, int e) {}

    public static String ASK() {
        return "NAME";
    }

    public static void PRINT(String name) {
        System.out.println("Hello " + name + "!");
    }
}
