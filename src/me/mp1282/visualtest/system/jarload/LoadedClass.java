package me.mp1282.visualtest.system.jarload;

import me.mp1282.visualtest.util.Identifiable;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LoadedClass extends Identifiable {

    private final LoadedJar loadedJar;
    private final Class<?> clazz;
    private final Map<String, LoadedMethod> methodMap;

    /* Package-private: Only instantiable by LoadedJar */
    LoadedClass(LoadedJar loadedJar, Class<?> clazz) {
        this.loadedJar = loadedJar;
        this.clazz = clazz;
        this.methodMap = new HashMap<>();

        inspectClass();
    }

    public LoadedJar getLoadedJar() {
        return loadedJar;
    }

    public Class<?> getWrappedClass() {
        return clazz;
    }

    public Map<String, LoadedMethod> getMethodMap() {
        return Collections.unmodifiableMap(methodMap);
    }

    private void inspectClass() {
        for(Method method : clazz.getDeclaredMethods()) {
            LoadedMethod exe = new LoadedMethod(this, method);
            exe.init();
            methodMap.put(exe.getSignature(), exe);
        }
    }
}
