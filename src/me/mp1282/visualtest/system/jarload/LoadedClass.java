package me.mp1282.visualtest.system.jarload;

import me.mp1282.visualtest.util.Identifiable;

import java.lang.reflect.Method;
import java.util.*;

public class LoadedClass extends Identifiable {

    private final LoadedJar loadedJar;
    private final Class<?> clazz;
    private final Collection<LoadedMethod> methods;

    /* Package-private: Only instantiable by LoadedJar */
    LoadedClass(LoadedJar loadedJar, Class<?> clazz) {
        this.loadedJar = loadedJar;
        this.clazz = clazz;
        this.methods = findMethods();
    }

    public LoadedJar getLoadedJar() {
        return loadedJar;
    }

    public Class<?> getWrappedClass() {
        return clazz;
    }

    public Collection<LoadedMethod> getMethods() {
        return methods;
    }

    private Collection<LoadedMethod> findMethods() {
        List<LoadedMethod> methods = new ArrayList<>();

        for(Method method : clazz.getDeclaredMethods()) {
            methods.add(new LoadedMethod(this, method));
        }

        return Collections.unmodifiableCollection(methods);
    }
}
