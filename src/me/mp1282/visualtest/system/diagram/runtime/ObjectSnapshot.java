package me.mp1282.visualtest.system.diagram.runtime;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ObjectSnapshot {

    private static final Kryo KRYO_LIB = new Kryo();

    private final Class<?> type;
    private final byte[] bytes;

    private Object cachedObject;

    public ObjectSnapshot(Object object) {
        this.type = object.getClass();
        KRYO_LIB.register(type);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);
        KRYO_LIB.writeObject(output, object);
        output.close();

        this.bytes = bos.toByteArray();
    }

    public byte[] getBytes() {
        return bytes;
    }

    public Object getObject() {
        if(cachedObject == null) {
            Input input = new Input(new ByteArrayInputStream(bytes));
            cachedObject = KRYO_LIB.readObject(input, type);
        }
        return cachedObject;
    }
}
