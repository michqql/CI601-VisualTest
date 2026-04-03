package me.mp1282.visualtest.system.diagram.runtime;

import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.ExecutionPath;
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
    private final Set<DiagramNode> skippedNodes;

    public ExecuteTask(Diagram diagram) {
        this.diagram          = diagram;
        this.executionOrder   = new ArrayList<>();
        this.inputPortToDataMap = new HashMap<>();
        this.skippedNodes     = new HashSet<>();

        createExecutionOrder();
    }

    /* Performs a topological sort (Kahn's algorithm) of the diagram nodes.
     * Both data port edges and execution path edges are used as ordering constraints.
     * If a cycle is detected, an exception is thrown.
     */
    private void createExecutionOrder() {
        final List<DiagramNode> nodes = new ArrayList<>(diagram.nodesProperty());
        final Map<DiagramNode, Integer> indegree = new HashMap<>();
        final Queue<DiagramNode> next = new ArrayDeque<>();

        /* 1. Calculate indegree by counting incoming data edges AND incoming execution path edges */
        for (DiagramNode node : nodes) {
            /* Incoming data edges */
            for (InputParameter input : node.getInputs()) {
                if (input.getFrom() != null)
                    indegree.compute(node, (_, degree) -> degree == null ? 1 : ++degree);
            }

            /* Incoming execution path edge (nodeBefore) */
            if (node.getNodeBefore().getOther() != null)
                indegree.compute(node, (_, degree) -> degree == null ? 1 : ++degree);

            /* 2. Nodes with zero indegree are processed first */
            if (indegree.getOrDefault(node, 0) == 0)
                next.add(node);
        }

        /* 3. Process the queue — decrement neighbours and add newly zero-indegree nodes */
        while(!next.isEmpty()) {
            DiagramNode top = next.poll();
            this.executionOrder.add(top);

            /* Decrement data-connected downstream nodes */
            for (OutputReturn output : top.getOutputs()) {
                if(output.getTo() != null) {
                    int resultingDegree = indegree.compute(output.getTo().getParentNode(),
                            (_, degree) -> degree == null ? -1 : --degree);
                    if(resultingDegree == 0)
                        next.add(output.getTo().getParentNode());
                }
            }

            /* Decrement execution-path-connected downstream nodes */
            for (ExecutionPath afterPath : top.getNodeAfterPaths()) {
                if (afterPath.getOther() != null) {
                    int resultingDegree = indegree.compute(afterPath.getOther(),
                            (_, degree) -> degree == null ? -1 : --degree);
                    if (resultingDegree == 0)
                        next.add(afterPath.getOther());
                }
            }
        }

        /* Check for cycles */
        if(executionOrder.size() != nodes.size())
            throw new RuntimeException("Cycle detected");
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

        /* If this node is on a skipped branch, propagate nulls and forward the skip */
        if (skippedNodes.contains(currentNode)) {
            for (OutputReturn outputPort : currentNode.getOutputs()) {
                if (outputPort.getTo() != null)
                    inputPortToDataMap.put(outputPort.getTo(), null);
            }
            /* Propagate the skip along all outgoing execution paths */
            for (ExecutionPath afterPath : currentNode.getNodeAfterPaths()) {
                markSkipped(afterPath.getOther());
            }
            return result;
        }

        /* Prepare inputs for current node */
        Object[] inputs = new Object[exe.getNumberOfParameters()];
        for(IDataPort<ParameterType> inputPort : currentNode.getInputs()) {
            Object inputValue = inputPortToDataMap.remove(inputPort);
            inputs[inputPort.getType().getIndex()] = inputValue;
        }

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

        /* If this is a multi-branch node, determine which path to skip */
        List<ExecutionPath> afterPaths = currentNode.getNodeAfterPaths();
        if (afterPaths.size() > 1) {
            int chosenIndex = exe.getChosenBranchIndex(inputs);
            for (int i = 0; i < afterPaths.size(); i++) {
                if (i != chosenIndex)
                    markSkipped(afterPaths.get(i).getOther());
            }
        }

        return result;
    }

    /* BFS from start, marking all reachable nodes (via execution paths) as skipped */
    private void markSkipped(DiagramNode start) {
        if (start == null) return;
        Queue<DiagramNode> toProcess = new LinkedList<>();
        toProcess.add(start);
        while (!toProcess.isEmpty()) {
            DiagramNode node = toProcess.poll();
            if (skippedNodes.contains(node)) continue;
            skippedNodes.add(node);
            for (ExecutionPath afterPath : node.getNodeAfterPaths()) {
                if (afterPath.getOther() != null)
                    toProcess.add(afterPath.getOther());
            }
        }
    }
}
