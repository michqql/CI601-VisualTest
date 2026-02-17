package me.mp1282.visualtest.system.persistence.deserializers;

import com.google.gson.*;
import me.mp1282.visualtest.system.ProjectInformation;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.util.GsonUtil;

import java.lang.reflect.Type;

public class ProjectInformationJsonDeserializer implements JsonDeserializer<ProjectInformation> {
    @Override
    public ProjectInformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject root = jsonElement.getAsJsonObject();

        ProjectInformation info = VisualTestSystem.getInstance().getProjectInformation();
        info.nameProperty().set(GsonUtil.getAsOrDefault(root.get("name"), JsonElement::getAsString, ""));

        return info;
    }
}
