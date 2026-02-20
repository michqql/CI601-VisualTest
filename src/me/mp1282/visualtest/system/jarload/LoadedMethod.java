package me.mp1282.visualtest.system.jarload;

import me.mp1282.visualtest.system.executable.DataPort;
import me.mp1282.visualtest.system.executable.MethodExecutable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class LoadedMethod extends MethodExecutable {

    private final LoadedClass loadedClass;
    private final String signature;

    /* Package-private: Only instantiable by LoadedClass */
    LoadedMethod(LoadedClass loadedClass, Method method) {
        super(method);
        this.loadedClass = loadedClass;
        this.signature = createStringSignature(method);
    }

    @Override
    protected void populateInputOutputDataPorts(List<DataPort> inputs, List<DataPort> outputs) {
    }

    @Override
    protected void setup() {
        super.cachedInfoMap.put("Source JAR", loadedClass.getLoadedJar().getName());
    }

    public LoadedClass getLoadedClass() {
        return loadedClass;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public String getPersistenceId() {
        return signature;
    }

    private static String createStringSignature(Method method) {
        return Modifier.toString(method.getModifiers()) + " " +
                method.getReturnType().getSimpleName() + " " + method.getName();
    }
}
