package me.mp1282.visualtest;

import java.lang.instrument.Instrumentation;

public class Agent {

    private static Instrumentation inst;

    public static void premain(String args, Instrumentation instLocal) {
        inst = instLocal;
        System.out.println("Initialised Agent");
    }

    public static Class<?>[] getLoadedClasses() {
        return inst.getAllLoadedClasses();
    }
}
