package me.mp1282.visualtest.system.persistence.handlers.nodedata;

import com.google.gson.*;
import me.mp1282.visualtest.system.diagram.node.data.RangeCheckNodeData;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;
import me.mp1282.visualtest.util.GsonUtil;

import java.lang.reflect.Type;

public class RangeCheckNodeDataPersistenceHandler implements IPersistenceHandler<RangeCheckNodeData> {

    private static final String TYPE_CLASS_KEY = "type_class";
    private static final String MIN_VALUE_KEY  = "min_value";
    private static final String MAX_VALUE_KEY  = "max_value";

    @Override
    public RangeCheckNodeData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        RangeCheckNodeData nodeData = new RangeCheckNodeData();
        JsonObject root = (JsonObject) jsonElement;

        String className = GsonUtil.getAsOrDefault(root.get(TYPE_CLASS_KEY), JsonElement::getAsString, null);
        if (className == null)
            return nodeData;

        try {
            Class<? extends Number> numericType = Class.forName(className).asSubclass(Number.class);
            nodeData.typeProperty().set(numericType);

            JsonElement minEl = root.get(MIN_VALUE_KEY);
            JsonElement maxEl = root.get(MAX_VALUE_KEY);

            if (minEl != null && !minEl.isJsonNull())
                nodeData.minProperty().set(toTypedNumber(numericType, minEl.getAsDouble()));
            if (maxEl != null && !maxEl.isJsonNull())
                nodeData.maxProperty().set(toTypedNumber(numericType, maxEl.getAsDouble()));

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return nodeData;
    }

    @Override
    public JsonElement serialize(RangeCheckNodeData nodeData, Type type, JsonSerializationContext ctx) {
        JsonObject root = new JsonObject();

        Class<? extends Number> numericType = nodeData.typeProperty().get();
        if (numericType == null) {
            root.add(TYPE_CLASS_KEY, JsonNull.INSTANCE);
            return root;
        }

        root.addProperty(TYPE_CLASS_KEY, numericType.getName());

        Number min = nodeData.minProperty().get();
        Number max = nodeData.maxProperty().get();

        if (min != null) root.addProperty(MIN_VALUE_KEY, min.doubleValue());
        else             root.add(MIN_VALUE_KEY, JsonNull.INSTANCE);

        if (max != null) root.addProperty(MAX_VALUE_KEY, max.doubleValue());
        else             root.add(MAX_VALUE_KEY, JsonNull.INSTANCE);

        return root;
    }

    private static Number toTypedNumber(Class<? extends Number> clazz, double value) {
        if (clazz == Byte.class)    return (byte)  value;
        if (clazz == Short.class)   return (short) value;
        if (clazz == Long.class)    return (long)  value;
        if (clazz == Float.class)   return (float) value;
        if (clazz == Double.class)  return value;
        return (int) value;
    }
}