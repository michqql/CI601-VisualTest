package me.mp1282.visualtest.system.jarload;

import me.mp1282.visualtest.util.HashUtil;
import me.mp1282.visualtest.util.Identifiable;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LoadedJar extends Identifiable {

    private final URI uri;
    private final URLClassLoader loader;
    private final Map<String, LoadedClass> loadedClassMap;
    private final String name;
    private final String hash;

    /* Package-private */
    LoadedJar(File jarFile) throws Exception {
        this.uri = jarFile.toURI();
        this.loader = new URLClassLoader(new URL[] { uri.toURL() }, null);
        this.loadedClassMap = new HashMap<>();
        this.name = jarFile.getName();
        this.hash = HashUtil.calculateHash(jarFile, HashUtil.Algorithm.SHA_256);

        loadJar(jarFile);
    }

    private void loadJar(File file) throws Exception {
        try (JarFile jar = new JarFile(file)) {
            Enumeration<JarEntry> entries = jar.entries();
            while(entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                String path = entry.getName();
                if(path.endsWith(".class")) {
                    path = path.replace("/", ".").replace(".class", "");
                    Class<?> loadedClass = loader.loadClass(path);

                    loadedClassMap.put(loadedClass.getName(), new LoadedClass(this, loadedClass));
                }
            }
        }
    }

    public URI getURI() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    public Map<String, LoadedClass> getLoadedClassMap() {
        return Collections.unmodifiableMap(loadedClassMap);
    }
}
