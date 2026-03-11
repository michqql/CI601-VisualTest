package me.mp1282.visualtest.system.persistence.handlers;

import com.google.gson.*;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;
import me.mp1282.visualtest.util.GsonUtil;
import me.mp1282.visualtest.util.Identifiable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.UUID;

public class DiagramNodePersistenceHandler implements IPersistenceHandler<DiagramNode> {

    private static final String UUID_KEY            = "uuid";
    private static final String POSITION_KEY        = "pos";
    private static final String POSITION_X_KEY      = "x";
    private static final String POSITION_Y_KEY      = "y";
    private static final String POSITION_WIDTH_KEY  = "width";
    private static final String POSITION_HEIGHT_KEY = "height";
    private static final String EXECUTABLE_KEY      = "executable";
    private static final String DATA_KEY            = "data";

    private final Field uuidField;
    private final Field dataField;

    public DiagramNodePersistenceHandler() {
        try {
            /* Find the UUID protected final field in the Identifiable class */
            uuidField = Identifiable.class.getDeclaredField("uuid");
            uuidField.setAccessible(true);

            /* Find the NodeData private final field in the DiagramNode class */
            dataField = DiagramNode.class.getDeclaredField("data");
            dataField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Could not find fields", e);
        }
    }

    @Override
    public DiagramNode deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        final JsonObject root = (JsonObject) jsonElement;
        final Executable executable = ctx.deserialize(root.get(EXECUTABLE_KEY), Executable.class);
        if(executable == null)
            throw new IllegalStateException("Unexpected null value (executable)");

        final DiagramNode node = new DiagramNode(executable);
        setUniqueId(node, ctx.deserialize(root.get(UUID_KEY), UUID.class));

        final JsonObject pos = root.getAsJsonObject(POSITION_KEY);
        node.xProperty     ().set(GsonUtil.getAsOrDefault(pos.get(POSITION_X_KEY     ), JsonElement::getAsDouble, 0).doubleValue());
        node.yProperty     ().set(GsonUtil.getAsOrDefault(pos.get(POSITION_Y_KEY     ), JsonElement::getAsDouble, 0).doubleValue());
        node.widthProperty ().set(GsonUtil.getAsOrDefault(pos.get(POSITION_WIDTH_KEY ), JsonElement::getAsDouble, 0).doubleValue());
        node.heightProperty().set(GsonUtil.getAsOrDefault(pos.get(POSITION_HEIGHT_KEY), JsonElement::getAsDouble, 0).doubleValue());

        setNodeData(node, ctx.deserialize(root.get(DATA_KEY), NodeData.class));

        return node;
    }

    @Override
    public JsonElement serialize(DiagramNode node, Type type, JsonSerializationContext ctx) {
        final JsonObject root = new JsonObject();
        final JsonObject pos = new JsonObject();

        root.add(UUID_KEY, ctx.serialize(node.getUniqueId(), UUID.class));

        /* Node position on diagram */
        pos.addProperty(POSITION_X_KEY,      node.xProperty().get());
        pos.addProperty(POSITION_Y_KEY,      node.yProperty().get());
        pos.addProperty(POSITION_WIDTH_KEY,  node.widthProperty().get());
        pos.addProperty(POSITION_HEIGHT_KEY, node.heightProperty().get());
        root.add(POSITION_KEY, pos);

        /* The wrapped executable - providing Executable.class
         * forces the library to use ExecutablePersistenceHandler
         */
        root.add(EXECUTABLE_KEY, ctx.serialize(node.getExecutable(), Executable.class));

        /* The node data - providing NodeData.class
         * forces the library to use NodeDataPersistenceHandler,
         * which uses the correct underlying type.
         */
        root.add(DATA_KEY, ctx.serialize(node.getData(), NodeData.class));

        return root;
    }

    private void setUniqueId(Identifiable identifiable, UUID uuid) {
        try {
            uuidField.set(identifiable, uuid);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void setNodeData(DiagramNode node, NodeData data) {
        try {
            dataField.set(node, data);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
