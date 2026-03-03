package me.mp1282.visualtest.system.diagram.runtime;

import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.data.ParameterType;

import java.util.*;

public class ExecuteTask {

    private static final Comparator<DiagramNode> NODE_COMPARATOR =
            Comparator.comparingInt(node -> node.executionCostProperty().get());

    private final Diagram diagram;
    private final List<DiagramNode> executionOrder;
    private final Map<IDataPort<ParameterType>, Object> inputPortToDataMap;

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

    public List<DiagramNode> getExecutionOrder() {
        return executionOrder;
    }

    public boolean canStep() {
        return !executionOrder.isEmpty();
    }

    public RunStep step(System.Logger log) throws Exception {
        final DiagramNode currentNode = executionOrder.removeFirst();
        final Executable exe = currentNode.getExecutable();

        final RunStep result = new RunStep(diagram, currentNode);

        /* Prepare inputs for current node */
        Object[] inputs = new Object[exe.getNumberOfParameters()];
        for(IDataPort<ParameterType> inputPort : currentNode.getInputs()) {
            Object inputValue = inputPortToDataMap.remove(inputPort);
            inputs[inputPort.getType().getIndex()] = inputValue;
        }

        result.setInputs(inputs);

        /* Prepare outputs for current node */
        Object[] outputs = new Object[exe.getNumberOfReturnValues()];

        /* Execute this node */
        log.log(System.Logger.Level.INFO, "Executing: " + exe.getName());
        exe.execute(inputs, outputs, currentNode.getData());

        /* Store outputs */
        for(OutputReturn outputPort : currentNode.getOutputs()) {
            Object output = outputs[outputPort.getType().getIndex()];
            log.log(System.Logger.Level.INFO, "Output: " + output);
            IDataPort<ParameterType> nextInput = outputPort.getTo();

            inputPortToDataMap.put(nextInput, output);
        }

        result.setOutputs(outputs);

        return result;
    }
}
