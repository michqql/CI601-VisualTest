package me.mp1282.visualtest.system.inbuilt.special;

import me.mp1282.visualtest.system.diagram.node.NodeData;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.data.ParameterType;
import me.mp1282.visualtest.system.executable.data.ReturnType;

import java.util.List;

public class ConstantExecutable extends Executable {

    @Override
    protected void setup(List<ParameterType> parameterTypes, List<ReturnType> returnTypes) {
        /* Only ever has 1 return type, which is the constant data */
        returnTypes.add(new ReturnType.Builder().setIndex(0).setDataType(Object.class).build());
    }

    @Override
    public void execute(Object[] inputs, Object[] outputs,
                        final NodeData data) throws Exception {
        if(data instanceof ConstantData constantData)
            outputs[0] = constantData.getConstant();
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    protected String getNamespace() {
        return "";
    }

    public static class ConstantData extends NodeData {
        private Object constant;

        public Object getConstant() {
            return constant;
        }

        public void setConstant(Object constant) {
            this.constant = constant;
        }
    }
}
