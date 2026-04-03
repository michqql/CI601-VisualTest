package me.mp1282.visualtest.system.inbuilt.special;

import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.diagram.node.data.RangeCheckNodeData;
import me.mp1282.visualtest.system.executable.iodata.ParameterType;
import me.mp1282.visualtest.system.executable.iodata.ReturnType;
import me.mp1282.visualtest.system.inbuilt.SpecialExecutable;

import java.util.List;

public class RangeCheckExecutable extends SpecialExecutable {

    @Override
    protected void setup(List<ParameterType> parameterTypes, List<ReturnType> returnTypes) {
        parameterTypes.add(new ParameterType.Builder().setIndex(0).setDataType(Number.class).build());
        returnTypes.add(new ReturnType.Builder().setIndex(0).setDataType(boolean.class).build());
    }

    @Override
    public void execute(Object[] inputs, Object[] outputs, final NodeData data) {
        if (!(data instanceof RangeCheckNodeData rangeData)) {
            outputs[0] = false;
            return;
        }

        Number min = rangeData.minProperty().get();
        Number max = rangeData.maxProperty().get();

        if (min == null || max == null || !(inputs[0] instanceof Number value)) {
            outputs[0] = false;
            return;
        }

        double v = value.doubleValue();
        outputs[0] = v >= min.doubleValue() && v <= max.doubleValue();
    }

    @Override
    public String getName() {
        return "Range Check";
    }

    @Override
    protected String getNamespace() {
        return "*special";
    }

    @Override
    public NodeData createNodeData() {
        return new RangeCheckNodeData();
    }
}