package me.mp1282.visualtest.system.inbuilt.special;

import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.data.ParameterType;
import me.mp1282.visualtest.system.executable.data.ReturnType;

import java.util.List;
import java.util.Map;

public class ConstantExecutable extends Executable {

    public static String CONSTANT_KEY = "constant";

    @Override
    protected void setup(List<ParameterType> parameterTypes, List<ReturnType> returnTypes) {
        /* Only ever has 1 return type, which is the constant data */
        returnTypes.add(new ReturnType.Builder().setIndex(0).setDataType(Object.class).build());
    }

    @Override
    public void execute(Object[] inputs, Object[] outputs,
                        final Map<String, Object> extraData) throws Exception {
        Object constant = extraData.get(CONSTANT_KEY);
        outputs[0] = constant;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    protected String getNamespace() {
        return "";
    }
}
