package me.mp1282.visualtest.system.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.mp1282.visualtest.system.IReset;
import me.mp1282.visualtest.system.ProjectInformation;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.persistence.deserializers.ProjectInformationJsonDeserializer;
import me.mp1282.visualtest.system.persistence.serializers.ProjectInformationJsonSerializer;
import me.mp1282.visualtest.system.persistence.serializers.SystemJsonSerializer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PersistenceService implements IReset {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(VisualTestSystem.class, new SystemJsonSerializer())
            .registerTypeAdapter(ProjectInformation.class, new ProjectInformationJsonSerializer())
            .registerTypeAdapter(ProjectInformation.class, new ProjectInformationJsonDeserializer())
            .create();

    public void load(File directory) throws Exception {
        final ProjectInformation info = VisualTestSystem.getInstance().getProjectInformation();

        /* Need to manually set project directory in the ProjectInformation class */
        info.projectDirectoryProperty().set(directory);

        File infoFile = new File(directory, "project_info.json");
        if(infoFile.exists())
            gson.fromJson(new FileReader(infoFile), ProjectInformation.class);
    }

    public void save(File as) throws Exception {
        try (FileWriter writer = new FileWriter(as)) {
            gson.toJson(VisualTestSystem.getInstance(), writer);
        }
    }

    @Override
    public void reset() {

    }
}
