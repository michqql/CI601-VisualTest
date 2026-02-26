package me.mp1282.visualtest.system.executable.data;

public class ReturnType implements IDataType {

    private int index;
    private Class<?> dataType;

    private ReturnType() {
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Type getType() {
        return Type.RETURN;
    }

    @Override
    public Class<?> getDataType() {
        return dataType;
    }

    public static class Builder {
        private final ReturnType obj;

        public Builder() {
            this.obj = new ReturnType();
        }

        public ReturnType build() {
            return obj;
        }

        public Builder setIndex(int index) {
            this.obj.index = index;
            return this;
        }

        public Builder setDataType(Class<?> dataType) {
            this.obj.dataType = dataType;
            return this;
        }
    }
}
