package me.mp1282.visualtest.util;

import java.io.File;

public class Preconditions {

    public static void fileExists(File file) {
        if(!file.exists())
            throw new IllegalStateException("File " + file.getName() + " does not exist.");
    }
}
