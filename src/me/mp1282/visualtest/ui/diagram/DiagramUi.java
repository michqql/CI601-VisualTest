package me.mp1282.visualtest.ui.diagram;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.ui.diagram.helper.ConnectionHelper;
import me.mp1282.visualtest.ui.diagram.helper.KeyboardHelper;
import me.mp1282.visualtest.ui.diagram.helper.SelectionHelper;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeHolderUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeInfoUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.diagram.port.*;
import me.mp1282.visualtest.ui.event.DataPortMouseEvent;
import me.mp1282.visualtest.ui.event.ExecutableBodyMouseEvent;
import me.mp1282.visualtest.ui.other.ISelectableUi;
import me.mp1282.visualtest.util.*;

import java.util.*;

/*
 * The DiagramUi is responsible for managing DiagramNodeUis. This includes handling events for them.
 */
public class DiagramUi extends Pane {

    private final Diagram diagram;
    /* Helpers */
    private final SelectionHelper selectionHelper;
    private final ConnectionHelper connectionHelper;

    private final DoubleProperty translateX;
    private final DoubleProperty translateY;

    /* UI elements that aren't nodes and connected lines*/
    private final Canvas gridCanvas;
    /* Child UI element variables (nodes & connectors) */
    private final DiagramNodeHolderUi nodeHolderUi;
    private final ConnectorHolderUi connectorHolderUi;

    public DiagramUi(final Diagram diagram) {
        this.diagram = diagram;
        /* Helpers */
        this.selectionHelper = new SelectionHelper();
        this.connectionHelper = new ConnectionHelper(this);

        /* Diagram state */
        this.translateX = new SimpleDoubleProperty();
        this.translateY = new SimpleDoubleProperty();

        /* Other UI elements */
        this.gridCanvas = new Canvas();
        this.nodeHolderUi = new DiagramNodeHolderUi(diagram,
                this::onDiagramNodeUiAdd, null);
        this.connectorHolderUi = new ConnectorHolderUi(this, nodeHolderUi,
                this::onDataPortConnectorUiAdd, this::onExecutionPathConnectorUiAdd);

        connectionHelper.setConnectorHolderUi(connectorHolderUi);

        /* Ensure the canvas cannot receive mouse events */
        gridCanvas.setMouseTransparent(true);

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
        translateX.bindBidirectional(diagram.translateXProperty());
        translateY.bindBidirectional(diagram.translateYProperty());

        /* Add event listeners to handle mouse events
         *
         * Using event filter here to process the mouse move event before the ExecutableBackedUi
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

        getChildren().addAll(
                gridCanvas,
                nodeHolderUi,
                connectorHolderUi,
                connectionHelper.getDataPortConnectionLine(),
                connectionHelper.getExecutionPathConnectionLine(),
                infoUi
        );

        /* Must rebuild connectors from the start in case the diagram was loaded with connections */
        connectorHolderUi.rebuildConnectors();
    }

    public Diagram getDiagram() {
        return diagram;
    }

    private void onDiagramNodeUiAdd(DiagramNodeUi ui) {
        /* Add event listeners to the component. */
        ui.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDraggedForNode);

        /* When a data port is clicked, call into the connection helper to handle connecting data ports */
        ui.addEventHandler(DataPortMouseEvent.CLICK, connectionHelper::onDataPortComponentClickEvent);
        /* Must use PRESSED over CLICK because:
         * When CLICK => Primary Mouse Button Down = false
         * When PRESS => Primary Mouse Button Down = true
         */
        ui.addEventHandler(ExecutableBodyMouseEvent.PRESSED, this::handleMousePressedForNode);

        /* Don't need to bind or set layoutX/Y here as the ExecutableBackedUi
         * class already binds these properties to the DiagramNode properties.
         */

        /* Context Menu */
        ContextMenu contextMenu = new ContextMenu();
        ui.setContextMenu(contextMenu);

        /* If the Executable that this node is wrapping has no outputs,
         * add a MenuItem to specify the execution path.
         */
        if(ui.getNode().getExecutable().getNumberOfReturnValues() == 0) {
            MenuItem executionPathItem = new MenuItem("Specify 'Execution Path'");
            executionPathItem.setOnAction(_ -> connectionHelper.handleExecutionPathConnecting(ui, true));

            contextMenu.getItems().add(executionPathItem);
        }
    }

    private void onDataPortConnectorUiAdd(DataPortConnectorLineUi connector) {
        connector.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressedForConnector);
    }

    private void onExecutionPathConnectorUiAdd(ExecutionPathConnectorLineUi connector) {
        connector.setMouseClickConsumer(this::handleMousePressedForConnector);
    }

    /* Handles the user clicking in an empty area.
     * Needed to set the initial mouse position before the user starts dragging,
     * so that the initial diff value is not massively inaccurate.
     * Handles for both empty area and on UI components.
     */
    private void handleMouseClickInEmptyArea(MouseEvent e) {
        MouseDelta.updatePosition(e);

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
            double deltaX = e.getSceneX() - MouseDelta.lastSceneMouseX.get();
            double deltaY = e.getSceneY() - MouseDelta.lastSceneMouseY.get();
            MouseDelta.updatePosition(e);

            translateX.set(translateX.get() + deltaX);
            translateY.set(translateY.get() + deltaY);

            nodeHolderUi.translate(deltaX, deltaY);

            redrawGridCanvas();
            connectionHelper.redrawConnectingLine();

            e.consume();
        }
    }

    /* Handles the user moving the mouse within the Diagram ui */
    private void handleMouseMoveInEmptyArea(MouseEvent e) {
        MouseDelta.updatePosition(e);

        /* This function is called from the 'parent' space (DiagramUi),
         * so no need to convert from local to parent coordinates.
         */
        connectionHelper.redrawConnectingLine();
    }

    /* Handles the user clicking on a component */
    private void handleMousePressedForNode(ExecutableBodyMouseEvent exeEvent) {
        final MouseEvent wrappedMouseEvent = exeEvent.getWrappedMouseEvent();
        MouseDelta.updatePosition(wrappedMouseEvent);

        /* Checks if a diagram node was clicked */
        if(exeEvent.getSource() instanceof DiagramNodeUi ui) {
            /* See if the execution path can be connected - ONLY if the primary mouse button was pressed */
            if(wrappedMouseEvent.isPrimaryButtonDown() && connectionHelper.handleExecutionPathConnecting(ui, false)) {
                exeEvent.consume();
                return;
            }

            /* Perform last:
             * Try to handle selecting a UI element - ONLY if the primary mouse button was pressed
             */
            if(wrappedMouseEvent.isPrimaryButtonDown())
                selectionHelper.handleSelection(ui, wrappedMouseEvent.isShiftDown());
        }
    }

    private void handleMouseDraggedForNode(MouseEvent e) {
        if(e.getSource() instanceof DiagramNodeUi ui) {
            /* Only allow translation if the primary mouse button is down and
             * the user is not hovering over a data port
             */
            if(e.isPrimaryButtonDown() && ui.hoveredDataPortProperty().get() == null) {
                final double deltaX = e.getSceneX() - MouseDelta.lastSceneMouseX.get();
                final double deltaY = e.getSceneY() - MouseDelta.lastSceneMouseY.get();

                MouseDelta.updatePosition(e);

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
                    final DiagramNode node = new DiagramNode(exe);
                    diagram.addDiagramNode(node);
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

        double spacingPx = 20; /* grid spacing in pixels */
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);

        double startX = translateX.get() % spacingPx;
        double startY = translateY.get() % spacingPx;

        /* Vertical lines */
        for (double x = startX; x < getWidth(); x += spacingPx) {
            gc.strokeLine(x, 0, x, getHeight());
        }
        /* Horizontal lines */
        for (double y = startY; y < getHeight(); y += spacingPx) {
            gc.strokeLine(0, y, getWidth(), y);
        }
    }

    private void deleteSelected() {
        final ObservableList<ISelectableUi> selected = selectionHelper.getList();
        /* Rather than removing nodes from the diagram one by one, add them to this list and
         * remove them all at once. This results in 1 change fire, rather than several.
         */
        final Collection<DiagramNode> nodesToRemove = new ArrayList<>();

        for (final ISelectableUi ui : selected) {
            if(ui instanceof DiagramNodeUi nodeUi) {
                nodesToRemove.add(nodeUi.getNode());

            } else if(ui instanceof DataPortConnectorLineUi lineUi) {
                /* Calling the disconnect logic on the source port will disconnect the target port too */
                diagram.disconnectDataPort(lineUi.getOutput());

            } else if(ui instanceof ExecutionPathConnectorLineUi lineUi) {
                /* Calling the disconnect logic on the source port will disconnect the target port too */
                diagram.disconnectExecutionPath(lineUi.getSourceNodeUi().getNode());

            }
        }

        selected.clear();

        /* Updates the node model, which in turn will update the DiagramNodeHolderUi */
        diagram.removeDiagramNodes(nodesToRemove);

        /* Call into the ConnectorHolderUi to rebuild the connections */
        connectorHolderUi.rebuildConnectors();
    }
}
