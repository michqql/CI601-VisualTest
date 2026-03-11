package me.mp1282.visualtest.system.diagram.port;

import me.mp1282.visualtest.system.executable.iodata.IDataType;

public interface IDataPort<T extends IDataType> extends IPort {
    T getType();

    /**
     * Gets the other data port that is connected to this one.
     * @return The other data port, or null
     */
    IDataPort<?> getOther();

    void disconnect();
}
