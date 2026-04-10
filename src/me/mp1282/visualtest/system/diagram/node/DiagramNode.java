package me.mp1282.visualtest.system.diagram.node;

import javafx.beans.property.*;
import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.diagram.port.ExecutionPath;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.iodata.ParameterType;
import me.mp1282.visualtest.system.executable.iodata.ReturnType;
import me.mp1282.visualtest.util.Identifiable;

import java.util.*;

public class DiagramNode extends Identifiable {

    private final Executable executable; /* The wrapped executable */
    private final NodeData data;
    /* The data ports for this node - must be List type for get(index) operation */
    private final List<InputParameter> inputs;
    private final List<OutputReturn> outputs;
    /* Single incoming execution path port */
    private final ExecutionPath nodeBefore;
    /* Outgoing execution path ports — size driven by executable.getExecutionPathOutputCount() */
    private final List<ExecutionPath> nodeAfterPaths;

    /* Position data */
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final DoubleProperty width;
    private final DoubleProperty height;

    public DiagramNode(Executable executable) {
        super();
        this.executable     = executable;
        this.data           = executable.createNodeData();
        this.inputs         = createInputs();
        this.outputs        = createOutputs();
        this.nodeBefore     = new ExecutionPath(this);
        this.nodeAfterPaths = createNodeAfterPaths(executable.getExecutionPathOutputCount());

        this.x      = new SimpleDoubleProperty();
        this.y      = new SimpleDoubleProperty();
        this.width  = new SimpleDoubleProperty();
        this.height = new SimpleDoubleProperty();
    }

    public Executable getExecutable() {
        return executable;
    }

    public NodeData getData() {
        return data;
    }

    public List<InputParameter> getInputs() {
        return inputs;
    }

    public List<OutputReturn> getOutputs() {
        return outputs;
    }

    public ExecutionPath getNodeBefore() {
        return nodeBefore;
    }

    /** Returns all outgoing execution path ports. Most nodes have exactly one. */
    public List<ExecutionPath> getNodeAfterPaths() {
        return nodeAfterPaths;
    }

    /** Convenience accessor for path at the given index. */
    public ExecutionPath getNodeAfterPath(int index) {
        return nodeAfterPaths.get(index);
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    /**
     * Resizes the outgoing execution path list to {@code newCount}.
     * <ul>
     *   <li>Growing: appends new, unconnected {@link ExecutionPath} entries.</li>
     *   <li>Shrinking: disconnects and removes excess entries (both sides of each
     *       removed connection are cleared).</li>
     * </ul>
     */
    public void setExecutionPathCount(int newCount) {
        int current = nodeAfterPaths.size();
        if (newCount < current) {
            for (int i = newCount; i < current; i++) {
                DiagramNode target = nodeAfterPaths.get(i).getOther();
                if (target != null) {
                    target.getNodeBefore().setOther(null);
                    nodeAfterPaths.get(i).setOther(null);
                }
            }
            nodeAfterPaths.subList(newCount, current).clear();
        } else {
            for (int i = current; i < newCount; i++)
                nodeAfterPaths.add(new ExecutionPath(this));
        }
    }

    private List<ExecutionPath> createNodeAfterPaths(int count) {
        List<ExecutionPath> paths = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            paths.add(new ExecutionPath(this));
        }
        return paths; /* mutable — setExecutionPathCount() may grow/shrink it */
    }

    private List<InputParameter> createInputs() {
        List<InputParameter> inputs = new ArrayList<>();

        for (ParameterType type : executable.getParameterTypes()) {
            inputs.add(new InputParameter(this, type));
        }

        return Collections.unmodifiableList(inputs);
    }

    private List<OutputReturn> createOutputs() {
        List<OutputReturn> outputs = new ArrayList<>();

        for (ReturnType type : executable.getReturnTypes()) {
            outputs.add(new OutputReturn(this, type));
        }

        return Collections.unmodifiableList(outputs);
    }

    @Override
    public String toString() {
        return "DiagramNode{" +
                "executable=" + executable.getName() +
                '}';
    }
}