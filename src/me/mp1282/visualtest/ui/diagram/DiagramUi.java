package me.mp1282.visualtest.ui.diagram;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.ui.diagram.helper.KeyboardHelper;
import me.mp1282.visualtest.ui.diagram.helper.SelectionHelper;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeInfoUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.diagram.port.DataPortArea;
import me.mp1282.visualtest.ui.diagram.port.DataPortConnectorLineUi;
import me.mp1282.visualtest.ui.executable.ExecutableUi;
import me.mp1282.visualtest.ui.other.ISelectableUi;
import me.mp1282.visualtest.util.DragContext;
import me.mp1282.visualtest.util.Pair;

import java.util.List;

/*
 * The DiagramUi is responsible for managing DiagramNodeUis. This includes handling events for them.
 */
public class DiagramUi extends Pane {

    private final Diagram diagram;

    private final DoubleProperty translateX;
    private final DoubleProperty translateY;

    /* Mouse positions */
    private double lastSceneMouseX;
    private double lastSceneMouseY;
    private double lastMouseX;
    private double lastMouseY;

    /* Variables to handle connecting of data/flow ports together */
    private final ObjectProperty<Pair<DiagramNodeUi, DataPortArea>> sourcePortPair;
    private final ObjectProperty<DiagramNodeUi> sourceFlowPort;

    /* Helpers */
    private final SelectionHelper selectionHelper;
    private final KeyboardHelper keyboardHelper;

    /* UI elements that aren't nodes and connected lines*/
    private final Canvas gridCanvas;
    private final Line connectingLine;
    private final DiagramNodeInfoUi infoUi;

    public DiagramUi(Diagram diagram) {
        this.diagram = diagram;
        this.translateX = new SimpleDoubleProperty();
        this.translateY = new SimpleDoubleProperty();
        this.sourcePortPair = new SimpleObjectProperty<>();
        this.sourceFlowPort = new SimpleObjectProperty<>();
        this.selectionHelper = new SelectionHelper();
        this.keyboardHelper = new KeyboardHelper(this);
        this.gridCanvas = new Canvas();
        this.connectingLine = new Line();
        this.infoUi = new DiagramNodeInfoUi(selectionHelper.getList());

        gridCanvas.setMouseTransparent(true);

        /* Add event listeners to the size of this UI component
         * that redraws the grid lines
         */
        widthProperty().addListener(e -> redrawGridCanvas());
        heightProperty().addListener(e -> redrawGridCanvas());
        redrawGridCanvas();

        /* Ensure connecting line is not visible */
        connectingLine.setVisible(false);
        sourcePortPair.addListener((_, _, newVal) ->
                handleSourceDataPortChange(newVal));

        /* Ensure the diagram node info UI is on the right side of this UI */
        infoUi.layoutXProperty().bind(widthProperty().subtract(infoUi.widthProperty()).subtract(10));
        infoUi.layoutYProperty().set(10);

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
        keyboardHelper.addKeyHandler(keyEvent -> keyEvent.getCode() == KeyCode.DELETE, this::deleteSelected);

        getChildren().addAll(gridCanvas, connectingLine, infoUi);
    }

    private void handleDiagramNodeChange(ListChangeListener.Change<? extends DiagramNode> change) {
        while(change.next()) {
            /* Add UI components for any new diagram nodes */
            for (DiagramNode added : change.getAddedSubList())
                createDiagramNodeUi(added);

            /* TODO: Handle removed items */
        }
    }

    private void handleSourceDataPortChange(Pair<DiagramNodeUi, DataPortArea> pair) {
        /* Redraw line first before setting visibility to remove visual flicker */
        redrawConnectingLine();
        connectingLine.setVisible(pair != null);
    }

    /* Handles the user clicking in an empty area.
     * Needed to set the initial mouse position before the user starts dragging,
     * so that the initial diff value is not massively inaccurate.
     * Handles for both empty area and on UI components.
     */
    private void handleMouseClickInEmptyArea(MouseEvent e) {
        lastSceneMouseX = e.getSceneX();
        lastSceneMouseY = e.getSceneY();
        lastMouseX      = e.getX();
        lastMouseY      = e.getY();

        if(e.isPrimaryButtonDown()) {
            /* If the user is currently connecting data ports, cancel this action */
            sourcePortPair.set(null);
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
            lastMouseX      = e.getX();
            lastMouseY      = e.getY();

            translateX.set(translateX.get() + deltaX);
            translateY.set(translateY.get() + deltaY);

            /* Translate all UI components by new translate X and Y */
            for (Node node : getChildren()) {
                if (node instanceof DiagramNodeUi || node instanceof DataPortConnectorLineUi) {
                    node.setTranslateX(node.getTranslateX() + deltaX);
                    node.setTranslateY(node.getTranslateY() + deltaY);
                }
            }

            redrawGridCanvas();
            redrawConnectingLine();

            e.consume();
        }
    }

    /* Handles the user moving the mouse within the Diagram ui */
    private void handleMouseMoveInEmptyArea(MouseEvent e) {
        lastSceneMouseX = e.getSceneX();
        lastSceneMouseY = e.getSceneY();
        lastMouseX      = e.getX();
        lastMouseY      = e.getY();

        /* This function is called from the 'parent' space (DiagramUi),
         * so no need to convert from local to parent coordinates.
         */
        redrawConnectingLine();
    }

    /* Handles the user clicking on a component */
    private void handleMousePressedForNode(MouseEvent e) {
        lastSceneMouseX = e.getSceneX();
        lastSceneMouseY = e.getSceneY();
        /* Need to translate e.getX/Y() to parent coordinates,
         * as lastMouseX/Y are in parent space
         */
        Point2D pos = ((Node) e.getSource()).localToParent(e.getX(), e.getY());
        lastMouseX = pos.getX();
        lastMouseY = pos.getY();

        /* Checks if a diagram node was clicked */
        if(e.getSource() instanceof DiagramNodeUi ui) {
            /* Checks if a data port was clicked */
            final DataPortArea hoveredPort = ui.hoveredDataPortProperty().get();
            if(hoveredPort != null) {
                /* If no current source data port is set, set this data port as the source.
                 * Otherwise, handle 'connecting' the two data ports together.
                 */
                final Pair<DiagramNodeUi, DataPortArea> sourcePair = sourcePortPair.get();
                if (sourcePair == null) {
                    sourcePortPair.set(new Pair<>(ui, ui.hoveredDataPortProperty().get()));
                } else {
                    boolean connectionValid = diagram.connectDataPorts(
                            /* Source Node => */ sourcePair.key().getNode(),
                            /* Source Port => */ sourcePair.value().getDataPort(),
                            /* Target Node => */ ui.getNode(),
                            /* Target Port => */ hoveredPort.getDataPort());

                    if (connectionValid) {
                        createDataPortConnectorUi(sourcePair.key(), sourcePair.value(), ui, hoveredPort);

                        /* Set source variables back to null as they are no longer needed */
                        sourcePortPair.set(null);
                    }
                }
            } else {
                if (e.isPrimaryButtonDown())
                    selectionHelper.handleSelection(ui, e.isShiftDown());
            }
        }
    }

    private void handleMouseMoveForNode(MouseEvent e) {
        lastSceneMouseX = e.getSceneX();
        lastSceneMouseY = e.getSceneY();
        /* Need to translate e.getX/Y() to parent coordinates,
         * as lastMouseX/Y are in parent space
         */
        Point2D pos = ((Node) e.getSource()).localToParent(e.getX(), e.getY());
        lastMouseX = pos.getX();
        lastMouseY = pos.getY();

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
                lastMouseX = pos.getX();
                lastMouseY = pos.getY();

                ui.layoutXProperty().set(ui.layoutXProperty().get() + deltaX);
                ui.layoutYProperty().set(ui.layoutYProperty().get() + deltaY);

                final Pair<DiagramNodeUi, DataPortArea> sourcePair = sourcePortPair.get();
                if(sourcePair != null && sourcePair.key().equals(ui)) {
                    redrawConnectingLine();
                }

                e.consume();
            }
        }
    }

    private void handleMousePressedForConnector(MouseEvent e) {
        if(e.getSource() instanceof DataPortConnectorLineUi ui) {
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

    private void redrawConnectingLine() {
        final Pair<DiagramNodeUi, DataPortArea> sourcePair = sourcePortPair.get();
        if(sourcePair != null) {
            /* Draw line between source data port and mouse cursor */
            connectingLine.setStartX(sourcePair.key().getLayoutX() +
                    sourcePair.key().getTranslateX() + sourcePair.value().getMidX());
            connectingLine.setStartY(sourcePair.key().getLayoutY() +
                    sourcePair.key().getTranslateY() + sourcePair.value().getMidY());
            connectingLine.setEndX(lastMouseX);
            connectingLine.setEndY(lastMouseY);
        }
    }

    private void createDiagramNodeUi(DiagramNode node) {
        final ExecutableUi ui = new DiagramNodeUi(node);
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

        getChildren().add(ui);
    }

    private void createDataPortConnectorUi(DiagramNodeUi sourceNodeUi, DataPortArea sourcePort,
                                           DiagramNodeUi targetNodeUi, DataPortArea targetPort) {
        final DataPortConnectorLineUi connector = new DataPortConnectorLineUi(
                sourceNodeUi, sourcePort, targetNodeUi, targetPort);
        connector.setFocusTraversable(true); /* Allows this UI element to handle key events */
        connector.setTranslateX(translateX.get());
        connector.setTranslateY(translateY.get());

        /* Add event handlers */
        connector.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressedForConnector);

        getChildren().add(connector);
    }

    private void deleteSelected() {
        ObservableList<ISelectableUi> selected = selectionHelper.getList();
        for (ISelectableUi ui : selected) {
            if(ui instanceof DiagramNodeUi nodeUi) {
                DiagramNode node = nodeUi.getNode();
                node.dataConnectionsProperty().keySet().forEach(port -> diagram.disconnectDataPort(node, port));
            } else if(ui instanceof DataPortConnectorLineUi lineUi) {
                diagram.disconnectDataPort(lineUi.getSourceNodeUi().getNode(), lineUi.getSourcePort().getDataPort());
            }

            getChildren().remove(ui);
        }

        selected.clear();
    }
}
