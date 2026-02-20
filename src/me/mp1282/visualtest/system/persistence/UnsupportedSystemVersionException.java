package me.mp1282.visualtest.system.persistence;

import me.mp1282.visualtest.system.VisualTestSystem;

public class UnsupportedSystemVersionException extends RuntimeException {
    public UnsupportedSystemVersionException(int major, int minor) {
        super(getFormatMessage(major, minor));
    }

    private static String getFormatMessage(int major, int minor) {
        return String.format(
                "System (Version %d.%d) does not support save (Version %d.%d)",
                VisualTestSystem.SYSTEM_VERSION_MAJOR, VisualTestSystem.SYSTEM_VERSION_MINOR,
                major, minor
        );
    }
}
