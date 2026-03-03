package me.mp1282.visualtest.system.diagram;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.data.IDataType;
import me.mp1282.visualtest.util.DiagramZoomLevel;

import java.util.*;

public final class Diagram {

    private final BooleanProperty unsaved;

    /* Basic information */
    private final ObservableList<DiagramNode> nodes;
    private final StringProperty name;

    /* Position data */
    private final DoubleProperty translateX;
    private final DoubleProperty translateY;
    private final ObjectProperty<DiagramZoomLevel> zoomLevel;

    public Diagram() {
        this.unsaved       = new SimpleBooleanProperty();
        this.nodes         = FXCollections.observableArrayList();
        this.name          = new SimpleStringProperty("Diagram-" + UUID.randomUUID());
        this.translateX    = new SimpleDoubleProperty();
        this.translateY    = new SimpleDoubleProperty();
        this.zoomLevel     = new SimpleObjectProperty<>(DiagramZoomLevel.DEFAULT);

        /* When a property value changes, mark the diagram as unsaved */
        nodes     .addListener((InvalidationListener) _ -> unsaved.set(true));
        name      .addListener((_, _, _)                -> unsaved.set(true));
        translateX.addListener((_, _, _)                -> unsaved.set(true));
        translateY.addListener((_, _, _)                -> unsaved.set(true));
    }

    public BooleanProperty unsavedProperty() {
        return unsaved;
    }

    public ObservableList<DiagramNode> nodesProperty() {
        return nodes;
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

    public ObjectProperty<DiagramZoomLevel> zoomLevelProperty() {
        return zoomLevel;
    }

    /* Place an executable onto the diagram */
    public DiagramNode placeExecutable(Executable executable) {
        DiagramNode node = new DiagramNode(executable);
        nodes.add(node);
        return node;
    }

    /* Create a flow connection between two flow ports */
    public boolean connectExecutionPath(DiagramNode source, DiagramNode target) {

//        if(canConnectExecutionPath(source, target) && canConnectDataPorts(source, target)) {
//            source.executionPathNodeAfterProperty().set(target);
//            target.executionPathNodeBeforeProperty().set(source);
//            return true;
//        }
        return false;
    }

    public void disconnectExecutionPath(DiagramNode node) {
//        final DiagramNode before = node.executionPathNodeBeforeProperty().get();
//        final DiagramNode after  = node.executionPathNodeAfterProperty().get();
//
//        if(before != null) before.executionPathNodeAfterProperty().set(null);
//        if(after  != null) after .executionPathNodeBeforeProperty().set(null);
//
//        /* Set the before and after for the node passed to this function to null */
//        node.executionPathNodeBeforeProperty().set(null);
//        node.executionPathNodeAfterProperty().set(null);
    }

    /* Create a connection between two data ports */
    public boolean connectDataPorts(OutputReturn output, InputParameter input) {

//        /* The source and target port cannot be both inputs or both outputs */
//        if(sourcePort.inputPort() == targetPort.inputPort())
//            return false;
//
//        if(canConnectExecutionPath(source, target) && canConnectDataPorts(source, target)) {
//            source.dataConnectionsProperty().put(sourcePort, new Pair<>(target, targetPort));
//            target.dataConnectionsProperty().put(targetPort, new Pair<>(source, sourcePort));
//            return true;
//        }
        return false;
    }

    /* Removes a connection between two data ports provided one of them */
    public void disconnectDataPort(IDataPort<? extends IDataType> dataPort) {
//        Pair<DiagramNode, DataPort> pair = source.dataConnectionsProperty().remove(sourcePort);
//        if(pair != null)
//            pair.key().dataConnectionsProperty().remove(pair.value());
    }

    /* Check if two flow ports can be connected together */
    public boolean canConnectExecutionPath(DiagramNode sourceNode, DiagramNode targetNode) {
//        /* Check to see if this connection would result in a cyclic dependency */
//        Set<DiagramNode> visitedNodes = new HashSet<>();
//        Queue<DiagramNode> nodesToVisit = new LinkedList<>();
//        nodesToVisit.add(targetNode);
//
//        while(!nodesToVisit.isEmpty()) {
//            DiagramNode currentNode = nodesToVisit.poll();
//            if(currentNode.equals(sourceNode)) {
//                /* Cycle detected */
//                return false;
//            }
//
//            visitedNodes.add(currentNode);
//
//            /* Add connected flow nodes that haven't already been visited */
//            PropertyHelper.whenPresentForEach(
//                    List.of(currentNode.executionPathNodeBeforeProperty(), currentNode.executionPathNodeAfterProperty()),
//                    node -> {
//                        if(!visitedNodes.contains(node))
//                            nodesToVisit.add(node);
//                    });
//        }

        /* No cycle was detected, this connection is valid */
        return true;
    }

    /* Check if two data ports can be connected together */
    public boolean canConnectDataPorts(DiagramNode sourceNode, DiagramNode targetNode) {
//        /* Check to see if this connection would result in a cyclic dependency */
//        Set<DiagramNode> visitedNodes = new HashSet<>();
//        Queue<DiagramNode> nodesToVisit = new LinkedList<>();
//        nodesToVisit.add(targetNode);
//        while(!nodesToVisit.isEmpty()) {
//            DiagramNode currentNode = nodesToVisit.poll();
//            if(currentNode.equals(sourceNode)) {
//                /* Cycle detected */
//                return false;
//            }
//
//            visitedNodes.add(currentNode);
//
//            for(Pair<DiagramNode, DataPort> connectedNode : currentNode.dataConnectionsProperty().values()) {
//                if(!visitedNodes.contains(connectedNode.key())) {
//                    nodesToVisit.add(connectedNode.key());
//                }
//            }
//        }

        /* No cycle was detected, this connection is valid */
        return true;
    }

    public DiagramNode getNodeByUniqueId(UUID uuid) {
        for(DiagramNode node : nodes) {
            if(node.getUniqueId().equals(uuid))
                return node;
        }

        return null;
    }

    public Set<Executable> getExecutablesWithinDiagram() {
        /* Turn the list of nodes into a set of executables */
        Set<Executable> executables = new HashSet<>();
        for (DiagramNode node : nodes)
            executables.add(node.getExecutable());

        return executables;
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
