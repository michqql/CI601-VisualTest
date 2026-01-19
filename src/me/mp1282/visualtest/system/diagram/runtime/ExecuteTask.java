package me.mp1282.visualtest.system.diagram.runtime;

import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.system.executable.DataPort;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.util.Pair;

import java.util.*;

public class ExecuteTask {

    private static final Comparator<DiagramNode> NODE_COMPARATOR =
            Comparator.comparingInt(node -> node.executionCostProperty().get());

    private final Diagram diagram;
    private final List<DiagramNode> executionOrder;
    private final Map<Pair<DiagramNode, DataPort>, Object> inputPortToDataMap;

    public ExecuteTask(Diagram diagram) {
        this.diagram = diagram;
        this.executionOrder = new ArrayList<>();
        this.inputPortToDataMap = new HashMap<>();

        createExecutionOrder();
    }

    private void createExecutionOrder() {
        executionOrder.addAll(diagram.nodesProperty());
        executionOrder.sort(NODE_COMPARATOR);
    }

    public boolean canStep() {
        return !executionOrder.isEmpty();
    }

    public void step(System.Logger log) throws Exception {
        final DiagramNode currentNode = executionOrder.removeFirst();
        final Executable exe = currentNode.getExecutable();

        /* Prepare inputs for current node */
        Object[] inputs = new Object[exe.getNumberOfInputs()];
        for(DataPort inputPort : exe.getInputs()) {
            Object input = inputPortToDataMap.remove(new Pair<>(currentNode, inputPort));
            inputs[inputPort.portIndex()] = input;
        }

        /* Prepare outputs for current node */
        Object[] outputs = new Object[exe.getNumberOfOutputs()];

        /* Execute this node */
        log.log(System.Logger.Level.INFO, "Executing: " + exe.getName());
        exe.execute(inputs, outputs);

        /* Store outputs */
        for(DataPort outputPort : exe.getOutputs()) {
            Object output = outputs[outputPort.portIndex()];
            log.log(System.Logger.Level.INFO, "Output: " + output);
            Pair<DiagramNode, DataPort> nextInput = currentNode.dataConnectionsProperty()
                    .get(outputPort);

            inputPortToDataMap.put(nextInput, output);
        }
    }
}
