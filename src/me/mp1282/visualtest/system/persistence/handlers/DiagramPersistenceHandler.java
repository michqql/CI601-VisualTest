package me.mp1282.visualtest.system.persistence.handlers;

import com.google.gson.*;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;
import me.mp1282.visualtest.system.persistence.exceptions.UnsupportedSystemVersionException;
import me.mp1282.visualtest.util.GsonUtil;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class DiagramPersistenceHandler implements IPersistenceHandler<Diagram> {

    private static final String VERSION_MAJOR_KEY    = "version_major";
    private static final String VERSION_MINOR_KEY    = "version_minor";
    private static final String NAME_KEY             = "name";
    private static final String TRANSLATE_X_KEY      = "translate_x";
    private static final String TRANSLATE_Y_KEY      = "translate_y";
    private static final String NODES_KEY            = "nodes";
    private static final String CONNECTIONS_KEY      = "connections";
    private static final String FROM_KEY             = "from";
    private static final String TO_KEY               = "to";
    private static final String CONNECTION_UUID_KEY  = "uuid";
    private static final String CONNECTION_INDEX_KEY = "index";

    @Override
    public Diagram deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        final JsonObject root = (JsonObject) jsonElement;

        /* Check that the version trying to be loaded by the file is supported */
        int versionMajor = GsonUtil.getAsOrDefault(root.get(VERSION_MAJOR_KEY), JsonElement::getAsInt, -1);
        int versionMinor = GsonUtil.getAsOrDefault(root.get(VERSION_MINOR_KEY), JsonElement::getAsInt, -1);
        if(!VisualTestSystem.isVersionSupported(versionMajor, versionMinor))
            throw new JsonParseException(new UnsupportedSystemVersionException(versionMajor, versionMinor));

        final Diagram diagram = new Diagram();
        diagram.nameProperty().set(GsonUtil.getAsOrDefault(root.get(NAME_KEY), JsonElement::getAsString, ""));
        diagram.translateXProperty().set(GsonUtil.getAsOrDefault(root.get(TRANSLATE_X_KEY), JsonElement::getAsDouble, 0).doubleValue());
        diagram.translateYProperty().set(GsonUtil.getAsOrDefault(root.get(TRANSLATE_Y_KEY), JsonElement::getAsDouble, 0).doubleValue());

        final JsonArray nodeArray = root.getAsJsonArray(NODES_KEY);
        for(JsonElement nodeElement : nodeArray) {
            diagram.addDiagramNode(ctx.deserialize(nodeElement, DiagramNode.class));
        }

        final JsonArray connectionArray = root.getAsJsonArray(CONNECTIONS_KEY);
        for(JsonElement connElement : connectionArray) {
            final JsonObject connection = connElement.getAsJsonObject();
            final JsonObject from       = connection.getAsJsonObject(FROM_KEY);
            final JsonObject to         = connection.getAsJsonObject(TO_KEY);

            final UUID fromUUID = ctx.deserialize(from.get(CONNECTION_UUID_KEY), UUID.class);
            final UUID toUUID   = ctx.deserialize(to  .get(CONNECTION_UUID_KEY), UUID.class);

            final int fromIndex = from.get(CONNECTION_INDEX_KEY).getAsInt();
            final int toIndex   = to  .get(CONNECTION_INDEX_KEY).getAsInt();

            final DiagramNode fromNode = diagram.getDiagramNodeByUniqueId(fromUUID);
            final DiagramNode toNode   = diagram.getDiagramNodeByUniqueId(toUUID  );
            if(fromNode == null || toNode == null)
                throw new RuntimeException("What the heck");

            final OutputReturn   output = fromNode.getOutputs().get(fromIndex);
            final InputParameter input  = toNode  .getInputs ().get(toIndex  );

            /* Could use Diagram.connectDataPorts, however this checks the DAG for cycles
             * Which would perform this operation for every connection loaded.
             * Instead, we trust that when saving there were no cycles, and the user did not edit
             * save data to introduce a cycle.
             */

            output.setTo(input);
            input.setFrom(output);
        }

        return diagram;
    }

    @Override
    public JsonElement serialize(Diagram diagram, Type type, JsonSerializationContext ctx) {
        final JsonObject root = new JsonObject();

        /* Version */
        root.addProperty(VERSION_MAJOR_KEY, VisualTestSystem.SYSTEM_VERSION_MAJOR);
        root.addProperty(VERSION_MINOR_KEY, VisualTestSystem.SYSTEM_VERSION_MINOR);

        /* Diagram info */
        root.addProperty(NAME_KEY, diagram.nameProperty().get());
        root.addProperty(TRANSLATE_X_KEY, diagram.translateXProperty().get());
        root.addProperty(TRANSLATE_Y_KEY, diagram.translateYProperty().get());

        final JsonArray nodeArray = new JsonArray();
        for(DiagramNode node : diagram.nodesProperty()) {
            nodeArray.add(ctx.serialize(node, DiagramNode.class));
        }
        root.add(NODES_KEY, nodeArray);

        final JsonArray connectionArray = new JsonArray();
        for (OutputReturn output : diagram.getAllConnectedOutputs()) {
            final InputParameter input = output.getTo();

            JsonObject connection = new JsonObject();

            JsonObject from = new JsonObject();
            from.add(CONNECTION_UUID_KEY, ctx.serialize(output.getParentNode().getUniqueId(), UUID.class));
            from.addProperty(CONNECTION_INDEX_KEY, output.getType().getIndex());

            JsonObject to = new JsonObject();
            to.add(CONNECTION_UUID_KEY, ctx.serialize(input.getParentNode().getUniqueId(), UUID.class));
            to.addProperty(CONNECTION_INDEX_KEY, input.getType().getIndex());

            connection.add(FROM_KEY, from);
            connection.add(TO_KEY, to);
            connectionArray.add(connection);
        }
        root.add(CONNECTIONS_KEY, connectionArray);

        return root;
    }
}
