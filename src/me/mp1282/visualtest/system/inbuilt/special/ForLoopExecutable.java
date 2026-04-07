package me.mp1282.visualtest.system.inbuilt.special;

import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.executable.iodata.ParameterType;
import me.mp1282.visualtest.system.executable.iodata.ReturnType;
import me.mp1282.visualtest.system.inbuilt.SpecialExecutable;

import java.util.List;

/**
 * Repeats a sub-graph N times, providing the current iteration index as a data output.
 * <p>
 * Execution paths:
 * <ul>
 *   <li>Index {@link #LOOP_INDEX} — taken once per iteration (0 … count-1).</li>
 *   <li>Index {@link #DONE_INDEX} — taken once after all iterations complete.</li>
 * </ul>
 * The {@link me.mp1282.visualtest.system.diagram.runtime.ExecuteTask} handles the
 * repeated execution of the loop body inline; this executable itself is a no-op at
 * runtime.
 */
public class ForLoopExecutable extends SpecialExecutable {

    /** Execution path index for the loop body (repeated). */
    public static final int LOOP_INDEX = 0;
    /** Execution path index taken once the loop finishes. */
    public static final int DONE_INDEX = 1;

    @Override
    protected void setup(List<ParameterType> parameterTypes, List<ReturnType> returnTypes) {
        /* Input: iteration count */
        parameterTypes.add(new ParameterType.Builder()
                .setIndex(0)
                .setDataType(Integer.class)
                .build());
        /* Output: current loop index (0-based) */
        returnTypes.add(new ReturnType.Builder()
                .setIndex(0)
                .setDataType(Integer.class)
                .build());
    }

    @Override
    public void execute(Object[] inputs, Object[] outputs, NodeData data) {
        /* No-op: ExecuteTask drives the loop body directly */
    }

    @Override
    public int getExecutionPathOutputCount() {
        return 2;
    }

    @Override
    public String getExecutionPathLabel(int branchIndex) {
        return switch (branchIndex) {
            case LOOP_INDEX -> "LOOP";
            case DONE_INDEX -> "DONE";
            default -> null;
        };
    }

    @Override
    public String getName() {
        return "For Loop";
    }

    @Override
    protected String getNamespace() {
        return "*special";
    }
}
