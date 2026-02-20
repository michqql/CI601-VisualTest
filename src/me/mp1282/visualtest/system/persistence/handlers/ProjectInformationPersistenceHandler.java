package me.mp1282.visualtest.system.persistence.handlers;

import com.google.gson.*;
import me.mp1282.visualtest.system.ProjectInformation;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;
import me.mp1282.visualtest.system.persistence.UnsupportedSystemVersionException;
import me.mp1282.visualtest.util.GsonUtil;

import java.lang.reflect.Type;

public class ProjectInformationPersistenceHandler implements IPersistenceHandler<ProjectInformation> {

    private static final String NAME_KEY = "name";
    private static final String VERSION_MAJOR_KEY = "system_version_major";
    private static final String VERSION_MINOR_KEY = "system_version_minor";

    @Override
    public ProjectInformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject root = jsonElement.getAsJsonObject();

        /* Check that the version trying to be loaded by the file is supported */
        int versionMajor = GsonUtil.getAsOrDefault(root.get(VERSION_MAJOR_KEY), JsonElement::getAsInt, -1);
        int versionMinor = GsonUtil.getAsOrDefault(root.get(VERSION_MINOR_KEY), JsonElement::getAsInt, -1);
        if(!VisualTestSystem.isVersionSupported(versionMajor, versionMinor))
            throw new JsonParseException(new UnsupportedSystemVersionException(versionMajor, versionMinor));

        ProjectInformation info = VisualTestSystem.getInstance().getProjectInformation();
        info.nameProperty().set(GsonUtil.getAsOrDefault(root.get(NAME_KEY), JsonElement::getAsString, ""));

        return info;
    }

    @Override
    public JsonElement serialize(ProjectInformation info, Type type, JsonSerializationContext ctx) {
        final JsonObject meta = new JsonObject();
        meta.addProperty(NAME_KEY, info.nameProperty().get());
        meta.addProperty(VERSION_MAJOR_KEY, info.versionMajorProperty().get());
        meta.addProperty(VERSION_MINOR_KEY, info.versionMinorProperty().get());

        return meta;
    }
}
