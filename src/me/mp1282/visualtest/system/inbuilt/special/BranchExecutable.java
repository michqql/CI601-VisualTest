package me.mp1282.visualtest.system.inbuilt.special;

import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.executable.iodata.ParameterType;
import me.mp1282.visualtest.system.executable.iodata.ReturnType;
import me.mp1282.visualtest.system.inbuilt.SpecialExecutable;

import java.util.List;

public class BranchExecutable extends SpecialExecutable {

    /** Execution path index taken when the condition is {@code true}. */
    public static final int TRUE_BRANCH  = 0;
    /** Execution path index taken when the condition is {@code false}. */
    public static final int FALSE_BRANCH = 1;

    @Override
    protected void setup(List<ParameterType> parameterTypes, List<ReturnType> returnTypes) {
        parameterTypes.add(new ParameterType.Builder().setIndex(0).setDataType(boolean.class).build());
        /* No data outputs — branching is expressed entirely through execution paths */
    }

    @Override
    public void execute(Object[] inputs, Object[] outputs, final NodeData data) {
        /* No-op: the runtime (ExecuteTask) reads inputs via getChosenBranchIndex */
    }

    @Override
    public int getExecutionPathOutputCount() {
        return 2;
    }

    @Override
    public int getChosenBranchIndex(Object[] inputs) {
        return (inputs[0] instanceof Boolean b && b) ? TRUE_BRANCH : FALSE_BRANCH;
    }

    @Override
    public String getName() {
        return "Branch";
    }

    @Override
    protected String getNamespace() {
        return "*special";
    }
}
