package me.mp1282.visualtest.system.inbuilt.special;

import me.mp1282.visualtest.system.diagram.node.data.ConstantNodeData;
import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.executable.iodata.ParameterType;
import me.mp1282.visualtest.system.executable.iodata.ReturnType;
import me.mp1282.visualtest.system.inbuilt.SpecialExecutable;

import java.util.List;

public class ConstantExecutable extends SpecialExecutable {

    @Override
    protected void setup(List<ParameterType> parameterTypes, List<ReturnType> returnTypes) {
        /* Only ever has 1 return type, which is the constant data */
        returnTypes.add(new ReturnType.Builder().setIndex(0).setDataType(Object.class).build());
    }

    @Override
    public void execute(Object[] inputs, Object[] outputs, final NodeData data) {
        if(data instanceof ConstantNodeData constantData)
            outputs[0] = constantData.constantProperty().get();
    }

    @Override
    public String getName() {
        return "Constant";
    }

    @Override
    protected String getNamespace() {
        return "*special";
    }

    @Override
    public NodeData createNodeData() {
        return new ConstantNodeData();
    }
}
