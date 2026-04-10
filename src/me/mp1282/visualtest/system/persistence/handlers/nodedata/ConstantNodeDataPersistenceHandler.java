package me.mp1282.visualtest.system.persistence.handlers.nodedata;

import com.google.gson.*;
import me.mp1282.visualtest.system.diagram.node.data.ConstantNodeData;
import me.mp1282.visualtest.system.diagram.runtime.ObjectSnapshot;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;
import me.mp1282.visualtest.util.GsonUtil;

import java.lang.reflect.Type;
import java.util.Base64;

public class ConstantNodeDataPersistenceHandler implements IPersistenceHandler<ConstantNodeData> {

    private static final String NULL_KEY       = "is_null";
    private static final String CLASS_NAME_KEY = "class_name";
    private static final String DATA_KEY       = "data";

    @Override
    public ConstantNodeData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        ConstantNodeData nodeData = new ConstantNodeData();
        JsonObject root = (JsonObject) jsonElement;

        boolean isNull = GsonUtil.getAsOrDefault(root.get(NULL_KEY), JsonElement::getAsBoolean, true);
        if(isNull)
            return nodeData;

        /* Otherwise, get class and decode base64 data string to byte array */
        String className = root.get(CLASS_NAME_KEY).getAsString();
        String base64data = root.get(DATA_KEY).getAsString();

        try {
            Class<?> clazz = Class.forName(className);
            byte[] data = Base64.getDecoder().decode(base64data);

            ObjectSnapshot snapshot = new ObjectSnapshot(clazz, data);
            nodeData.constantProperty().set(snapshot.getObject());

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return nodeData;
    }

    @Override
    public JsonElement serialize(ConstantNodeData constantNodeData, Type type, JsonSerializationContext ctx) {
        JsonObject root = new JsonObject();

        Object object = constantNodeData.constantProperty().get();
        root.addProperty(NULL_KEY, object == null);

        if(object != null) {
            root.addProperty(CLASS_NAME_KEY, object.getClass().getPackageName() + "." + object.getClass().getSimpleName());
            ObjectSnapshot snapshot = new ObjectSnapshot(object);
            root.addProperty(DATA_KEY, Base64.getEncoder().encodeToString(snapshot.getBytes()));
        }

        return root;
    }
}
