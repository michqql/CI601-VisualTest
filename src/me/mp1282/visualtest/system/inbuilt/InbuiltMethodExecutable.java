package me.mp1282.visualtest.system.inbuilt;

import me.mp1282.visualtest.system.executable.MethodExecutable;

import java.lang.reflect.Method;

public class InbuiltMethodExecutable extends MethodExecutable {

    /* Package-private: Only instantiable by InbuiltMethodRepository */
    InbuiltMethodExecutable(InbuiltFunctionProvider providerAnnotation, Method method) {
        super(method);
    }
}
