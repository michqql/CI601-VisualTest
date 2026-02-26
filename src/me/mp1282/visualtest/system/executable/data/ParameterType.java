package me.mp1282.visualtest.system.executable.data;

import java.lang.reflect.Parameter;

/**
 * {@link ParameterType} represents a parameter/input into an {@link me.mp1282.visualtest.system.executable.Executable}
 */
public class ParameterType implements IDataType {

    private int index;
    private Class<?> dataType;
    private boolean varArgs;

    private ParameterType() {}

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Type getType() {
        return Type.PARAMETER;
    }

    @Override
    public Class<?> getDataType() {
        return dataType;
    }

    public boolean isVarArgs() {
        return varArgs;
    }

    public static class Builder {
        private final ParameterType obj;

        public Builder() {
            this.obj = new ParameterType();
        }

        public Builder(Parameter parameter, int index) {
            this();

            obj.index = index;
            obj.dataType = parameter.getType();
            obj.varArgs  = parameter.isVarArgs();
        }

        public ParameterType build() {
            return obj;
        }

        public Builder setIndex(int index) {
            obj.index = index;
            return this;
        }

        public Builder setDataType(Class<?> dataType) {
            obj.dataType = dataType;
            return this;
        }

        public Builder setVarArgs(boolean varArgs) {
            obj.varArgs = varArgs;
            return this;
        }
    }
}
