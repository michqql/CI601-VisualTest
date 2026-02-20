package me.mp1282.visualtest.system.inbuilt;

import me.mp1282.visualtest.system.executable.MethodExecutable;

import java.lang.reflect.Method;

public class InbuiltMethod extends MethodExecutable {

    private final String persistenceId;

    /* Package-private: Only instantiable by InbuiltMethodRepository */
    InbuiltMethod(InbuiltFunctionProvider providerAnnotation, Method method) {
        super(method);
        this.persistenceId = createId(providerAnnotation, method);
    }

    /* Package-private identifier getter */
    public String getPersistenceId() {
        return persistenceId;
    }

    private static String createId(InbuiltFunctionProvider providerAnnotation, Method method) {
        return String.format(
                "%s.%s.%s",
                providerAnnotation.providerName(),
                method.getDeclaringClass().getName(),
                method.getName()
        );
    }
}
