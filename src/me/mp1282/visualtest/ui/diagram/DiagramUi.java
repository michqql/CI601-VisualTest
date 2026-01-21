package me.mp1282.visualtest.ui.diagram;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.ui.diagram.helper.ConnectionHelper;
import me.mp1282.visualtest.ui.diagram.helper.KeyboardHelper;
import me.mp1282.visualtest.ui.diagram.helper.SelectionHelper;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeInfoUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.diagram.port.DataPortArea;
import me.mp1282.visualtest.ui.diagram.port.DataPortConnectorLineUi;
import me.mp1282.visualtest.ui.diagram.port.ExecutionPathConnectorLineUi;
import me.mp1282.visualtest.ui.diagram.port.IConnectorUi;
import me.mp1282.visualtest.ui.other.ISelectableUi;
import me.mp1282.visualtest.util.DragContext;
import me.mp1282.visualtest.util.PropertyHelper;

import java.util.*;

/*
 * The DiagramUi is responsible for managing DiagramNodeUis. This includes handling events for them.
 */
public class DiagramUi extends Pane {

    private final Diagram diagram;

    /* Child UI element variables (nodes & connectors) */
    private final Map<DiagramNode, DiagramNodeUi> nodeToUiMap;
    private final Set<DataPortConnectorLineUi> dataPortConnectors;
    private final Set<ExecutionPathConnectorLineUi> executionPathConnectors;

    private final DoubleProperty translateX;
    private final DoubleProperty translateY;

    /* Mouse positions */
    private double lastSceneMouseX;
    private double lastSceneMouseY;
    /* TODO: Change this from public to private and introduce a mediator */
    public final DoubleProperty lastMouseX;
    public final DoubleProperty lastMouseY;

    /* Helpers */
    private final SelectionHelper selectionHelper;
    private final ConnectionHelper connectionHelper;

    /* UI elements that aren't nodes and connected lines*/
    private final Canvas gridCanvas;

    public DiagramUi(Diagram diagram) {
        this.diagram = diagram;
        /* Main UI elements */
        this.nodeToUiMap = new HashMap<>();
        this.dataPortConnectors = new HashSet<>();
        this.executionPathConnectors = new HashSet<>();
        /* Diagram state */
        this.translateX = new SimpleDoubleProperty();
        this.translateY = new SimpleDoubleProperty();
        /* Mouse positions */
        this.lastMouseX = new SimpleDoubleProperty();
        this.lastMouseY = new SimpleDoubleProperty();
        /* Helpers */
        this.selectionHelper = new SelectionHelper();
        this.connectionHelper = new ConnectionHelper(this);
        /* Other UI elements */
        this.gridCanvas = new Canvas();

        /* Ensure the canvas cannot receive mouse events */
        gridCanvas.setMouseTransparent(true);
        /* Ensure connecting line is not visible initially */

        /* Ensure the diagram node info UI is on the right side of this UI */
        final DiagramNodeInfoUi infoUi = new DiagramNodeInfoUi(selectionHelper.getList());
        infoUi.layoutXProperty().bind(widthProperty().subtract(infoUi.widthProperty()).subtract(10));
        infoUi.layoutYProperty().set(10);

        /* Add event listeners to the size of this UI component
         * that redraws the grid lines
         */
        PropertyHelper.addListenerForEach(List.of(widthProperty(), heightProperty()), _ -> redrawGridCanvas());
        redrawGridCanvas();

        /* Bind translate properties to diagram properties */
        diagram.nodesProperty().addListener((ListChangeListener<? super DiagramNode>) this::handleDiagramNodeChange);
        diagram.translateXProperty().bind(translateX);
        diagram.translateYProperty().bind(translateY);

        /* Add event listeners to handle mouse events */
        /* Using event filter here to process the mouse move event before the ExecutableBackedUi
         * class gets to process the event, otherwise this method does not get called.
         */
        addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMouseClickInEmptyArea);
        addEventFilter (MouseEvent.MOUSE_DRAGGED, this::handleMouseDraggedInEmptyArea);
        addEventFilter (MouseEvent.MOUSE_MOVED,   this::handleMouseMoveInEmptyArea);

        /* Add event listeners to handle executable UI elements
         * being dragged over the diagram UI. When they are dragged over
         * the drag over event confirms whether the user can drop.
         * The dropped event handles the placement of the executable
         * component into the diagram.
         */
        setOnDragOver(this::handleDragOver);
        setOnDragDropped(this::handleDragDrop);

        /* Add key event handlers */
        final KeyboardHelper keyboardHelper = new KeyboardHelper(this);
        keyboardHelper.addKeyHandler(keyEvent -> keyEvent.getCode() == KeyCode.DELETE, this::deleteSelected);

        getChildren().addAll(gridCanvas, connectionHelper.getTempConnectionLine(), infoUi);
    }

    public Diagram getDiagram() {
        return diagram;
    }

    private void handleDiagramNodeChange(ListChangeListener.Change<? extends DiagramNode> change) {
        while(change.next()) {
            /* Add UI components for any new diagram nodes */
            for (DiagramNode added : change.getAddedSubList())
                createDiagramNodeUi(added);

            /* TODO: Handle removed items */
        }
    }

    /* Handles the user clicking in an empty area.
     * Needed to set the initial mouse position before the user starts dragging,
     * so that the initial diff value is not massively inaccurate.
     * Handles for both empty area and on UI components.
     */
    private void handleMouseClickInEmptyArea(MouseEvent e) {
        lastSceneMouseX = e.getSceneX();
        lastSceneMouseY = e.getSceneY();
        lastMouseX.set(e.getX());
        lastMouseY.set(e.getY());

        if(e.isPrimaryButtonDown()) {
            connectionHelper.cancelConnection();
            selectionHelper.clear();
        }
    }

    /* Handles the user dragging in an empty area (not an executable or other UI component)
     * Should translate the background grid and other UI components.
     */
    private void handleMouseDraggedInEmptyArea(MouseEvent e) {
        /* Only translate when middle mouse button is down */
        if(e.isMiddleButtonDown()) {
            double deltaX = e.getSceneX() - lastSceneMouseX;
            double deltaY = e.getSceneY() - lastSceneMouseY;
            lastSceneMouseX = e.getSceneX();
            lastSceneMouseY = e.getSceneY();
            lastMouseX.set(e.getX());
            lastMouseY.set(e.getY());

            translateX.set(translateX.get() + deltaX);
            translateY.set(translateY.get() + deltaY);

            /* Translate all UI components by new translate X and Y */
            for (Node node : getChildren()) {
                if (node instanceof IDiagramElement) {
                    node.setTranslateX(node.getTranslateX() + deltaX);
                    node.setTranslateY(node.getTranslateY() + deltaY);
                }
            }

            redrawGridCanvas();
            connectionHelper.redrawConnectingLine();

            e.consume();
        }
    }

    /* Handles the user moving the mouse within the Diagram ui */
    private void handleMouseMoveInEmptyArea(MouseEvent e) {
        lastSceneMouseX = e.getSceneX();
        lastSceneMouseY = e.getSceneY();
        lastMouseX.set(e.getX());
        lastMouseY.set(e.getY());

        /* This function is called from the 'parent' space (DiagramUi),
         * so no need to convert from local to parent coordinates.
         */
        connectionHelper.redrawConnectingLine();
    }

    /* Handles the user clicking on a component */
    private void handleMousePressedForNode(MouseEvent e) {
        lastSceneMouseX = e.getSceneX();
        lastSceneMouseY = e.getSceneY();
        /* Need to translate e.getX/Y() to parent coordinates,
         * as lastMouseX/Y are in parent space
         */
        Point2D pos = ((Node) e.getSource()).localToParent(e.getX(), e.getY());
        lastMouseX.set(pos.getX());
        lastMouseY.set(pos.getY());

        /* Checks if a diagram node was clicked */
        if(e.getSource() instanceof DiagramNodeUi ui) {
            /* Try to handle connecting data ports - ONLY if the primary mouse button was pressed */
            if(e.isPrimaryButtonDown() && connectionHelper.handleDataPortConnecting(ui)) {
                e.consume();
                return;
            }

            /* Next, see if the execution path can be connected - ONLY if the primary mouse button was pressed */
            if(e.isPrimaryButtonDown() && connectionHelper.handleExecutionPathConnecting(ui, false)) {
                e.consume();
                return;
            }

            /* Perform last:
             * Try to handle selecting a UI element - ONLY if the primary mouse button was pressed
             */
            if(e.isPrimaryButtonDown())
                selectionHelper.handleSelection(ui, e.isShiftDown());
        }
    }

    private void handleMouseMoveForNode(MouseEvent e) {
        lastSceneMouseX = e.getSceneX();
        lastSceneMouseY = e.getSceneY();
        /* Need to translate e.getX/Y() to parent coordinates,
         * as lastMouseX/Y are in parent space
         */
        Point2D pos = ((Node) e.getSource()).localToParent(e.getX(), e.getY());
        lastMouseX.set(pos.getX());
        lastMouseY.set(pos.getY());

        if(e.getSource() instanceof DiagramNodeUi ui) {
            /* For each data port, check if the user is currently hovering,
             * if so set the hovered property.
             */
            for (DataPortArea area : ui.getCachedPortAreas()) {
                if (area.isInside(e.getX(), e.getY())) {
                    ui.hoveredDataPortProperty().set(area);
                    return;
                }
            }

            /* No data port is being hovered if the code has reached here,
             * thus set the hovered property to null
             */
            ui.hoveredDataPortProperty().set(null);
        }
    }

    private void handleMouseDraggedForNode(MouseEvent e) {
        if(e.getSource() instanceof DiagramNodeUi ui) {
            /* Only allow translation if the primary mouse button is down and
             * the user is not hovering over a data port
             */
            if(e.isPrimaryButtonDown() && ui.hoveredDataPortProperty().get() == null) {
                final double deltaX = e.getSceneX() - lastSceneMouseX;
                final double deltaY = e.getSceneY() - lastSceneMouseY;

                lastSceneMouseX = e.getSceneX();
                lastSceneMouseY = e.getSceneY();
                /* Need to translate e.getX/Y() to parent coordinates,
                 * as lastMouseX/Y are in parent space
                 */
                Point2D pos = ((Node) e.getSource()).localToParent(e.getX(), e.getY());
                lastMouseX.set(pos.getX());
                lastMouseY.set(pos.getY());

                ui.layoutXProperty().set(ui.layoutXProperty().get() + deltaX);
                ui.layoutYProperty().set(ui.layoutYProperty().get() + deltaY);

                /* Redraw the connecting line in case the UI node dragged was the one being connected */
                connectionHelper.redrawConnectingLine();

                e.consume();
            }
        }
    }

    private void handleMousePressedForConnector(MouseEvent e) {
        if(e.getSource() instanceof IConnectorUi ui) {
            if (e.isPrimaryButtonDown())
                selectionHelper.handleSelection(ui, e.isShiftDown());

            e.consume();
        }
    }

    /* Handles the user dragging something over the diagram UI.
     * Needs to check if the thing being dragged is an executable UI
     * component (by checking if a data transfer object is present)
     * and telling the UI system that this is an accepted item.
     */
    private void handleDragOver(DragEvent e) {
        /* If this is an executable being dragged, allow it to be dropped here */
        if(e.getDragboard().hasContent(Executable.DTO_DATA_FORMAT))
            e.acceptTransferModes(TransferMode.MOVE);
    }

    /* Handles the user dropping a dragged item into the diagram UI.
     * Only accepts a data transfer object for an executable UI.
     * Places the exe UI into the diagram.
     */
    private void handleDragDrop(DragEvent e) {
        Dragboard db = e.getDragboard();
        if(db.hasContent(Executable.DTO_DATA_FORMAT)) {
            /* Retrieve the executable from the DragContext */
            if(DragContext.getObject() instanceof Executable exe) {
                try {
                    final DiagramNode node = diagram.placeExecutable(exe);
                    node.xProperty().set(e.getX());
                    node.yProperty().set(e.getY());
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.err.println("Error: Dragged object is not an Executable");
            }
        }
    }

    private void redrawGridCanvas() {
        gridCanvas.setWidth(getWidth());
        gridCanvas.setHeight(getHeight());

        GraphicsContext gc = gridCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        double spacing = 20; // grid spacing in pixels
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);

        double startX = translateX.get() % spacing;
        double startY = translateY.get() % spacing;

        // vertical lines
        for (double x = startX; x < getWidth(); x += spacing) {
            gc.strokeLine(x, 0, x, getHeight());
        }
        // horizontal lines
        for (double y = startY; y < getHeight(); y += spacing) {
            gc.strokeLine(0, y, getWidth(), y);
        }
    }

    private void createDiagramNodeUi(DiagramNode node) {
        final DiagramNodeUi ui = new DiagramNodeUi(node);

        /* Add event listeners to the component.
         *
         * Can't use mouseClicked for this component because it doesn't
         * get called for some reason, but mousePressed does.
         */
        ui.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressedForNode);
        ui.addEventHandler(MouseEvent.MOUSE_MOVED,   this::handleMouseMoveForNode);
        ui.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDraggedForNode);

        /* Don't need to bind or set layoutX/Y here as the ExecutableBackedUi
         * class already binds these properties to the DiagramNode properties.
         */

        /* Context Menu */
        ContextMenu contextMenu = new ContextMenu();
        ui.setContextMenu(contextMenu);

        /* If the Executable that this node is wrapping has no outputs,
         * add a MenuItem to specify the execution path.
         */
        if(node.getExecutable().getNumberOfOutputs() == 0) {
            MenuItem executionPathItem = new MenuItem("Specify 'Execution Path'");
            /* TODO: disable this menu item if it doesn't make sense as an action the user can take */
            executionPathItem.setOnAction(_ -> connectionHelper.handleExecutionPathConnecting(ui, true));

            contextMenu.getItems().add(executionPathItem);
        }

        getChildren().add(ui);
        sortChildrenByZOrder();
        nodeToUiMap.put(node, ui);
    }

    private void createDataPortConnectorUi(DiagramNodeUi sourceNodeUi, DataPortArea sourcePort,
                                           DiagramNodeUi targetNodeUi, DataPortArea targetPort) {
        /* Check if there is already a UI element for this connection */
        for (DataPortConnectorLineUi ui : dataPortConnectors) {
            if(ui.equals(sourceNodeUi, sourcePort, targetNodeUi, targetPort))
                return;
        }

        final DataPortConnectorLineUi connector = new DataPortConnectorLineUi(
                sourceNodeUi, sourcePort, targetNodeUi, targetPort);
        connector.setTranslateX(translateX.get());
        connector.setTranslateY(translateY.get());

        /* Add event handlers */
        connector.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressedForConnector);

        getChildren().add(connector);
        sortChildrenByZOrder();
        dataPortConnectors.add(connector);
    }

    private void createExecutionPathConnectorUi(DiagramNodeUi before, DiagramNodeUi after) {
        /* Check if there is already a UI element for this connection */
        for(ExecutionPathConnectorLineUi ui : executionPathConnectors) {
            if(ui.equals(before, after))
                return;
        }

        final ExecutionPathConnectorLineUi connector = new ExecutionPathConnectorLineUi(before, after);
        connector.setTranslateX(translateX.get());
        connector.setTranslateY(translateY.get());

        /* Add event handlers */
        connector.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressedForConnector);

        getChildren().add(connector);
        sortChildrenByZOrder();
        executionPathConnectors.add(connector);
    }

    public void rebuildDataPortConnections() {
        /* Firstly remove all current data port connector UI elements */
        getChildren().removeIf(DataPortConnectorLineUi.class::isInstance);
        dataPortConnectors.clear();

        /* Loop over each DiagramNode in the Diagram */
        for (DiagramNode node : diagram.nodesProperty()) {
            final DiagramNodeUi sourceNodeUi = nodeToUiMap.get(node);
            if(sourceNodeUi == null) /* TODO: If this is null we have a serious problem */
                throw new RuntimeException("DiagramNode does not have a DiagramNodeUi when rebuilding data ports! (A)");

            /* Loop over each data port connection for this node */
            node.dataConnectionsProperty().forEach((port, pair) -> {
                final DiagramNodeUi targetNodeUi = nodeToUiMap.get(pair.key());
                if(targetNodeUi == null)
                    throw new RuntimeException("DiagramNode does not have a DiagramNodeUi when rebuilding data ports! (B)");

                final DataPortArea sourceArea = sourceNodeUi.getAreaFromDataPort(port);
                if(sourceArea == null)
                    throw new RuntimeException("DiagramNodeUi does not have a DataPortArea when rebuilding data ports! (A)");

                final DataPortArea targetArea = targetNodeUi.getAreaFromDataPort(pair.value());
                if(targetArea == null)
                    throw new RuntimeException("DiagramNodeUi does not have a DataPortArea when rebuilding data ports! (B)");

                /* Create the connection UI */
                createDataPortConnectorUi(sourceNodeUi, sourceArea, targetNodeUi, targetArea);
            });
        }
    }

    public void rebuildExecutionPathConnections() {
        /* Firstly remove all current data port connector UI elements */
        getChildren().removeIf(ExecutionPathConnectorLineUi.class::isInstance);
        executionPathConnectors.clear();

        /* Loop over each DiagramNode in the Diagram */
        for (DiagramNode node : diagram.nodesProperty()) {
            final DiagramNodeUi currentNodeUi = nodeToUiMap.get(node);
            if(currentNodeUi == null) /* TODO: If this is null we have a serious problem */
                throw new RuntimeException("DiagramNode does not have a DiagramNodeUi when rebuilding execution path! (A)");

            final DiagramNode before = node.executionPathNodeBeforeProperty().get();
            if(before != null) {
                final DiagramNodeUi beforeNodeUi = nodeToUiMap.get(before);
                if(beforeNodeUi != null)
                    createExecutionPathConnectorUi(beforeNodeUi, currentNodeUi);
            }

            final DiagramNode after = node.executionPathNodeAfterProperty().get();
            if(after != null) {
                final DiagramNodeUi afterNodeUi = nodeToUiMap.get(after);
                if(afterNodeUi != null)
                    createExecutionPathConnectorUi(currentNodeUi, afterNodeUi);
            }
        }
    }

    private void deleteSelected() {
        ObservableList<ISelectableUi> selected = selectionHelper.getList();
        for (ISelectableUi ui : selected) {
            if(ui instanceof DiagramNodeUi nodeUi) {
                DiagramNode node = nodeUi.getNode();

                /* Remove the data port connections to this node
                 * (node, port '_') -> (pair.key, pair.value)
                 * (pair.key, pair.value) -> (node, port)
                 *
                 * Thus, performing pair.key remove pair.value effectively removes (node, port).
                 *
                 * Cannot use diagram.disconnectDataPorts here as that would cause a ConcurrentModificationException
                 * if the number of connections is greater than 1.
                 */
                node.dataConnectionsProperty().forEach((_, pair) -> {
                    pair.key().dataConnectionsProperty().remove(pair.value());
                });

                diagram.nodesProperty().remove(node);
                nodeToUiMap.remove(node);

            } else if(ui instanceof DataPortConnectorLineUi lineUi) {
                /* Calling the disconnect logic on the source port will disconnect the target port too */
                diagram.disconnectDataPort(lineUi.getSourceNodeUi().getNode(), lineUi.getSourcePort().getDataPort());

            } else if(ui instanceof ExecutionPathConnectorLineUi lineUi) {
                /* Calling the disconnect logic on the source port will disconnect the target port too */
                diagram.disconnectExecutionPath(lineUi.getSourceNodeUi().getNode());

            }

            getChildren().remove(ui);
        }

        selected.clear();
        rebuildDataPortConnections();
        rebuildExecutionPathConnections();
    }

    private void sortChildrenByZOrder() {
        FXCollections.sort(getChildren(), (a, b) -> {
            /* Return negative if a should be BELOW b,
             * return positive if a should be ABOVE b
             */
            int priorityA = a instanceof IDiagramElement ui ? ui.getZOrder() : 100;
            int priorityB = b instanceof IDiagramElement ui ? ui.getZOrder() : 100;

            return priorityB - priorityA;
        });
    }
}
