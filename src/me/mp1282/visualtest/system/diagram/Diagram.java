package me.mp1282.visualtest.system.diagram;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.ExecutionPath;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.executable.iodata.IDataType;

import java.util.*;

public final class Diagram {

    private final BooleanProperty unsaved;
    private final Map<DiagramNode, InvalidationListener> nodeListeners = new HashMap<>();

    /* Basic information */
    private final ReadOnlyListWrapper<DiagramNode> nodes;
    private final StringProperty name;

    /* Position data */
    private final DoubleProperty translateX;
    private final DoubleProperty translateY;

    public Diagram() {
        this.unsaved       = new SimpleBooleanProperty();
        this.nodes         = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
        this.name          = new SimpleStringProperty("Diagram-" + UUID.randomUUID());
        this.translateX    = new SimpleDoubleProperty();
        this.translateY    = new SimpleDoubleProperty();

        /* When a property value changes, mark the diagram as unsaved */
        nodes.addListener((ListChangeListener<DiagramNode>) change -> {
            unsaved.set(true);
            while (change.next()) {
                change.getAddedSubList().forEach(this::watchNode);
                change.getRemoved().forEach(this::unwatchNode);
            }
        });
        name      .addListener((_, _, _) -> unsaved.set(true));
        translateX.addListener((_, _, _) -> unsaved.set(true));
        translateY.addListener((_, _, _) -> unsaved.set(true));
    }

    public BooleanProperty unsavedProperty() {
        return unsaved;
    }

    public ReadOnlyListProperty<DiagramNode> nodesProperty() {
        return nodes.getReadOnlyProperty();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public DoubleProperty translateXProperty() {
        return translateX;
    }

    public DoubleProperty translateYProperty() {
        return translateY;
    }

    /* Add and remove diagram nodes */
    public void addDiagramNode(DiagramNode node) {
        this.nodes.add(node);
    }

    public void removeDiagramNode(DiagramNode node) {
        if(this.nodes.remove(node)) {
            /* Was removed from the list; remove connections */
            disconnectNode(node);
        }
    }

    public void removeDiagramNodes(Collection<DiagramNode> nodesToRemove) {
        this.nodes.removeAll(nodesToRemove);

        /* TODO: This assumes all nodes are contained within this diagram, and disconnects their connections blindly */
        for (DiagramNode node : nodesToRemove) {
            disconnectNode(node);
        }
    }

    private void disconnectNode(DiagramNode node) {
        for (InputParameter input : node.getInputs()) {
            disconnectDataPort(input);
        }

        for (OutputReturn output : node.getOutputs()) {
            disconnectDataPort(output);
        }

        disconnectAllExecutionPaths(node);
    }

    /**
     * Connects the execution path from {@code source @ branchIndex} to {@code target}.
     * @param source The source node
     * @param branchIndex The source branch index
     * @param target The target node
     * @return {@code true} if the connection was made successfully.
     */
    public boolean connectExecutionPath(DiagramNode source, int branchIndex, DiagramNode target) {
        if(canConnectExecutionPath(source, target) && canConnectDataPorts(source, target)) {
            source.getNodeAfterPath(branchIndex).setOther(target);
            target.getNodeBefore().setOther(source);
            unsaved.set(true);
            return true;
        }
        return false;
    }

    /**
     * Disconnects the specific execution path between {@code source} and {@code target},
     * identified by finding which of source's outgoing paths points to target.
     */
    public void disconnectExecutionPath(DiagramNode source, DiagramNode target) {
        for (ExecutionPath afterPath : source.getNodeAfterPaths()) {
            if (afterPath.getOther() == target) {
                afterPath.setOther(null);
                target.getNodeBefore().setOther(null);
                unsaved.set(true);
                return;
            }
        }
    }

    /**
     * Disconnects ALL execution paths connected to {@code node} — both its incoming
     * (nodeBefore) and all outgoing paths. Used when removing a node from the diagram.
     */
    public void disconnectAllExecutionPaths(DiagramNode node) {
        /* Sever the incoming link: find which afterPath of the predecessor points here */
        if (node.getNodeBefore().getOther() != null) {
            DiagramNode predecessor = node.getNodeBefore().getOther();
            for (ExecutionPath afterPath : predecessor.getNodeAfterPaths()) {
                if (afterPath.getOther() == node) {
                    afterPath.setOther(null);
                    break;
                }
            }
            node.getNodeBefore().setOther(null);
        }

        /* Sever all outgoing links */
        for (ExecutionPath afterPath : node.getNodeAfterPaths()) {
            if (afterPath.getOther() != null) {
                afterPath.getOther().getNodeBefore().setOther(null);
                afterPath.setOther(null);
            }
        }
    }

    /* Create a connection between two data ports */
    public boolean connectDataPorts(OutputReturn output, InputParameter input) {
        if (!areDataTypesCompatible(output.getType().getDataType(), input.getType().getDataType()))
            return false;
        if(canConnectExecutionPath(output.getParentNode(), input.getParentNode()) &&
                canConnectDataPorts(output.getParentNode(), input.getParentNode())) {
            output.setTo(input);
            input.setFrom(output);
            unsaved.set(true);
            return true;
        }
        return false;
    }

    /**
     * Returns true if a value of {@code outputType} can be assigned to a port expecting
     * {@code inputType}. Primitive types are normalised to their wrapper equivalents so
     * that {@code int} and {@code Integer} are treated as compatible.
     */
    public static boolean areDataTypesCompatible(Class<?> outputType, Class<?> inputType) {
        if (outputType == null || inputType == null) return false;
        return wrap(inputType).isAssignableFrom(wrap(outputType));
    }

    private static Class<?> wrap(Class<?> c) {
        if (!c.isPrimitive()) return c;
        if (c == int.class)     return Integer.class;
        if (c == long.class)    return Long.class;
        if (c == double.class)  return Double.class;
        if (c == float.class)   return Float.class;
        if (c == boolean.class) return Boolean.class;
        if (c == byte.class)    return Byte.class;
        if (c == short.class)   return Short.class;
        if (c == char.class)    return Character.class;
        return c;
    }

    /* Removes a connection between two data ports provided one of them */
    public void disconnectDataPort(IDataPort<? extends IDataType> dataPort) {
        if(dataPort.getOther() != null)
            dataPort.getOther().disconnect();
        dataPort.disconnect();
        unsaved.set(true);
    }

    /* Check if connecting source -> target via an execution path would create a cycle */
    public boolean canConnectExecutionPath(DiagramNode sourceNode, DiagramNode targetNode) {
        Set<DiagramNode> visitedNodes = new HashSet<>();
        Queue<DiagramNode> nodesToVisit = new LinkedList<>();
        nodesToVisit.add(targetNode);

        while(!nodesToVisit.isEmpty()) {
            DiagramNode currentNode = nodesToVisit.poll();
            if(currentNode.equals(sourceNode)) {
                /* Cycle detected */
                return false;
            }

            visitedNodes.add(currentNode);

            /* Follow incoming execution path */
            final DiagramNode before = currentNode.getNodeBefore().getOther();
            if(before != null && !visitedNodes.contains(before))
                nodesToVisit.add(before);

            /* Follow all outgoing execution paths */
            for (ExecutionPath afterPath : currentNode.getNodeAfterPaths()) {
                DiagramNode after = afterPath.getOther();
                if(after != null && !visitedNodes.contains(after))
                    nodesToVisit.add(after);
            }
        }

        /* No cycle was detected, this connection is valid */
        return true;
    }

    /* Check if two data ports can be connected together by checking
     * if a cyclic dependency would be created
     */
    public boolean canConnectDataPorts(DiagramNode sourceNode, DiagramNode destinationNode) {
        Set<DiagramNode> visitedNodes = new HashSet<>();      /* The set of nodes already visited */
        Queue<DiagramNode> nodesToVisit = new LinkedList<>(); /* FIFO queue of nodes to visit     */
        nodesToVisit.add(destinationNode);                    /* Start at the destination node    */
        while(!nodesToVisit.isEmpty()) {
            DiagramNode currentNode = nodesToVisit.poll();
            /* If the source node can be reached (by being a node being processed
             * from the queue of nodes) then a cycle would be created from source to destination
             */
            if(currentNode.equals(sourceNode))
                return false; /* Cycle detected */

            visitedNodes.add(currentNode);

            /* The input data ports */
            for (InputParameter input : currentNode.getInputs()) {
                if(input.getFrom() != null && !visitedNodes.contains(input.getFrom().getParentNode()))
                    nodesToVisit.add(input.getFrom().getParentNode());
            }

            /* The output data ports */
            for(OutputReturn output : currentNode.getOutputs()) {
                if(output.getTo() != null && !visitedNodes.contains(output.getTo().getParentNode()))
                    nodesToVisit.add(output.getTo().getParentNode());
            }
        }

        /* No cycle was detected, this connection is valid */
        return true;
    }

    private void watchNode(DiagramNode node) {
        InvalidationListener listener = _ -> unsaved.set(true);
        nodeListeners.put(node, listener);
        node.xProperty().addListener(listener);
        node.yProperty().addListener(listener);
        node.getData().addInvalidationListener(listener);
    }

    private void unwatchNode(DiagramNode node) {
        InvalidationListener listener = nodeListeners.remove(node);
        if (listener == null) return;
        node.xProperty().removeListener(listener);
        node.yProperty().removeListener(listener);
    }

    public DiagramNode getDiagramNodeByUniqueId(UUID uuid) {
        /* Loop over all diagram nodes and return if an equivalent UUID is found */
        for (DiagramNode node : nodes) {
            if(node.getUniqueId().equals(uuid))
                return node;
        }

        return null;
    }

    public List<IDataPort<?>> getDataPortsWithoutConnections() {
        List<IDataPort<?>> unconnectedPorts = new ArrayList<>();

        for(DiagramNode node : nodes) {
            /* Check input ports */
            for(InputParameter input : node.getInputs()) {
                if(input.getFrom() == null)
                    unconnectedPorts.add(input);
            }

            /* Check output ports */
            for(OutputReturn output : node.getOutputs()) {
                if(output.getTo() == null)
                    unconnectedPorts.add(output);
            }
        }

        return unconnectedPorts;
    }

    public List<OutputReturn> getAllConnectedOutputs() {
        List<OutputReturn> list = new ArrayList<>();

        for(DiagramNode node : nodes) {
            for (OutputReturn output : node.getOutputs()) {
                /* If the output is connected, add to the list */
                if(output.getTo() != null)
                    list.add(output);
            }
        }

        return list;
    }

    /** Returns all execution path connections in the diagram as (source, branchIndex, target) tuples. */
    public List<ExecutionPathConnection> getAllExecutionPathConnections() {
        List<ExecutionPathConnection> connections = new ArrayList<>();
        for (DiagramNode node : nodes) {
            List<ExecutionPath> paths = node.getNodeAfterPaths();
            for (int i = 0; i < paths.size(); i++) {
                if (paths.get(i).getOther() != null)
                    connections.add(new ExecutionPathConnection(node, i, paths.get(i).getOther()));
            }
        }
        return connections;
    }

    public record ExecutionPathConnection(DiagramNode source, int branchIndex, DiagramNode target) {}
}