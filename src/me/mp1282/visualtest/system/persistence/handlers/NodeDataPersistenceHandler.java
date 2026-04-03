package me.mp1282.visualtest.system.persistence.handlers;

import com.google.gson.*;
import me.mp1282.visualtest.system.diagram.node.data.ConstantNodeData;
import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.diagram.node.data.RangeCheckNodeData;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;
import me.mp1282.visualtest.system.persistence.handlers.nodedata.ConstantNodeDataPersistenceHandler;
import me.mp1282.visualtest.system.persistence.handlers.nodedata.RangeCheckNodeDataPersistenceHandler;

import java.lang.reflect.Type;

public class NodeDataPersistenceHandler implements IPersistenceHandler<NodeData> {

    private static final String CLASS_NAME_KEY = "type";
    private static final String DATA_KEY       = "data";

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ConstantNodeData.class, new ConstantNodeDataPersistenceHandler())
            .registerTypeAdapter(RangeCheckNodeData.class, new RangeCheckNodeDataPersistenceHandler())
            .create();

    @Override
    public NodeData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject root = (JsonObject) jsonElement;

        NodeData instance;

        String className = root.get(CLASS_NAME_KEY).getAsString();
        Class<?> clazz;
        try {
            /* Find the class with that specified name */
            clazz = Class.forName(className);

            /* If the clazz is NodeData, return empty node data object */
            if(NodeData.class.equals(clazz))
                return new NodeData();

            /* Ensure the class is instance of NodeData */
            if(!NodeData.class.isAssignableFrom(clazz))
                throw new RuntimeException("Class is not NodeData or sub-class");

            Class<? extends NodeData> nodeDataClass = clazz.asSubclass(NodeData.class);

            /* Request that GSON deserializes, passing in the node data class */
            instance = gson.fromJson(root.get(DATA_KEY), nodeDataClass);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    @Override
    public JsonElement serialize(NodeData nodeData, Type type, JsonSerializationContext ctx) {
        JsonObject root = new JsonObject();
        root.addProperty(CLASS_NAME_KEY, nodeData.getClass().getPackageName() + "." + nodeData.getClass().getSimpleName());
        root.add(DATA_KEY, gson.toJsonTree(nodeData));

        return root;

    }
}
