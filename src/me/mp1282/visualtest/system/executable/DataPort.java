package me.mp1282.visualtest.system.executable;

public record DataPort(Executable executable,
                       boolean inputPort, int portIndex,
                       Class<?> dataType, String dataTypeDescriptor, String name) {
}
