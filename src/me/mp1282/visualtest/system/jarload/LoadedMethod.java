package me.mp1282.visualtest.system.jarload;

import me.mp1282.visualtest.system.executable.MethodExecutable;

import java.lang.reflect.Method;

public class LoadedMethod extends MethodExecutable {

    private final LoadedClass loadedClass;

    /* Package-private: Only instantiable by LoadedClass */
    LoadedMethod(LoadedClass loadedClass, Method method) {
        super(method);
        this.loadedClass = loadedClass;
    }

//    @Override
//    protected void setup() {
//
//    }

//    public LoadedClass getLoadedClass() {
//        return loadedClass;
//    }
//
//    public String getSignature() {
//        return signature;
//    }
//
//    @Override
//    public String getPersistenceId() {
//        return signature;
//    }
//
//    private static String createStringSignature(Method method) {
//        return Modifier.toString(method.getModifiers()) + " " +
//                method.getReturnType().getSimpleName() + " " + method.getName();
//    }
}
