package me.mp1282.visualtest.system.diagram;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import me.mp1282.visualtest.system.executable.DataPort;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.util.DiagramZoomLevel;
import me.mp1282.visualtest.util.Pair;
import me.mp1282.visualtest.util.PropertyHelper;

import java.util.*;

public final class Diagram {

    /* The extractor is used to monitor changes to the connections
     * of each DiagramNode within the nodes list.
     */
    private static final Callback<DiagramNode, Observable[]> EXTRACTOR = (node) ->
            new Observable[] { node.dataConnectionsProperty() };

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
        this.nodes         = FXCollections.observableArrayList(EXTRACTOR);
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

        if(canConnectExecutionPath(source, target) && canConnectDataPorts(source, target)) {
            source.executionPathNodeAfterProperty().set(target);
            target.executionPathNodeBeforeProperty().set(source);
            return true;
        }
        return false;
    }

    public void disconnectExecutionPath(DiagramNode node) {
        final DiagramNode before = node.executionPathNodeBeforeProperty().get();
        final DiagramNode after  = node.executionPathNodeAfterProperty().get();

        if(before != null) before.executionPathNodeAfterProperty().set(null);
        if(after  != null) after .executionPathNodeBeforeProperty().set(null);

        /* Set the before and after for the node passed to this function to null */
        node.executionPathNodeBeforeProperty().set(null);
        node.executionPathNodeAfterProperty().set(null);
    }

    /* Create a connection between two data ports */
    public boolean connectDataPorts(DiagramNode source, DataPort sourcePort,
                                    DiagramNode target, DataPort targetPort) {

        /* The source and target port cannot be both inputs or both outputs */
        if(sourcePort.inputPort() == targetPort.inputPort())
            return false;

        if(canConnectExecutionPath(source, target) && canConnectDataPorts(source, target)) {
            source.dataConnectionsProperty().put(sourcePort, new Pair<>(target, targetPort));
            target.dataConnectionsProperty().put(targetPort, new Pair<>(source, sourcePort));
            return true;
        }
        return false;
    }

    /* Removes a connection between two data ports provided one of them */
    public void disconnectDataPort(DiagramNode source, DataPort sourcePort) {
        Pair<DiagramNode, DataPort> pair = source.dataConnectionsProperty().remove(sourcePort);
        if(pair != null)
            pair.key().dataConnectionsProperty().remove(pair.value());
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

            /* Add connected flow nodes that haven't already been visited */
            PropertyHelper.whenPresentForEach(
                    List.of(currentNode.executionPathNodeBeforeProperty(), currentNode.executionPathNodeAfterProperty()),
                    node -> {
                        if(!visitedNodes.contains(node))
                            nodesToVisit.add(node);
                    });
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

            for(Pair<DiagramNode, DataPort> connectedNode : currentNode.dataConnectionsProperty().values()) {
                if(!visitedNodes.contains(connectedNode.key())) {
                    nodesToVisit.add(connectedNode.key());
                }
            }
        }

        /* No cycle was detected, this connection is valid */
        return true;
    }

    public Set<Executable> getExecutablesWithinDiagram() {
        /* Turn the list of nodes into a set of executables */
        Set<Executable> executables = new HashSet<>();
        for (DiagramNode node : nodes)
            executables.add(node.getExecutable());

        return executables;
    }

    public List<Pair<DiagramNode, DataPort>> getDataPortsWithoutConnections() {
        List<Pair<DiagramNode, DataPort>> unconnectedPorts = new ArrayList<>();

        for(DiagramNode node : nodes) {
            Executable exe = node.getExecutable();

            /* Check input ports */
            for(DataPort inputPort : exe.getInputs()) {
                if(!node.dataConnectionsProperty().containsKey(inputPort)) {
                    unconnectedPorts.add(new Pair<>(node, inputPort));
                }
            }

            /* Check output ports */
            for(DataPort outputPort : exe.getOutputs()) {
                if(!node.dataConnectionsProperty().containsKey(outputPort)) {
                    unconnectedPorts.add(new Pair<>(node, outputPort));
                }
            }
        }

        return unconnectedPorts;
    }
}
