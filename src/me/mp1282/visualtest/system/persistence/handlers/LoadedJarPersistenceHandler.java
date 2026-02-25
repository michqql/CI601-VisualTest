package me.mp1282.visualtest.system.persistence.handlers;

import com.google.gson.*;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.jarload.LoadedJar;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;
import me.mp1282.visualtest.system.persistence.exceptions.JarFileNotFoundException;
import me.mp1282.visualtest.util.GsonUtil;
import me.mp1282.visualtest.util.HashUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * <p>
 * The <strong>LoadedJarPersistenceHandler</strong> is responsible for the serialisation
 * and deserialisation of a {@link LoadedJar} object to and from JSON objects.
 * </p>
 * <p>
 * As a LoadedJar is loaded from a JAR file, the persistence only saves the location
 * of the JAR file on the computer (URI), the file name + extension and a hash
 * of the file.
 * </p>
 * <p>
 * The URI is used to find the exact location of the JAR file immediately.
 * If that fails, the filename will be used to find the JAR in the project workspace directory.
 * If that fails, an exception will be thrown.
 * <br><br>
 * If the JAR file is found but the hash is different, an exception will be thrown.
 * </p>
 */
public class LoadedJarPersistenceHandler implements IPersistenceHandler<LoadedJar> {

    private static final String FILENAME_KEY = "filename";
    private static final String URI_KEY      = "uri";
    private static final String HASH_KEY     = "hash";

    @Override
    public LoadedJar deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject root = (JsonObject) jsonElement;

        String uriString = GsonUtil.getAsOrDefault(root.get(URI_KEY),      JsonElement::getAsString, "");
        String filename  = GsonUtil.getAsOrDefault(root.get(FILENAME_KEY), JsonElement::getAsString, "");
        String hash      = GsonUtil.getAsOrDefault(root.get(HASH_KEY),     JsonElement::getAsString, "");

        File jarFile = null;

        /* Find file from URI firstly */
        try {
            jarFile = new File(new URI(uriString));

            /* Reset the jar file to be null if it doesn't exist */
            if(!jarFile.exists())
                jarFile = null;

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        /* If the file was not found, look in the project workspace for the JAR */
        if(jarFile == null) {
            File projectDir = VisualTestSystem.getInstance().getProjectInformation().projectDirectoryProperty().get();
            /* Only use this if the project directory exists */
            if(projectDir != null && projectDir.exists() && projectDir.isDirectory()) {
                jarFile = new File(projectDir, filename);

                /* Reset the jar file to be null if it doesn't exist */
                if (!jarFile.exists())
                    jarFile = null;
            }
        }

        /* If the jar file is still null, throw an exception */
        if(jarFile == null || !jarFile.exists())
            throw new JarFileNotFoundException(filename, uriString);

        /* Check the JAR file is the same via hash */
        if(!hash.equals(HashUtil.calculateHash(jarFile, HashUtil.Algorithm.SHA_256))) {
            System.out.println("Hash NEQ");
        }

        return VisualTestSystem.getInstance().getJarRepository().loadJar(jarFile).orElse(null);
    }

    @Override
    public JsonElement serialize(LoadedJar jar, Type type, JsonSerializationContext ctx) {
        JsonObject root = new JsonObject();

        root.addProperty(FILENAME_KEY, jar.getName());
        root.addProperty(URI_KEY,      jar.getURI().toString());
        root.addProperty(HASH_KEY,     jar.getHash());

        return root;
    }
}
