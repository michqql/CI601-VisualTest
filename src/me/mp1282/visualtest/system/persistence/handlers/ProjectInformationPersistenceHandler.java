package me.mp1282.visualtest.system.persistence.handlers;

import com.google.gson.*;
import me.mp1282.visualtest.system.ProjectInformation;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.jarload.LoadedJar;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;
import me.mp1282.visualtest.system.persistence.exceptions.UnsupportedSystemVersionException;
import me.mp1282.visualtest.util.GsonUtil;

import java.lang.reflect.Type;

public class ProjectInformationPersistenceHandler implements IPersistenceHandler<ProjectInformation> {

    private static final String NAME_KEY          = "name";
    private static final String VERSION_MAJOR_KEY = "system_version_major";
    private static final String VERSION_MINOR_KEY = "system_version_minor";
    private static final String JARS_KEY          = "required_jars";

    @Override
    public ProjectInformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        final JsonObject root = jsonElement.getAsJsonObject();

        /* Check that the version trying to be loaded by the file is supported */
        int versionMajor = GsonUtil.getAsOrDefault(root.get(VERSION_MAJOR_KEY), JsonElement::getAsInt, -1);
        int versionMinor = GsonUtil.getAsOrDefault(root.get(VERSION_MINOR_KEY), JsonElement::getAsInt, -1);
        if(!VisualTestSystem.isVersionSupported(versionMajor, versionMinor))
            throw new JsonParseException(new UnsupportedSystemVersionException(versionMajor, versionMinor));

        ProjectInformation info = VisualTestSystem.getInstance().getProjectInformation();
        info.nameProperty().set(GsonUtil.getAsOrDefault(root.get(NAME_KEY), JsonElement::getAsString, ""));

        /* For each LoadedJar, load the dependency */
        final JsonArray jarArray = root.getAsJsonArray(JARS_KEY);
        for(JsonElement element : jarArray) {
            /* Result is ignored because the deserialisation process
             * also puts the LoadedJar into the repository.
             */
            ctx.deserialize(element, LoadedJar.class);
        }

        return info;
    }

    @Override
    public JsonElement serialize(ProjectInformation info, Type type, JsonSerializationContext ctx) {
        final JsonObject root = new JsonObject();
        root.addProperty(NAME_KEY, info.nameProperty().get());
        root.addProperty(VERSION_MAJOR_KEY, info.versionMajorProperty().get());
        root.addProperty(VERSION_MINOR_KEY, info.versionMinorProperty().get());

        /* For each LoadedJar, save the dependency */
        final JsonArray jarArray = new JsonArray();
        for(LoadedJar jar : VisualTestSystem.getInstance().getJarRepository().getLoadedJars()) {
            jarArray.add(ctx.serialize(jar, LoadedJar.class));
        }
        root.add(JARS_KEY, jarArray);

        return root;
    }
}
