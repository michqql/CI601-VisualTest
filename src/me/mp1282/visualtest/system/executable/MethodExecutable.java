package me.mp1282.visualtest.system.executable;

import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.executable.iodata.ParameterType;
import me.mp1282.visualtest.system.executable.iodata.ReturnType;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * A MethodExecutable is responsible for executing a Java {@link Method}
 */
public abstract class MethodExecutable extends Executable {

    protected final Method method;

    protected MethodExecutable(Method method) {
        this.method = method;
    }

    @Override
    public void execute(Object[] inputs, Object[] outputs,
                        final NodeData data) throws Exception {
        Object returnedValue = this.method.invoke(null, inputs);
        /* A MethodExecutable can only ever have 0 or 1 outputs, so write to the array
         * if this method returns an output. Otherwise, do nothing.
         */
        if(getNumberOfReturnValues() > 0)
            outputs[0] = returnedValue;
    }

    @Override
    public String getName() {
        return this.method.getName();
    }

    @Override
    protected String getNamespace() {
        return method.getDeclaringClass().getPackageName() + "_" + method.getDeclaringClass().getSimpleName();
    }

    @Override
    protected void setup(List<ParameterType> parameterTypes, List<ReturnType> returnTypes) {
        /* Inputs */
        final Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++)
            parameterTypes.add(new ParameterType.Builder(params[i], i).build());

        /* Output */
        if (method.getReturnType() != void.class) {
            returnTypes.add(
                    new ReturnType.Builder()
                            .setIndex(0)
                            .setDataType(method.getReturnType())
                            .build()
            );
        }
    }
}
