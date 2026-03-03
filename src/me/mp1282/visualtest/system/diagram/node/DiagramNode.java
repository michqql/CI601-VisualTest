package me.mp1282.visualtest.system.diagram.node;

import javafx.beans.property.*;
import me.mp1282.visualtest.system.diagram.port.ExecutionPath;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.data.ParameterType;
import me.mp1282.visualtest.system.executable.data.ReturnType;
import me.mp1282.visualtest.util.Identifiable;

import java.util.*;

public class DiagramNode extends Identifiable {

    private static final int COST_OF_NO_INPUTS          = 0;
    private static final int COST_PER_UNCONNECTED_INPUT = 1;
    private static final int COST_PER_CONNECTED_INPUT   = 2;
    private static final int COST_OF_EXECUTION_PATH     = 5;

    private final Executable executable; /* The wrapped executable */
    private final NodeData data;
    /* The data ports for this node */
    private final Collection<InputParameter> inputs;
    private final Collection<OutputReturn> outputs;
    /* The execution path ports for this node */
    private final ExecutionPath nodeBefore;
    private final ExecutionPath nodeAfter;

    /* The cached execution cost, only recalculated when
     * the executable or its connections changes
     */
    private final IntegerProperty cachedExecutionCost;

    /* Position data */
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final DoubleProperty width;
    private final DoubleProperty height;

    public DiagramNode(Executable executable) {
        super();
        this.executable                  = executable;
        this.data                        = executable.createNodeData();
        this.inputs                      = createInputs();
        this.outputs                     = createOutputs();
        this.nodeBefore                  = new ExecutionPath(this);
        this.nodeAfter                   = new ExecutionPath(this);

        this.cachedExecutionCost         = new SimpleIntegerProperty();
        this.x                           = new SimpleDoubleProperty();
        this.y                           = new SimpleDoubleProperty();
        this.width                       = new SimpleDoubleProperty();
        this.height                      = new SimpleDoubleProperty();

        /* Recalculate execution cost when connections change */
        calculateExecutionCost(); /* Calculate initial value */
    }

    public Executable getExecutable() {
        return executable;
    }

    public NodeData getData() {
        return data;
    }

    public Collection<InputParameter> getInputs() {
        return inputs;
    }

    public Collection<OutputReturn> getOutputs() {
        return outputs;
    }

    public ExecutionPath getNodeBefore() {
        return nodeBefore;
    }

    public ExecutionPath getNodeAfter() {
        return nodeAfter;
    }

    public IntegerProperty executionCostProperty() {
        return cachedExecutionCost;
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

    /* Execution cost is calculated by these rules:
     * - With no inputs, cost is 0
     * - With inputs (not connected), cost is 1 per input
     * - With inputs (connected), cost is sum of input costs + 1.
     */
    private void calculateExecutionCost() {
        int cost = COST_OF_NO_INPUTS;

        /* TODO: Add cost algorithm back */
//        /* Calculate cost for data port inputs */
//        if(executable.getNumberOfInputs() > 0) {
//            for(DataPort input : executable.getInputs()) {
//                Pair<DiagramNode, DataPort> connection = dataPortConnectionMap.get(input);
//                if(connection == null) {
//                    /* Not connected, add base cost */
//                    cost += COST_PER_UNCONNECTED_INPUT;
//                    continue;
//                } else {
//                    /* Connected, add input cost plus extra cost */
//                    cost += connection.key().executionCostProperty().get() + COST_PER_CONNECTED_INPUT;
//                }
//            }
//        }
//
//        /* Calculate cost for execution path */
//        if(executionPathNodeBefore.get() != null)
//            cost += executionPathNodeBefore.get().executionCostProperty().get() + COST_OF_EXECUTION_PATH;

        /* Set final cost */
        cachedExecutionCost.set(cost);
    }

    private Collection<InputParameter> createInputs() {
        List<InputParameter> inputs = new ArrayList<>();

        for (ParameterType type : executable.getParameterTypes()) {
            inputs.add(new InputParameter(this, type));
        }

        return Collections.unmodifiableCollection(inputs);
    }

    private Collection<OutputReturn> createOutputs() {
        List<OutputReturn> outputs = new ArrayList<>();

        for (ReturnType type : executable.getReturnTypes()) {
            outputs.add(new OutputReturn(this, type));
        }

        return Collections.unmodifiableCollection(outputs);
    }

    @Override
    public String toString() {
        return "DiagramNode{" +
                "executable=" + executable.getName() +
                ", executionCost=" + cachedExecutionCost.get() +
                '}';
    }
}
