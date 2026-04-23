package me.mp1282.visualtest.system.diagram.runtime;

import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.ExecutionPath;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.iodata.ParameterType;
import me.mp1282.visualtest.system.inbuilt.special.ForLoopExecutable;

import java.util.*;

public class ExecuteTask {

    private final Diagram diagram;
    private final List<DiagramNode> executionOrder;
    private final Map<IDataPort<ParameterType>, Object> inputPortToDataMap;
    private final Set<DiagramNode> skippedNodes;
    private final Map<OutputReturn, Object> outputPortToLastValueMap;

    public ExecuteTask(Diagram diagram) {
        this.diagram          = diagram;
        this.executionOrder   = new ArrayList<>();
        this.inputPortToDataMap = new HashMap<>();
        this.skippedNodes     = new HashSet<>();
        this.outputPortToLastValueMap = new HashMap<>();

        createExecutionOrder();
    }

    /* Performs a topological sort (Kahn's algorithm) of the diagram nodes.
     * Both data port edges and execution path edges are used as ordering constraints.
     * If a cycle is detected, an exception is thrown.
     * Time complexity: O(V + E)
     */
    private void createExecutionOrder() {
        final List<DiagramNode> nodes = new ArrayList<>(diagram.nodesProperty());
        final Map<DiagramNode, Integer> indegree = new HashMap<>();
        final Queue<DiagramNode> next = new ArrayDeque<>();

        /* 1. In-degree (number of incoming edges into a vertex) is calculated for
         *    each node. A queue is created from nodes with an in-degree of zero.
         */
        for (DiagramNode node : nodes) {
            /* Incoming data edges */
            for (InputParameter input : node.getInputs()) {
                if (input.getFrom() != null)
                    indegree.compute(node, (_, degree) -> degree == null ? 1 : ++degree);
            }

            /* Incoming execution path edge (nodeBefore) */
            if (node.getNodeBefore().getOther() != null)
                indegree.compute(node, (_, degree) -> degree == null ? 1 : ++degree);

            /* Nodes with zero indegree are processed first */
            if (indegree.getOrDefault(node, 0) == 0)
                next.add(node);
        }

        /* 2. Process the queue: the head is added to the ordered list, it's neighbours
         *    (via output edges) have their in-degree decreased.
         */
        while(!next.isEmpty()) {
            DiagramNode top = next.poll();
            this.executionOrder.add(top);

            /* Decrement data-connected downstream nodes */
            for (OutputReturn output : top.getOutputs()) {
                if(output.getTo() != null) {
                    int resultingDegree = indegree.compute(
                            /* Key          => */ output.getTo().getParentNode(),
                            /* Mapping Func => */ (_, degree) -> degree == null ? -1 : --degree
                    );

                    if(resultingDegree == 0) next.add(output.getTo().getParentNode());
                }
            }

            /* Decrement execution-path-connected downstream nodes,
             * same operation as data-connections */
            for (ExecutionPath afterPath : top.getNodeAfterPaths()) {
                if (afterPath.getOther() != null) {
                    int resultingDegree = indegree.compute(
                            /* Key          => */ afterPath.getOther(),
                            /* Mapping Func => */ (_, degree) -> degree == null ? -1 : --degree
                    );

                    if (resultingDegree == 0)
                        next.add(afterPath.getOther());
                }
            }
        }

        /* 3. Check for cycles */
        if(executionOrder.size() != nodes.size())
            throw new RuntimeException("Cycle detected");
    }

    public Diagram getDiagram() {
        return diagram;
    }

    public List<DiagramNode> getExecutionOrder() {
        return executionOrder;
    }

    public Set<DiagramNode> getSkippedNodes() {
        return Collections.unmodifiableSet(skippedNodes);
    }

    public Object getOutputPortValue(OutputReturn port) {
        return outputPortToLastValueMap.get(port);
    }

    public boolean canStep() {
        return !executionOrder.isEmpty();
    }

    public RunStep step(System.Logger log) throws Exception {
        final DiagramNode currentNode = executionOrder.removeFirst();
        final Executable exe = currentNode.getExecutable();

        final RunStep result = new RunStep(diagram, currentNode);

        /* If this node is on a skipped branch, propagate skip flag */
        if (skippedNodes.contains(currentNode)) {
            /* Propagate the skip to all output data connections */
            for (OutputReturn outputPort : currentNode.getOutputs()) {
                if (outputPort.getTo() != null) {
                    inputPortToDataMap.put(outputPort.getTo(), null);
                    markSkipped(outputPort.getTo().getParentNode());
                }
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
        result.setInputs(inputs);
        result.setOutputs(outputs);

        /* ForLoop: execute body nodes inline N times instead of the normal path */
        if (exe instanceof ForLoopExecutable) {
            handleForLoop(currentNode, inputs);
            return result;
        }

        /* Store outputs */
        for(OutputReturn outputPort : currentNode.getOutputs()) {
            Object output = outputs[outputPort.getType().getIndex()];
            log.log(System.Logger.Level.INFO, "Output: " + output);
            IDataPort<ParameterType> nextInput = outputPort.getTo();

            outputPortToLastValueMap.put(outputPort, output);
            inputPortToDataMap.put(nextInput, output);
        }

        /* If this is a multi-branch node, determine which path to skip */
        List<ExecutionPath> afterPaths = currentNode.getNodeAfterPaths();
        if (afterPaths.size() > 1) {
            int chosenIndex = exe.getChosenBranchIndex(inputs, currentNode.getData());
            for (int i = 0; i < afterPaths.size(); i++) {
                if (i != chosenIndex)
                    markSkipped(afterPaths.get(i).getOther());
            }
        }

        return result;
    }

    /**
     * Handles inline execution of a ForLoop node's body subgraph.
     * <p>
     * The loop body is identified as all nodes that currently sit at the head of
     * {@link #executionOrder} up to (but not including) the node connected via the
     * DONE path. Those nodes are removed from {@code executionOrder} and executed
     * {@code count} times, with the loop index output updated each iteration.
     * After the loop finishes, the DONE path continues normally.
     */
    private void handleForLoop(DiagramNode loopNode, Object[] inputs) throws Exception {
        int count = (inputs[0] instanceof Number n) ? n.intValue() : 0;
        if (count < 0) count = 0;

        /* The DONE path node is the boundary between body and post-loop graph */
        DiagramNode doneNode = loopNode.getNodeAfterPath(ForLoopExecutable.DONE_INDEX).getOther();

        /* Pull loop body nodes out of the execution queue */
        List<DiagramNode> loopBody = new ArrayList<>();
        while (!executionOrder.isEmpty() && executionOrder.get(0) != doneNode) {
            loopBody.add(executionOrder.removeFirst());
        }

        /* Identify inputs to loop body nodes that originate from OUTSIDE the loop.
         * These must be restored before each iteration because the normal execution
         * consumes (removes) entries from inputPortToDataMap. */
        Set<DiagramNode> bodySet = new HashSet<>(loopBody);
        Map<IDataPort<ParameterType>, Object> outsideInputSnapshot = new HashMap<>();
        for (DiagramNode bodyNode : loopBody) {
            for (InputParameter port : bodyNode.getInputs()) {
                if (port.getFrom() == null || !bodySet.contains(port.getFrom().getParentNode())) {
                    outsideInputSnapshot.put(port, inputPortToDataMap.get(port));
                }
            }
        }

        /* Execute loop body 'count' times */
        for (int i = 0; i < count; i++) {
            /* Restore external inputs (consumed on previous iteration) */
            inputPortToDataMap.putAll(outsideInputSnapshot);

            /* Publish the current index on ForLoop's output port */
            for (OutputReturn outputPort : loopNode.getOutputs()) {
                if (outputPort.getTo() != null)
                    inputPortToDataMap.put(outputPort.getTo(), i);
            }

            /* Execute each body node in topological order */
            for (DiagramNode bodyNode : loopBody) {
                if (skippedNodes.contains(bodyNode)) continue;

                Executable bodyExe = bodyNode.getExecutable();
                Object[] bodyInputs  = new Object[bodyExe.getNumberOfParameters()];
                Object[] bodyOutputs = new Object[bodyExe.getNumberOfReturnValues()];

                for (IDataPort<ParameterType> port : bodyNode.getInputs())
                    bodyInputs[port.getType().getIndex()] = inputPortToDataMap.remove(port);

                bodyExe.execute(bodyInputs, bodyOutputs, bodyNode.getData());

                for (OutputReturn outputPort : bodyNode.getOutputs()) {
                    if (outputPort.getTo() != null)
                        inputPortToDataMap.put(outputPort.getTo(), bodyOutputs[outputPort.getType().getIndex()]);
                }
            }
        }

        /* Clean up any remaining index output entry so it doesn't leak to doneNode */
        for (OutputReturn outputPort : loopNode.getOutputs())
            inputPortToDataMap.remove(outputPort.getTo());

        /* Mark the LOOP path subgraph as skipped so the runtime does not try to
         * re-execute body nodes (they have already been removed from executionOrder) */
        markSkipped(loopNode.getNodeAfterPath(ForLoopExecutable.LOOP_INDEX).getOther());
    }

    private void markSkipped(DiagramNode start) {
        if(start != null)
            skippedNodes.add(start);
    }
}
