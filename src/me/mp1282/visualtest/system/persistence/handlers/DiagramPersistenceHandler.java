package me.mp1282.visualtest.system.persistence.handlers;

import com.google.gson.*;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.system.executable.DataPort;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;
import me.mp1282.visualtest.util.DataPortConnectionData;
import me.mp1282.visualtest.util.GsonUtil;

import java.lang.reflect.Type;
import java.util.UUID;

public class DiagramPersistenceHandler implements IPersistenceHandler<Diagram> {

    private static final String NAME_KEY        = "name";
    private static final String NODES_KEY       = "nodes";
    private static final String CONNECTIONS_KEY = "connections";

    @Override
    public Diagram deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        final JsonObject root = (JsonObject) jsonElement;
        final Diagram diagram = new Diagram();
        diagram.nameProperty().set(GsonUtil.getAsOrDefault(root.get(NAME_KEY), JsonElement::getAsString, ""));

        final JsonArray nodeArray = root.getAsJsonArray(NODES_KEY);
        for(JsonElement nodeElement : nodeArray) {
            diagram.nodesProperty().add(ctx.deserialize(nodeElement, DiagramNode.class));
        }

        final JsonArray connectionArray = root.getAsJsonArray(CONNECTIONS_KEY);
        for(JsonElement connElement : connectionArray) {
            JsonObject conn = connElement.getAsJsonObject();

            /* A */
            UUID nodeAId = ctx.deserialize(conn.get("node_a"), UUID.class);
            boolean portAInput = conn.get("port_a_input").getAsBoolean();
            int portAIndex = conn.get("port_a_index").getAsInt();
            DiagramNode nodeA = diagram.getNodeByUniqueId(nodeAId);
            DataPort portA = nodeA.getExecutable().getDataPort(portAInput, portAIndex);

            /* B */
            UUID nodeBId = ctx.deserialize(conn.get("node_b"), UUID.class);
            boolean portBInput = conn.get("port_b_input").getAsBoolean();
            int portBIndex = conn.get("port_b_index").getAsInt();
            DiagramNode nodeB = diagram.getNodeByUniqueId(nodeBId);
            DataPort portB = nodeB.getExecutable().getDataPort(portBInput, portBIndex);

            diagram.connectDataPorts(nodeA, portA, nodeB, portB);
        }

        return diagram;
    }

    @Override
    public JsonElement serialize(Diagram diagram, Type type, JsonSerializationContext ctx) {
        final JsonObject root = new JsonObject();

        root.addProperty(NAME_KEY, diagram.nameProperty().get());

        final JsonArray nodeArray = new JsonArray();
        for(DiagramNode node : diagram.nodesProperty()) {
            nodeArray.add(ctx.serialize(node, DiagramNode.class));
        }
        root.add(NODES_KEY, nodeArray);

        final JsonArray connectionArray = new JsonArray();
        for(DataPortConnectionData data : diagram.getAllDataPortConnections()) {
            JsonObject conn = new JsonObject();
            /* A */
            conn.add("node_a", ctx.serialize(data.nodeA().getUniqueId()));
            conn.addProperty("port_a_input", data.portA().inputPort());
            conn.addProperty("port_a_index", data.portA().portIndex());

            /* B */
            conn.add("node_b", ctx.serialize(data.nodeB().getUniqueId()));
            conn.addProperty("port_b_input", data.portB().inputPort());
            conn.addProperty("port_b_index", data.portB().portIndex());

            connectionArray.add(conn);
        }
        root.add(CONNECTIONS_KEY, connectionArray);

        return root;
    }
}
