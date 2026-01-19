package me.mp1282.visualtest.util;

public class DragContext {

    private static Object object;

    public static void setObject(Object obj) {
        object = obj;
    }

    public static Object getObject() {
        return object;
    }
}
