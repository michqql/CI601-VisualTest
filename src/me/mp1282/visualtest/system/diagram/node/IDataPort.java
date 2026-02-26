package me.mp1282.visualtest.system.diagram.node;

import me.mp1282.visualtest.system.executable.data.IDataType;

public interface IDataPort<T extends IDataType> extends IPort {
    T getType();
}
