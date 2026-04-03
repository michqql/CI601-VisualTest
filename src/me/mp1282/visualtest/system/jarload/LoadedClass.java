package me.mp1282.visualtest.system.jarload;

import me.mp1282.visualtest.util.Identifiable;

import java.lang.reflect.Method;
import java.util.*;

public class LoadedClass extends Identifiable {

    private final LoadedJar loadedJar;
    private final Class<?> clazz;
    private final Collection<LoadedMethodExecutable> methods;

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

    public Collection<LoadedMethodExecutable> getMethods() {
        return methods;
    }

    private Collection<LoadedMethodExecutable> findMethods() {
        List<LoadedMethodExecutable> methods = new ArrayList<>();

        for(Method method : clazz.getDeclaredMethods()) {
            methods.add(new LoadedMethodExecutable(this, method));
        }

        return Collections.unmodifiableCollection(methods);
    }
}
