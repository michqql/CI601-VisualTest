package me.mp1282.visualtest.system.persistence.handlers.nodedata;

import com.google.gson.*;
import me.mp1282.visualtest.system.diagram.node.data.SwitchCase;
import me.mp1282.visualtest.system.diagram.node.data.SwitchNodeData;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;

import java.lang.reflect.Type;

public class SwitchNodeDataPersistenceHandler implements IPersistenceHandler<SwitchNodeData> {

    private static final String CASES_KEY = "cases";
    private static final String LABEL_KEY = "label";
    private static final String VALUE_KEY = "value";

    @Override
    public SwitchNodeData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        SwitchNodeData nodeData = new SwitchNodeData(0); /* populated below from saved cases */
        JsonObject root = (JsonObject) jsonElement;

        JsonArray casesArray = root.getAsJsonArray(CASES_KEY);
        if (casesArray == null) return nodeData;

        /* The SwitchNodeData is initially empty (0 cases); populate it from the saved array.
         * The case count was already used to restore the SwitchExecutable instance and
         * size nodeAfterPaths correctly — we just need to fill in the values here. */
        for (JsonElement el : casesArray) {
            JsonObject caseObj = el.getAsJsonObject();
            SwitchCase switchCase = new SwitchCase();
            switchCase.labelProperty().set(caseObj.get(LABEL_KEY).getAsString());
            switchCase.valueProperty().set(caseObj.get(VALUE_KEY).getAsString());
            nodeData.getCases().add(switchCase);
        }

        return nodeData;
    }

    @Override
    public JsonElement serialize(SwitchNodeData nodeData, Type type, JsonSerializationContext ctx) {
        JsonObject root = new JsonObject();
        JsonArray casesArray = new JsonArray();

        for (SwitchCase switchCase : nodeData.getCases()) {
            JsonObject caseObj = new JsonObject();
            caseObj.addProperty(LABEL_KEY, switchCase.getLabel());
            caseObj.addProperty(VALUE_KEY, switchCase.getValue());
            casesArray.add(caseObj);
        }

        root.add(CASES_KEY, casesArray);
        return root;
    }
}
