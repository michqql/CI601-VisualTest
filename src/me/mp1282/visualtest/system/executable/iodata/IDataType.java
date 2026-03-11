package me.mp1282.visualtest.system.executable.iodata;

public interface IDataType {

    int      getIndex();
    Type     getType();
    Class<?> getDataType();

    enum Type {
        PARAMETER,
        RETURN
    }
}
