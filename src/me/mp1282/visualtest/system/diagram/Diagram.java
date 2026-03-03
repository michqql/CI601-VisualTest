package me.mp1282.visualtest.system.diagram;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.executable.data.IDataType;

import java.util.*;

public final class Diagram {

    private final BooleanProperty unsaved;

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
        nodes     .addListener((InvalidationListener) _ -> unsaved.set(true));
        name      .addListener((_, _, _)                -> unsaved.set(true));
        translateX.addListener((_, _, _)                -> unsaved.set(true));
        translateY.addListener((_, _, _)                -> unsaved.set(true));
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

        disconnectExecutionPath(node);
    }

    /* Create a flow connection between two flow ports */
    public boolean connectExecutionPath(DiagramNode source, DiagramNode target) {

        if(canConnectExecutionPath(source, target) && canConnectDataPorts(source, target)) {
            source.getNodeAfter().setOther(target);
            target.getNodeBefore().setOther(source);
            return true;
        }
        return false;
    }

    public void disconnectExecutionPath(DiagramNode node) {
        if(node.getNodeBefore().getOther() != null)
            node.getNodeBefore().getOther().getNodeAfter().setOther(null);

        if(node.getNodeAfter().getOther() != null)
            node.getNodeAfter().getOther().getNodeBefore().setOther(null);

        node.getNodeBefore().setOther(null);
        node.getNodeAfter().setOther(null);
    }

    /* Create a connection between two data ports */
    public boolean connectDataPorts(OutputReturn output, InputParameter input) {
        if(canConnectExecutionPath(output.getParentNode(), input.getParentNode()) &&
                canConnectDataPorts(output.getParentNode(), input.getParentNode())) {
            output.setTo(input);
            input.setFrom(output);
            return true;
        }
        return false;
    }

    /* Removes a connection between two data ports provided one of them */
    public void disconnectDataPort(IDataPort<? extends IDataType> dataPort) {
        if(dataPort.getOther() != null)
            dataPort.getOther().disconnect();
        dataPort.disconnect();
    }

    /* Check if two flow ports can be connected together */
    public boolean canConnectExecutionPath(DiagramNode sourceNode, DiagramNode targetNode) {
        /* Check to see if this connection would result in a cyclic dependency */
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

            /* Add connected execution path nodes that haven't already been visited */
            final DiagramNode before = currentNode.getNodeBefore().getOther();
            final DiagramNode after  = currentNode.getNodeAfter ().getOther();
            if(before != null && !visitedNodes.contains(before))
                nodesToVisit.add(before);

            if(after != null && !visitedNodes.contains(after))
                nodesToVisit.add(after);
        }

        /* No cycle was detected, this connection is valid */
        return true;
    }

    /* Check if two data ports can be connected together */
    public boolean canConnectDataPorts(DiagramNode sourceNode, DiagramNode targetNode) {
        /* Check to see if this connection would result in a cyclic dependency */
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
}
