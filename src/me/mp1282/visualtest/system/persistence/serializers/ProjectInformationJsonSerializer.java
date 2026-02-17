package me.mp1282.visualtest.system.persistence.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.mp1282.visualtest.system.ProjectInformation;

import java.lang.reflect.Type;

public class ProjectInformationJsonSerializer implements JsonSerializer<ProjectInformation> {
    @Override
    public JsonElement serialize(ProjectInformation info, Type type, JsonSerializationContext ctx) {
        JsonObject meta = new JsonObject();
        meta.addProperty("name", info.nameProperty().get());
        meta.addProperty("sys_version_major", info.versionMajorProperty().get());
        meta.addProperty("sys_version_minor", info.versionMinorProperty().get());
        return meta;
    }
}
