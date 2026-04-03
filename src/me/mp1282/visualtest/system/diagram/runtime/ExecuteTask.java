package me.mp1282.visualtest.system.diagram.runtime;

import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.iodata.ParameterType;

import java.util.*;

public class ExecuteTask {

    private final Diagram diagram;
    private final List<DiagramNode> executionOrder;
    private final Map<IDataPort<ParameterType>, Object> inputPortToDataMap;

    public ExecuteTask(Diagram diagram) {
        this.diagram = diagram;
        this.executionOrder = new ArrayList<>();
        this.inputPortToDataMap = new HashMap<>();

        createExecutionOrder();
    }

    /* Performs a topological sort of the diagram nodes to determine execution order.
     * If a cycle is detected, an exception is thrown.
     */
    private void createExecutionOrder() {
        /* Create copy in-case diagram node list is modified during this operation */
        final List<DiagramNode> nodes = new ArrayList<>(diagram.nodesProperty());
        final Map<DiagramNode, Integer> indegree = new HashMap<>();
        final Queue<DiagramNode> next = new ArrayDeque<>();

        /* 1. Calculate indegree of the vertices by counting number of incoming edges */
        for (DiagramNode node : nodes) {
            for (InputParameter input : node.getInputs()) {
                if (input.getFrom() != null)
                    indegree.compute(node, (_, degree) -> {
                        if (degree == null)
                            degree = 0;

                        return ++degree;
                    });
            }

            /* 2. Add all nodes with an indegree of zero to a queue to be processed first */
            if (indegree.getOrDefault(node, 0) == 0)
                next.add(node);
        }

        /* 3. Process nodes in the queue, adding them to the execution order
         *    and reducing the indegree of their neighbors.
         *    If a neighbor's indegree becomes zero, add it to the queue.
         */
        while(!next.isEmpty()) {
            DiagramNode top = next.poll();
            this.executionOrder.add(top);

            for (OutputReturn output : top.getOutputs()) {
                if(output.getTo() != null) {
                    int resultingDegree = indegree.compute(output.getTo().getParentNode(), (_, degree) -> {
                        if(degree == null)
                            degree = 0;

                        return --degree;
                    });
                    if(resultingDegree == 0)
                        next.add(output.getTo().getParentNode());
                }
            }
        }

        /* Check for cycles */
        if(executionOrder.size() != nodes.size())
            throw new RuntimeException("Cycle detected");

        /* Nodes are sorted */
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

//        result.setInputs(inputs);

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

//        result.setOutputs(outputs);

        return result;
    }
}
