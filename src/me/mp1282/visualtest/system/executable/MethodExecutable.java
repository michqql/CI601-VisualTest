package me.mp1282.visualtest.system.executable;

import me.mp1282.visualtest.util.GenericTypeConverter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MethodExecutable extends Executable {

    protected final Method method;
    protected Map<String, String> cachedInfoMap;

    protected MethodExecutable(Method method) {
        this.method = method;
    }

    @Override
    public void execute(Object[] inputs, Object[] outputs) throws Exception {
        Object returnedValue = this.method.invoke(null, inputs);
        /* A MethodExecutable can only ever have 0 or 1 outputs, so write to the array
         * if this method returns an output. Otherwise, do nothing.
         */
        if(getNumberOfOutputs() > 0)
            outputs[0] = returnedValue;
    }

    @Override
    public String getName() {
        return this.method.getName();
    }

    @Override
    protected void populateInputOutputDataPorts(List<DataPort> inputs, List<DataPort> outputs) {
        /* Inputs */
        final Parameter[] params = method.getParameters();
        for(int i = 0; i < params.length; ++i) {
            inputs.add(new DataPort(
                    /* Executable     => */ this,
                    /* Is Input Port  => */ true,
                    /* Port Index     => */ i,
                    /* Data Type      => */ params[i].getType(),
                    /* Data Type Name => */ getReadableDataType(params[i].getType(), true, i),
                    /* Data Port name => */ params[i].getName()
            ));
        }

        /* Outputs */
        if(method.getReturnType() != void.class) {
            outputs.add(new DataPort(
                    /* Executable     => */ this,
                    /* Is Input Port  => */ false,
                    /* Port Index     => */ 0,
                    /* Data Type      => */ method.getReturnType(),
                    /* Data Type Name => */ getReadableDataType(method.getReturnType(), false, 0),
                    /* Data Port Name => */ "return"
            ));
        }
    }

    @Override
    protected void setup() {
        /* Create the information map */
        this.cachedInfoMap = new HashMap<>() {{
            put("Class", method.getDeclaringClass().getSimpleName());
            put("Is Static", String.valueOf(Modifier.isStatic(method.getModifiers())));
        }};
    }

    @Override
    public Map<String, String> getInformationMap() {
        return cachedInfoMap;
    }

    private String getReadableDataType(Class<?> type, boolean isInputPort, int index) {
        final StringBuilder builder = new StringBuilder();

        /* If generics are being used, get the generic type */
        Type genericType;
        if(isInputPort) {
            genericType = method.getGenericParameterTypes()[index];
        } else {
            genericType = method.getGenericReturnType();
        }
        builder.append(GenericTypeConverter.typeToString(genericType));

        /* Add array and var args [] ... at the end */
        if(type.isArray()) {
            builder.append('[');
            if(method.isVarArgs())
                builder.append("...");
            builder.append(']');
        }

        return builder.toString();
    }
}
