package me.mp1282.visualtest.system.executable.data;

public interface IDataType {

    int      getIndex();
    Type     getType();
    Class<?> getDataType();

    enum Type {
        PARAMETER,
        RETURN
    }
}
