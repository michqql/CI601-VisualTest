package me.mp1282.visualtest.system.inbuilt.special;

import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.executable.iodata.ParameterType;
import me.mp1282.visualtest.system.executable.iodata.ReturnType;
import me.mp1282.visualtest.system.inbuilt.SpecialExecutable;

import java.util.List;

/**
 * A visual join-point that re-unifies diverging execution paths back into a single
 * sequential flow. Connect multiple upstream branches to separate Merge nodes and
 * chain them together, or connect both sides of a Branch/Switch directly to a shared
 * Merge.
 * <p>
 * Note: the underlying execution model supports one incoming execution path per node,
 * so Merge is a single-input, single-output passthrough. Its primary purpose is to
 * serve as a clear visual marker of where branches converge in the diagram.
 */
public class MergeExecutable extends SpecialExecutable {

    @Override
    protected void setup(List<ParameterType> parameterTypes, List<ReturnType> returnTypes) {
        /* No data ports — purely a control-flow marker */
    }

    @Override
    public void execute(Object[] inputs, Object[] outputs, NodeData data) {
        /* No-op */
    }

    @Override
    public String getName() {
        return "Merge";
    }

    @Override
    protected String getNamespace() {
        return "*special";
    }
}
