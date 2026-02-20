package me.mp1282.visualtest.system.persistence.handlers;

import com.google.gson.*;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;
import me.mp1282.visualtest.util.GsonUtil;

import java.lang.reflect.Type;

public class DiagramPersistenceHandler implements IPersistenceHandler<Diagram> {

    private static final String NAME_KEY        = "name";
    private static final String TRANSLATE_X_KEY = "translate_x";
    private static final String TRANSLATE_Y_KEY = "translate_y";
    private static final String NODES_KEY       = "nodes";

    @Override
    public Diagram deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        final JsonObject root = (JsonObject) jsonElement;
        final Diagram diagram = new Diagram();
        diagram.nameProperty().set(GsonUtil.getAsOrDefault(root.get(NAME_KEY), JsonElement::getAsString, ""));
        diagram.translateXProperty().set(GsonUtil.getAsOrDefault(root.get(TRANSLATE_X_KEY), JsonElement::getAsDouble, 0).doubleValue());
        diagram.translateYProperty().set(GsonUtil.getAsOrDefault(root.get(TRANSLATE_Y_KEY), JsonElement::getAsDouble, 0).doubleValue());

        final JsonArray nodeArray = root.getAsJsonArray(NODES_KEY);
        for(JsonElement nodeElement : nodeArray) {
            diagram.nodesProperty().add(ctx.deserialize(nodeElement, DiagramNode.class));
        }

        return diagram;
    }

    @Override
    public JsonElement serialize(Diagram diagram, Type type, JsonSerializationContext ctx) {
        final JsonObject root = new JsonObject();

        root.addProperty(NAME_KEY, diagram.nameProperty().get());
        root.addProperty(TRANSLATE_X_KEY, diagram.translateXProperty().get());
        root.addProperty(TRANSLATE_Y_KEY, diagram.translateYProperty().get());

        final JsonArray nodeArray = new JsonArray();
        for(DiagramNode node : diagram.nodesProperty()) {
            nodeArray.add(ctx.serialize(node, DiagramNode.class));
        }

        root.add(NODES_KEY, nodeArray);

        return root;
    }
}
